package no.hvl.dat108.booking.dto;

import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;

import java.io.Serializable;

/**
 * Det vi lagrar i HttpSession når ein brukar er logga inn.
 *
 * Kvifor ikkje berre lagre Brukar-entiteten?
 * - Brukar inneheld passordhash og salt, som ikkje har noko i sesjonen å gjere.
 * - Ein entitet i sesjonen er "detached" frå JPA, og blir fort utdatert.
 * - Sesjonsdata bør vere små og Serializable (sesjonar kan lagrast til disk).
 */
public class InnloggaBrukar implements Serializable {

    /** Nøkkelen sesjonsattributtet blir lagra under. Éin konstant = ingen skrivefeil. */
    public static final String SESJONSNOKKEL = "innloggaBrukar";

    private final Long id;
    private final String namn;
    private final Rolle rolle;

    public InnloggaBrukar(Long id, String namn, Rolle rolle) {
        this.id = id;
        this.namn = namn;
        this.rolle = rolle;
    }

    public static InnloggaBrukar fra(Brukar brukar) {
        return new InnloggaBrukar(brukar.getId(), brukar.getNamn(), brukar.getRolle());
    }

    public Long getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public boolean erAdmin() {
        return rolle == Rolle.ADMIN;
    }
}
