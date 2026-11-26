package no.hvl.dat108.booking.controller;

import no.hvl.dat108.booking.dto.InnloggaBrukar;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.SessionAttribute;

/**
 * Startsida etter innlogging. Blir erstatta av bookingoversikta i fase 3.
 */
@Controller
public class StartController {

    // @SessionAttribute hentar attributtet frå HttpSession for oss.
    // Interceptoren har allereie sikra at det finst, elles kjem vi ikkje hit.
    @GetMapping("/")
    public String start(@SessionAttribute(InnloggaBrukar.SESJONSNOKKEL) InnloggaBrukar brukar, Model model) {
        model.addAttribute("brukar", brukar);
        return "index";
    }
}
