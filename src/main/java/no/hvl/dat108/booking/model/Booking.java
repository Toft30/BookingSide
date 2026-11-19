package no.hvl.dat108.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Ein booking av eitt rom for eitt tidsrom, gjort av ein brukar.
 *
 * Forretningsreglane (ikkje overlapp, maks 2 timar osv.) blir IKKJE sjekka her,
 * men i service-laget og i ein eigen validator (fase 3).
 */
@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mange bookingar høyrer til éin brukar. LAZY: brukaren blir ikkje henta
    // frå databasen før vi faktisk kallar getBrukar().
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brukar_id")
    private Brukar brukar;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "rom_id")
    private Rom rom;

    // "start"/"slutt" kan kollidere med SQL-nøkkelord, så vi gir kolonnane eigne namn
    @Column(name = "start_tid", nullable = false)
    private LocalDateTime start;

    @Column(name = "slutt_tid", nullable = false)
    private LocalDateTime slutt;

    @Column(nullable = false)
    private LocalDateTime oppretta;

    protected Booking() {
    }

    public Booking(Brukar brukar, Rom rom, LocalDateTime start, LocalDateTime slutt) {
        this.brukar = brukar;
        this.rom = rom;
        this.start = start;
        this.slutt = slutt;
        this.oppretta = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Brukar getBrukar() {
        return brukar;
    }

    public Rom getRom() {
        return rom;
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    public LocalDateTime getSlutt() {
        return slutt;
    }

    public void setSlutt(LocalDateTime slutt) {
        this.slutt = slutt;
    }

    public LocalDateTime getOppretta() {
        return oppretta;
    }

    @Override
    public String toString() {
        // Brukar og rom er med vilje ikkje med, for å unngå å trigge lazy-lasting
        return "Booking[id=" + id + ", start=" + start + ", slutt=" + slutt + "]";
    }
}
