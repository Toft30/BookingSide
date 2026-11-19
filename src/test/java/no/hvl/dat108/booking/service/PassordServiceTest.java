package no.hvl.dat108.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Einingstestar for PassordService.
 *
 * Dette er ein vanleg JUnit-test utan Spring: vi lagar objektet sjølve med new.
 * Det går fordi PassordService ikkje har avhengigheiter, og testen blir mykje raskare
 * enn med @SpringBootTest.
 *
 * Forventningar testane byggjer på:
 * - genererSalt() returnerer saltet som ein hex-String (16 byte = 32 hex-teikn)
 * - hashMedSalt() brukar PBKDF2WithHmacSHA256 med 256 bit nøkkellengd (= 64 hex-teikn)
 */
class PassordServiceTest {

    private PassordService passordService;

    @BeforeEach
    void setUp() {
        passordService = new PassordService();
    }

    // --- genererSalt ---

    @Test
    void saltErHexStrengPaa32Teikn() {
        String salt = passordService.genererSalt();

        assertEquals(32, salt.length(), "16 byte salt skal bli 32 hex-teikn");
        assertTrue(salt.matches("[0-9a-f]+"), "saltet skal berre innehalde hex-teikn");
    }

    @Test
    void toSaltErUlike() {
        // Om dette feilar, er saltet ikkje tilfeldig (t.d. new Random(fast frø))
        assertNotEquals(passordService.genererSalt(), passordService.genererSalt());
    }

    // --- hashMedSalt ---

    @Test
    void hashErPaa64HexTeikn() {
        String hash = passordService.hashMedSalt("hemmeleg", passordService.genererSalt());

        // 256 bit = 32 byte = 64 hex-teikn. Med SHA1 og 128 bit blir det berre 32.
        assertEquals(64, hash.length());
    }

    @Test
    void sammePassordOgSaltGirSammeHash() {
        String salt = passordService.genererSalt();

        assertEquals(passordService.hashMedSalt("hemmeleg", salt),
                passordService.hashMedSalt("hemmeleg", salt));
    }

    @Test
    void sammePassordMedUliktSaltGirUlikHash() {
        // Dette er heile poenget med salt: to brukarar med same passord får ulik hash
        String hash1 = passordService.hashMedSalt("hemmeleg", passordService.genererSalt());
        String hash2 = passordService.hashMedSalt("hemmeleg", passordService.genererSalt());

        assertNotEquals(hash1, hash2);
    }

    @Test
    void hashErIkkjePassordetIKlartekst() {
        String hash = passordService.hashMedSalt("hemmeleg", passordService.genererSalt());

        assertFalse(hash.contains("hemmeleg"));
    }

    @Test
    void hashMedNullKastarUnntak() {
        String salt = passordService.genererSalt();

        assertThrows(IllegalArgumentException.class, () -> passordService.hashMedSalt(null, salt));
        assertThrows(IllegalArgumentException.class, () -> passordService.hashMedSalt("hemmeleg", null));
    }

    // --- erKorrektPassord ---

    @Test
    void rettPassordBlirGodkjent() {
        String salt = passordService.genererSalt();
        String hash = passordService.hashMedSalt("hemmeleg", salt);

        assertTrue(passordService.erKorrektPassord("hemmeleg", salt, hash));
    }

    @Test
    void feilPassordBlirAvvist() {
        String salt = passordService.genererSalt();
        String hash = passordService.hashMedSalt("hemmeleg", salt);

        assertFalse(passordService.erKorrektPassord("Hemmeleg", salt, hash), "store/små bokstavar skal telje");
        assertFalse(passordService.erKorrektPassord("", salt, hash));
    }

    @Test
    void rettPassordMedFeilSaltBlirAvvist() {
        String salt = passordService.genererSalt();
        String hash = passordService.hashMedSalt("hemmeleg", salt);

        assertFalse(passordService.erKorrektPassord("hemmeleg", passordService.genererSalt(), hash));
    }

    @Test
    void nullGirFalseIkkjeUnntak() {
        // Ved innlogging vil vi heller avvise enn å krasje
        String salt = passordService.genererSalt();
        String hash = passordService.hashMedSalt("hemmeleg", salt);

        assertFalse(passordService.erKorrektPassord(null, salt, hash));
        assertFalse(passordService.erKorrektPassord("hemmeleg", null, hash));
        assertFalse(passordService.erKorrektPassord("hemmeleg", salt, null));
    }
}
