package no.hvl.dat108.booking.repository;

import no.hvl.dat108.booking.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Fleire spørjingar (t.d. for overlapp og vekeoversikt) kjem i fase 3.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    // brukar.id -> Spring Data navigerer gjennom relasjonen til Brukar
    List<Booking> findByBrukarIdOrderByStartAsc(Long brukarId);

    List<Booking> findByRomIdOrderByStartAsc(Long romId);
}
