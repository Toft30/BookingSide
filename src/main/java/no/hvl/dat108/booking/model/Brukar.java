package no.hvl.dat108.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Ein brukar av systemet (student eller admin).
 *
 * Vi lagrar ALDRI passordet i klartekst, berre ein hash og saltet som vart
 * brukt til å lage hashen. Hashinga kjem i fase 2.
 */
@Entity
@Table(name = "brukar") // "user" er eit reservert ord i mange databasar, så vi unngår det
public class Brukar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    // unique = true gir ein UNIQUE-constraint i databasen
    @Column(nullable = false, unique = true)
    private String epost;

    @Column(nullable = false)
    private String passordHash;

    @Column(nullable = false)
    private String salt;

    // STRING lagrar "STUDENT"/"ADMIN". ORDINAL (standard) ville lagra 0/1,
    // som går gale dersom nokon endrar rekkjefølgja i enum-en.
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rolle rolle;

    /** JPA krev ein konstruktør utan parameter. */
    protected Brukar() {
    }

    public Brukar(String namn, String epost, String passordHash, String salt, Rolle rolle) {
        this.namn = namn;
        this.epost = epost;
        this.passordHash = passordHash;
        this.salt = salt;
        this.rolle = rolle;
    }

    public Long getId() {
        return id;
    }

    public String getNamn() {
        return namn;
    }

    public void setNamn(String namn) {
        this.namn = namn;
    }

    public String getEpost() {
        return epost;
    }

    public void setEpost(String epost) {
        this.epost = epost;
    }

    public String getPassordHash() {
        return passordHash;
    }

    public void setPassordHash(String passordHash) {
        this.passordHash = passordHash;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        this.rolle = rolle;
    }

    public boolean erAdmin() {
        return rolle == Rolle.ADMIN;
    }

    // Merk: passordHash og salt er med vilje ikkje med i toString (dei skal ikkje i loggen)
    @Override
    public String toString() {
        return "Brukar[id=" + id + ", namn=" + namn + ", epost=" + epost + ", rolle=" + rolle + "]";
    }
}
