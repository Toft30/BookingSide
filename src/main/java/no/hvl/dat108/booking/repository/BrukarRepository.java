package no.hvl.dat108.booking.repository;

import no.hvl.dat108.booking.model.Brukar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Spring Data lagar implementasjonen for oss ved oppstart.
 * Metodenamna blir tolka til spørjingar ("derived queries"):
 * findByEpost  ->  SELECT b FROM Brukar b WHERE b.epost = ?1
 */
public interface BrukarRepository extends JpaRepository<Brukar, Long> {

    Optional<Brukar> findByEpost(String epost);

    boolean existsByEpost(String epost);
}
