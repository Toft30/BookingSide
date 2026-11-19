package no.hvl.dat108.booking.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Eit grupperom som kan bookast.
 */
@Entity
@Table(name = "rom")
public class Rom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String namn;

    private int kapasitet;

    @Column(nullable = false)
    private String bygning;

    protected Rom() {
    }

    public Rom(String namn, int kapasitet, String bygning) {
        this.namn = namn;
        this.kapasitet = kapasitet;
        this.bygning = bygning;
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

    public int getKapasitet() {
        return kapasitet;
    }

    public void setKapasitet(int kapasitet) {
        this.kapasitet = kapasitet;
    }

    public String getBygning() {
        return bygning;
    }

    public void setBygning(String bygning) {
        this.bygning = bygning;
    }

    @Override
    public String toString() {
        return "Rom[id=" + id + ", namn=" + namn + ", kapasitet=" + kapasitet + ", bygning=" + bygning + "]";
    }
}
