package no.hvl.dat108.booking.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import no.hvl.dat108.booking.dto.InnloggaBrukar;
import no.hvl.dat108.booking.dto.Registreringsskjema;
import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.service.BrukarService;
import no.hvl.dat108.booking.service.EpostOpptekenException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class BrukarController {

    private final BrukarService brukarService;

    public BrukarController(BrukarService brukarService) {
        this.brukarService = brukarService;
    }

    // ---------- Registrering ----------

    @GetMapping("/registrer")
    public String visRegistrering(Model model) {
        model.addAttribute("skjema", new Registreringsskjema());
        return "registrer";
    }

    @PostMapping("/registrer")
    public String registrer(@Valid @ModelAttribute("skjema") Registreringsskjema skjema,
                            BindingResult bindingResult, RedirectAttributes ra) {

        // Eigen sjekk som Bean Validation ikkje dekkjer (to felt mot kvarandre).
        // Vi hoppar over den dersom feltet allereie har ein feil, så vi ikkje får to meldingar.
        if (!bindingResult.hasFieldErrors("passordRepetert") && !skjema.erPassordLike()) {
            bindingResult.rejectValue("passordRepetert", "passord.ulik", "Passorda må vere like");
        }

        // Feil i skjemaet: vis same side igjen (INGEN redirect), så feilmeldingane
        // og det brukaren skreiv framleis er med i modellen.
        if (bindingResult.hasErrors()) {
            return "registrer";
        }

        try {
            brukarService.registrer(skjema.getNamn(), skjema.getEpost(), skjema.getPassord());
        } catch (EpostOpptekenException e) {
            bindingResult.rejectValue("epost", "epost.oppteken", "Det finst allereie ein brukar med denne e-posten");
            return "registrer";
        }

        // Flash-attributt overlever éin redirect (lagra midlertidig i sesjonen av Spring)
        ra.addFlashAttribute("melding", "Brukaren er oppretta. Du kan no logge inn.");
        return "redirect:/login";
    }

    // ---------- Innlogging ----------

    @GetMapping("/login")
    public String visInnlogging() {
        return "innlogging";
    }

    @PostMapping("/login")
    public String loggInn(@RequestParam String epost, @RequestParam String passord,
                          HttpServletRequest request, RedirectAttributes ra) {

        Optional<Brukar> brukar = brukarService.loggInn(epost, passord);

        if (brukar.isEmpty()) {
            ra.addFlashAttribute("feilmelding", "Ugyldig e-post eller passord");
            ra.addFlashAttribute("epost", epost); // så brukaren slepp å skrive e-posten på nytt
            return "redirect:/login";
        }

        HttpSession sesjon = request.getSession();
        // Ny sesjons-id ved innlogging hindrar "session fixation": ein angripar som har
        // lurt på deg ein kjend sesjons-id før du logga inn, kan ikkje bruke han etterpå.
        request.changeSessionId();
        sesjon.setAttribute(InnloggaBrukar.SESJONSNOKKEL, InnloggaBrukar.fra(brukar.get()));

        // Timeout er sett i application.properties (server.servlet.session.timeout)
        return "redirect:/";
    }

    // ---------- Utlogging ----------

    // POST og ikkje GET: ein GET-lenke kan bli henta av nettlesaren på førehand
    // eller lagt inn i eit <img src> på ei anna side, og då ville du blitt logga ut.
    @PostMapping("/logout")
    public String loggUt(HttpSession sesjon, RedirectAttributes ra) {
        sesjon.invalidate();
        ra.addFlashAttribute("melding", "Du er no logga ut");
        return "redirect:/login";
    }
}
