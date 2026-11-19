package no.hvl.dat108.booking.service;

import org.springframework.stereotype.Service;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.HexFormat;
import java.security.MessageDigest;

@Service
public class PassordService{

    public String genererSalt(){
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return HexFormat.of().formatHex(salt);
    }

    public String hashMedSalt (String passord, String salt){
        if (passord == null || salt == null) {
            throw new IllegalArgumentException("Passord og salt kan ikkje vere null");
        } 
        char[] passordChars = passord.toCharArray();
        byte[] saltBytes = HexFormat.of().parseHex(salt);

        try {
            PBEKeySpec spec = new PBEKeySpec(passordChars, saltBytes, 1000, 256);
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException("Feil ved hashing av passord", e);
        }
    }

     public boolean erKorrektPassord(String passord, String salt, String passordHash) {
        if (passord == null || salt == null || passordHash == null) {
            return false;
        }
        return MessageDigest.isEqual(hashMedSalt(passord, salt).getBytes(), passordHash.getBytes());
    }
}