package no.hvl.dat108.booking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Registreringsskjema {
    @NotBlank(message = "Namn må fyllast ut")
    @Size(min = 2, max = 50, message = "Namn må vere mellom 2 og 50 teikn")
    private String namn;
    @NotBlank(message = "E-post må fyllast ut")
    @Email(message = "Ugyldig e-postadresse")
    private String epost;
    @NotBlank(message = "Passord må fyllast ut")
    @Size(min = 6, message = "Passord må vere minst 6 teikn")
    private String passord;
    @NotBlank(message = "Du må gjenta passordet")
    private String passordRepetert;
    
    public Registreringsskjema() {
    }
    
    public boolean erPassordLike() {
        return passord != null && passord.equals(passordRepetert);
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
    public String getPassord() {
        return passord;
    }
    public void setPassord(String passord) {
        this.passord = passord;
    }
    public String getPassordRepetert() {
        return passordRepetert;
    }
    public void setPassordRepetert(String passordRepetert) {
        this.passordRepetert = passordRepetert;
    }
}