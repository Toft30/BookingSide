package no.hvl.dat108.booking.repository;

import no.hvl.dat108.booking.model.Booking;
import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;
import no.hvl.dat108.booking.model.Rom;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * @DataJpaTest startar berre JPA-delen av Spring (ikkje web-laget),
 * brukar ein database i minnet og rullar tilbake etter kvar test.
 */
@DataJpaTest
class RepositoryTest {

    @Autowired
    private BrukarRepository brukarRepository;

    @Autowired
    private RomRepository romRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void finnBrukarPaaEpost() {
        brukarRepository.save(new Brukar("Test", "test@hvl.no", "hash", "salt", Rolle.STUDENT));

        assertTrue(brukarRepository.findByEpost("test@hvl.no").isPresent());
        assertTrue(brukarRepository.findByEpost("finst@ikkje.no").isEmpty());
    }

    @Test
    void epostMaaVereUnik() {
        brukarRepository.saveAndFlush(new Brukar("A", "same@hvl.no", "h", "s", Rolle.STUDENT));

        // saveAndFlush tvingar fram INSERT med ein gong, så UNIQUE-constrainten slår til
        assertThrows(DataIntegrityViolationException.class, () ->
                brukarRepository.saveAndFlush(new Brukar("B", "same@hvl.no", "h", "s", Rolle.STUDENT)));
    }

    @Test
    void finnBookingarForBrukarSortertPaaStart() {
        Brukar brukar = brukarRepository.save(new Brukar("Test", "b@hvl.no", "h", "s", Rolle.STUDENT));
        Rom rom = romRepository.save(new Rom("E403", 6, "Kronstad E"));
        LocalDateTime t = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

        bookingRepository.save(new Booking(brukar, rom, t.plusHours(4), t.plusHours(5)));
        bookingRepository.save(new Booking(brukar, rom, t, t.plusHours(1)));

        var bookingar = bookingRepository.findByBrukarIdOrderByStartAsc(brukar.getId());
        assertEquals(2, bookingar.size());
        assertEquals(t, bookingar.get(0).getStart());
    }
}
