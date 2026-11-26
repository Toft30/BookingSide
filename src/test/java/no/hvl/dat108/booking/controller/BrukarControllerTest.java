package no.hvl.dat108.booking.controller;

import no.hvl.dat108.booking.dto.InnloggaBrukar;
import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;
import no.hvl.dat108.booking.service.BrukarService;
import no.hvl.dat108.booking.service.EpostOpptekenException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * @WebMvcTest startar berre web-laget: kontrollarane, WebConfig (med interceptoren)
 * og Thymeleaf. BrukarService blir erstatta av ein Mockito-mock via @MockitoBean.
 * MockMvc sender "falske" HTTP-førespurnader utan å starte ein ekte server.
 */
@WebMvcTest({BrukarController.class, StartController.class})
class BrukarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BrukarService brukarService;

    // ---------- Registrering ----------

    @Test
    void registreringssidaErOpenUtanInnlogging() throws Exception {
        mockMvc.perform(get("/registrer"))
                .andExpect(status().isOk())
                .andExpect(view().name("registrer"))
                .andExpect(model().attributeExists("skjema"));
    }

    @Test
    void gyldigRegistreringGirRedirectMedFlash() throws Exception {
        mockMvc.perform(post("/registrer")
                        .param("namn", "Ola")
                        .param("epost", "ola@hvl.no")
                        .param("passord", "hemmeleg")
                        .param("passordRepetert", "hemmeleg"))
                .andExpect(status().is3xxRedirection())          // PRG
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("melding"));

        verify(brukarService).registrer("Ola", "ola@hvl.no", "hemmeleg");
    }

    @Test
    void ugyldigSkjemaViserSamaSidaMedFeil() throws Exception {
        mockMvc.perform(post("/registrer")
                        .param("namn", "")
                        .param("epost", "ikkje-ein-epost")
                        .param("passord", "kort")
                        .param("passordRepetert", "kort"))
                .andExpect(status().isOk())                      // ingen redirect ved feil
                .andExpect(view().name("registrer"))
                .andExpect(model().attributeHasFieldErrors("skjema", "namn", "epost", "passord"));

        verify(brukarService, never()).registrer(anyString(), anyString(), anyString());
    }

    @Test
    void ulikePassordGirFeilPaaPassordRepetert() throws Exception {
        mockMvc.perform(post("/registrer")
                        .param("namn", "Ola")
                        .param("epost", "ola@hvl.no")
                        .param("passord", "hemmeleg")
                        .param("passordRepetert", "noko-anna"))
                .andExpect(view().name("registrer"))
                .andExpect(model().attributeHasFieldErrorCode("skjema", "passordRepetert", "passord.ulik"));
    }

    @Test
    void opptekenEpostGirFeilPaaEpostfeltet() throws Exception {
        when(brukarService.registrer(anyString(), anyString(), anyString()))
                .thenThrow(new EpostOpptekenException("ola@hvl.no"));

        mockMvc.perform(post("/registrer")
                        .param("namn", "Ola")
                        .param("epost", "ola@hvl.no")
                        .param("passord", "hemmeleg")
                        .param("passordRepetert", "hemmeleg"))
                .andExpect(view().name("registrer"))
                .andExpect(model().attributeHasFieldErrorCode("skjema", "epost", "epost.oppteken"));
    }

    // ---------- Innlogging ----------

    @Test
    void feilInnloggingGirRedirectMedFeilmelding() throws Exception {
        when(brukarService.loggInn(any(), any())).thenReturn(Optional.empty());

        mockMvc.perform(post("/login").param("epost", "ola@hvl.no").param("passord", "feil"))
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("feilmelding"));
    }

    @Test
    void rettInnloggingLagrarBrukarISesjonOgByterSesjonsId() throws Exception {
        Brukar ola = new Brukar("Ola", "ola@hvl.no", "hash", "salt", Rolle.STUDENT);
        when(brukarService.loggInn("ola@hvl.no", "hemmeleg")).thenReturn(Optional.of(ola));

        MockHttpSession sesjon = new MockHttpSession();
        String gammalId = sesjon.getId();

        MvcResult resultat = mockMvc.perform(post("/login").session(sesjon)
                        .param("epost", "ola@hvl.no").param("passord", "hemmeleg"))
                .andExpect(redirectedUrl("/"))
                .andReturn();

        var nySesjon = resultat.getRequest().getSession(false);
        assertInstanceOf(InnloggaBrukar.class, nySesjon.getAttribute(InnloggaBrukar.SESJONSNOKKEL));
        assertNotEquals(gammalId, nySesjon.getId(), "sesjons-id skal bytast ved innlogging");
    }

    // ---------- Interceptor og utlogging ----------

    @Test
    void startsidaUtanInnloggingSenderTilLogin() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(redirectedUrl("/login?krevInnlogging"));
    }

    @Test
    void startsidaMedInnloggingVisastFram() throws Exception {
        MockHttpSession sesjon = innloggaSesjon();

        mockMvc.perform(get("/").session(sesjon))
                .andExpect(status().isOk())
                .andExpect(view().name("index"));
    }

    @Test
    void utloggingUgyldiggjerSesjonen() throws Exception {
        MockHttpSession sesjon = innloggaSesjon();

        mockMvc.perform(post("/logout").session(sesjon))
                .andExpect(redirectedUrl("/login"))
                .andExpect(flash().attributeExists("melding"));

        assertTrue(sesjon.isInvalid());
    }

    private MockHttpSession innloggaSesjon() {
        MockHttpSession sesjon = new MockHttpSession();
        sesjon.setAttribute(InnloggaBrukar.SESJONSNOKKEL, new InnloggaBrukar(1L, "Ola", Rolle.STUDENT));
        return sesjon;
    }
}
