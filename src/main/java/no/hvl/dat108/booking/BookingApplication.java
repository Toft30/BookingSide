package no.hvl.dat108.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Startpunktet for applikasjonen.
 *
 * @SpringBootApplication slår på komponentskanning i denne pakka og alle underpakker
 * (controller, service, repository, model, dto), og autokonfigurerer Spring MVC,
 * Thymeleaf, JPA og H2 ut frå avhengigheitene i pom.xml.
 */
@SpringBootApplication
public class BookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookingApplication.class, args);
    }
}
