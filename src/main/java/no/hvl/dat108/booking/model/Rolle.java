package no.hvl.dat108.booking.model;

/**
 * Kva rolle ein brukar har. Blir lagra som tekst i databasen
 * (sjå @Enumerated(EnumType.STRING) i Brukar).
 */
public enum Rolle {
    STUDENT,
    ADMIN
}
