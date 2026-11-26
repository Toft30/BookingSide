package no.hvl.dat108.booking.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import no.hvl.dat108.booking.dto.InnloggaBrukar;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

public class InnloggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {

        // getSession(false): ikkje lag ein ny sesjon berre for å sjekke
        HttpSession sesjon = request.getSession(false);

        if (sesjon != null && sesjon.getAttribute(InnloggaBrukar.SESJONSNOKKEL) != null) {
            return true; // logga inn -> kontrollaren får køyre
        }

        // Ikkje logga inn (eller sesjonen har gått ut på tid)
        response.sendRedirect(request.getContextPath() + "/login?krevInnlogging");
        return false; // stopp her, kontrollaren blir ikkje kalla
    }
}
