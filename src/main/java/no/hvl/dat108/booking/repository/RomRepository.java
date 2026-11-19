package no.hvl.dat108.booking.repository;

import no.hvl.dat108.booking.model.Rom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RomRepository extends JpaRepository<Rom, Long> {

    List<Rom> findAllByOrderByBygningAscNamnAsc();
}
