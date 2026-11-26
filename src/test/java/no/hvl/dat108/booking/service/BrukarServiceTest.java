package no.hvl.dat108.booking.service;

import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;
import no.hvl.dat108.booking.repository.BrukarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Einingstest med Mockito: BrukarRepository og PassordService er "falske" (mocks),
 * så vi testar BERRE logikken i BrukarService, utan database og utan ekte hashing.
 */
@ExtendWith(MockitoExtension.class)
class BrukarServiceTest {

    @Mock
    private BrukarRepository brukarRepository;

    @Mock
    private PassordService passordService;

    @InjectMocks // lagar BrukarService og sender inn mockane i konstruktøren
    private BrukarService brukarService;

    @Test
    void registrerLagrarStudentMedHashOgSalt() {
        when(brukarRepository.existsByEpost("ola@hvl.no")).thenReturn(false);
        when(passordService.genererSalt()).thenReturn("salt");
        when(passordService.hashMedSalt("hemmeleg", "salt")).thenReturn("hash");
        when(brukarRepository.save(any(Brukar.class))).thenAnswer(inv -> inv.getArgument(0));

        brukarService.registrer(" Ola ", " Ola@HVL.no ", "hemmeleg");

        // ArgumentCaptor fangar objektet som vart sendt til save(), så vi kan sjekke det
        ArgumentCaptor<Brukar> captor = ArgumentCaptor.forClass(Brukar.class);
        verify(brukarRepository).save(captor.capture());
        Brukar lagra = captor.getValue();

        assertEquals("Ola", lagra.getNamn());
        assertEquals("ola@hvl.no", lagra.getEpost(), "e-post skal normaliserast");
        assertEquals("hash", lagra.getPassordHash());
        assertEquals("salt", lagra.getSalt());
        assertEquals(Rolle.STUDENT, lagra.getRolle());
    }

    @Test
    void registrerMedOpptekenEpostKastarUnntakOgLagrarIkkje() {
        when(brukarRepository.existsByEpost("ola@hvl.no")).thenReturn(true);

        assertThrows(EpostOpptekenException.class,
                () -> brukarService.registrer("Ola", "ola@hvl.no", "hemmeleg"));

        verify(brukarRepository, never()).save(any());
    }

    @Test
    void loggInnMedRettPassordGirBrukar() {
        Brukar ola = new Brukar("Ola", "ola@hvl.no", "hash", "salt", Rolle.STUDENT);
        when(brukarRepository.findByEpost("ola@hvl.no")).thenReturn(Optional.of(ola));
        when(passordService.erKorrektPassord("hemmeleg", "salt", "hash")).thenReturn(true);

        assertEquals(Optional.of(ola), brukarService.loggInn("OLA@hvl.no", "hemmeleg"));
    }

    @Test
    void loggInnMedFeilPassordGirTom() {
        Brukar ola = new Brukar("Ola", "ola@hvl.no", "hash", "salt", Rolle.STUDENT);
        when(brukarRepository.findByEpost("ola@hvl.no")).thenReturn(Optional.of(ola));
        when(passordService.erKorrektPassord("feil", "salt", "hash")).thenReturn(false);

        assertTrue(brukarService.loggInn("ola@hvl.no", "feil").isEmpty());
    }

    @Test
    void loggInnMedUkjendEpostGirTom() {
        when(brukarRepository.findByEpost("ukjend@hvl.no")).thenReturn(Optional.empty());

        assertTrue(brukarService.loggInn("ukjend@hvl.no", "hemmeleg").isEmpty());
    }

    @Test
    void loggInnMedNullGirTom() {
        assertTrue(brukarService.loggInn(null, "x").isEmpty());
        assertTrue(brukarService.loggInn("ola@hvl.no", null).isEmpty());
    }
}
