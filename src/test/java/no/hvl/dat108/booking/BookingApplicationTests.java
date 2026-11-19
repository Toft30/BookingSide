package no.hvl.dat108.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Røyktest: startar heile Spring-konteksten. Feilar dersom t.d. ein entitet
 * eller eit repository-metodenamn er feil.
 */
@SpringBootTest
@ActiveProfiles("test")
class BookingApplicationTests {

    @Test
    void kontekstenStartar() {
    }
}
