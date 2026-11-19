# Grupperom-booking

Ei bookingside for grupperom på campus, laga som læringsprosjekt i **DAT108** ved HVL.
Prosjektet blir bygd i fasar, der kvar fase øver på ein del av pensum.

## Teknologi

- Java 21, Spring Boot 3, Maven
- Spring MVC + Thymeleaf
- Spring Data JPA med H2 (fil-basert, data overlever omstart)
- Bean Validation (`jakarta.validation`)
- Innlogging med `HttpSession` og eiga passordhashing (PBKDF2WithHmacSHA256 med salt) – utan Spring Security
- Vanleg JavaScript med `fetch` mot eit REST-API
- Testing: JUnit 5, Mockito, MockMvc

## Køyre prosjektet

```bash
./mvnw spring-boot:run      # Windows: mvnw.cmd spring-boot:run
./mvnw test                 # køyr alle testane
```

Opne <http://localhost:8080>. H2-konsollen ligg på <http://localhost:8080/h2-console>
(JDBC URL `jdbc:h2:file:./data/bookingdb`, brukar `sa`, tomt passord).

Vil du starte med tom database, slett mappa `data/`.

## Arkitektur

Lagdelt arkitektur, der kvart lag berre snakkar med laget under:

```
Nettlesar ──► controller ──► service ──► repository ──► H2
               (MVC/REST)    (reglar)    (Spring Data)
                  ▲ dto                      ▲ model (entitetar)
```

| Pakke        | Innhald |
|--------------|---------|
| `model`      | JPA-entitetar: `Brukar`, `Rom`, `Booking`, og enum-en `Rolle` |
| `repository` | Spring Data-grensesnitt (`JpaRepository`) |
| `service`    | Forretningsreglar |
| `controller` | MVC- og REST-kontrollarar |
| `dto`        | Skjema- og JSON-objekt inn/ut av kontrollarane |

## Domene

- **Brukar**: id, namn, e-post (unik), passordhash, salt, rolle (`STUDENT`/`ADMIN`)
- **Rom**: id, namn, kapasitet, bygning
- **Booking**: id, brukar, rom, start, slutt, oppretta-tidspunkt

### Forretningsreglar

- Ein booking kan ikkje overlappe med ein annan booking i same rom
- Ikkje i fortida, maks 2 timar lang, berre heile eller halve timar
- Ein brukar kan ha maks 3 aktive (framtidige) bookingar
- Berre eigaren kan avbestille ein booking. Admin kan opprette og endre rom.

## Plan

Delar merkte ✍️ skriv eg sjølv.

- [x] **Fase 1 – Skjelett**: Maven-prosjekt, pakkestruktur, entitetar, repository, testdata ved oppstart
- [ ] **Fase 2 – Registrering og innlogging**: Bean Validation og `BindingResult`, PRG, flash-meldingar,
      `HttpSession`, utlogging, timeout, `HandlerInterceptor`
      ✍️ passordhashing (salt, hash, verifisering)
- [ ] **Fase 3 – Booking med Thymeleaf**: vekeoversikt per rom, bookingskjema, «mine bookingar», avbestilling
      ✍️ overlapp-sjekk i service-laget og eigen validator for «maks 2 timar, ikkje i fortida»
- [ ] **Fase 4 – Samtidigheit**: test med 100 trådar via `ExecutorService` som viser dobbeltbooking
      ✍️ rettinga (`synchronized`/`Lock`, deretter diskusjon av `@Version`)
- [ ] **Fase 5 – REST-API**: `@RestController`, `ResponseEntity`, statuskodar, DTO-ar, `@ControllerAdvice`
      ✍️ endepunktet for å opprette booking
- [ ] **Fase 6 – JavaScript**: kalender med `fetch` og `async/await`, DOM-oppdatering, sanntidsvalidering, 409-handtering
- [ ] **Fase 7 – Statistikk med streams**: mest brukte rom, populære timar, bruksgrad
      ✍️ minst to spørringar, først som løkker og så som streams
- [ ] **Fase 8 – Testing og porteføljepuss**: Mockito, MockMvc, GitHub Actions, skjermbilete, «kva eg lærte»

## Kva eg lærte

_Fyllast ut undervegs._
