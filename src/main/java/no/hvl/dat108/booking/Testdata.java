package no.hvl.dat108.booking;

import no.hvl.dat108.booking.model.Booking;
import no.hvl.dat108.booking.model.Brukar;
import no.hvl.dat108.booking.model.Rolle;
import no.hvl.dat108.booking.model.Rom;
import no.hvl.dat108.booking.repository.BookingRepository;
import no.hvl.dat108.booking.repository.BrukarRepository;
import no.hvl.dat108.booking.repository.RomRepository;
import no.hvl.dat108.booking.service.PassordService;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Legg inn testdata når applikasjonen startar.
 *
 * CommandLineRunner.run() blir kalla av Spring etter at alle bønner er laga.
 * Sidan databasen er fil-basert, sjekkar vi først om det allereie finst data,
 * slik at vi ikkje legg inn dei same romma kvar gong vi startar på nytt.
 *
 * @Profile("!test") gjer at dette ikkje køyrer når vi testar med profilen "test".
 */
@Component
@Profile("!test")
public class Testdata implements CommandLineRunner {

    private final RomRepository romRepository;
    private final BrukarRepository brukarRepository;
    private final BookingRepository bookingRepository;
    private final PassordService passordService;

    // Konstruktørinjeksjon: Spring sender inn repository-bønnene
    public Testdata(RomRepository romRepository, BrukarRepository brukarRepository,
                    BookingRepository bookingRepository, PassordService passordService) {
        this.romRepository = romRepository;
        this.brukarRepository = brukarRepository;
        this.bookingRepository = bookingRepository;
        this.passordService = passordService;
    }

    @Override
    public void run(String... args) {
        if (romRepository.count() > 0) {
            return; // data finst frå før
        }

        List<Rom> rom = romRepository.saveAll(List.of(
                new Rom("E403", 6, "Kronstad E"),
                new Rom("E405", 4, "Kronstad E"),
                new Rom("M210", 8, "Kronstad M"),
                new Rom("M212", 4, "Kronstad M"),
                new Rom("Biblioteket grupperom 1", 10, "Kronstad Bibliotek")));

        
        String olaSalt = passordService.genererSalt();
        String olaHash = passordService.hashMedSalt("ola123", olaSalt);
        Brukar ola = brukarRepository.save(
                new Brukar("Ola Student", "ola@stud.hvl.no", olaHash, olaSalt, Rolle.STUDENT));
        
        String kariSalt = passordService.genererSalt();
        String kariHash = passordService.hashMedSalt("kari123", kariSalt);
        Brukar kari = brukarRepository.save(
                new Brukar("Kari Admin", "kari@hvl.no", kariHash, kariSalt, Rolle.ADMIN));

        // Éin booking i morgon kl. 10–12, så vi har noko å sjå på
        LocalDateTime start = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(10, 0));
        bookingRepository.save(new Booking(ola, rom.get(0), start, start.plusHours(2)));
         bookingRepository.save(new Booking(kari, rom.get(1), start, start.plusHours(2)));
    }
}
