package no.hvl.dat108.booking.service;

import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;
import no.hvl.dat108.booking.repository.BrukarRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;
import java.util.Optional;

/**
 * Forretningslogikk for brukarar: registrering og sjekk av innlogging.
 *
 * Kontrollaren skal ikkje snakke direkte med repository eller PassordService,
 * berre med denne klassa. Då kan reglane testast utan web-laget (sjå BrukarServiceTest).
 */
@Service
public class BrukarService {

    private final BrukarRepository brukarRepository;
    private final PassordService passordService;

    public BrukarService(BrukarRepository brukarRepository, PassordService passordService) {
        this.brukarRepository = brukarRepository;
        this.passordService = passordService;
    }

    /**
     * Opprettar ein ny student med hasha passord.
     *
     * @throws EpostOpptekenException dersom e-posten allereie er i bruk
     */
    @Transactional
    public Brukar registrer(String namn, String epost, String passord) {
        String normalisertEpost = normaliser(epost);

        if (brukarRepository.existsByEpost(normalisertEpost)) {
            throw new EpostOpptekenException(normalisertEpost);
        }

        String salt = passordService.genererSalt();
        String hash = passordService.hashMedSalt(passord, salt);

        // Nye brukarar er alltid STUDENT. Admin må setjast opp på anna vis.
        return brukarRepository.save(new Brukar(namn.trim(), normalisertEpost, hash, salt, Rolle.STUDENT));
    }

    /**
     * Returnerer brukaren dersom e-post og passord stemmer, elles Optional.empty().
     * Vi seier med vilje ikkje KVA som var feil (e-post eller passord),
     * slik at ein angripar ikkje kan finne ut kva e-postar som er registrerte.
     */
    public Optional<Brukar> loggInn(String epost, String passord) {
        if (epost == null || passord == null) {
            return Optional.empty();
        }
        return brukarRepository.findByEpost(normaliser(epost))
                .filter(b -> passordService.erKorrektPassord(passord, b.getSalt(), b.getPassordHash()));
    }

    /** "Ola@HVL.no " og "ola@hvl.no" skal reknast som same brukar. */
    private String normaliser(String epost) {
        return epost.trim().toLowerCase(Locale.ROOT);
    }
}
