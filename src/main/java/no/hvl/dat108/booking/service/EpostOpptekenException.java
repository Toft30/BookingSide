package no.hvl.dat108.booking.service;

/**
 * Blir kasta når nokon prøver å registrere seg med ei e-postadresse som allereie finst.
 * Kontrollaren fangar denne og viser ei feilmelding ved e-postfeltet.
 */
public class EpostOpptekenException extends RuntimeException {

    public EpostOpptekenException(String epost) {
        super("E-postadressa er allereie registrert: " + epost);
    }
}
