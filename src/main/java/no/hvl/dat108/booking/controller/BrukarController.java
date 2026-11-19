package no.hvl.dat108.booking.controller;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import no.hvl.dat108.booking.dto.Registreringsskjema;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller 
public class BrukarController {
    
    @GetMapping("/registrer")
    public String visRegistrering(Model model){
        model.addAttribute("Skjema", new Registreringsskjema());
        return "registrer";
    }

    @PostMapping("/registrer")
    public String registrerBrukar(@Valid @ModelAttribute("Skjema") Registreringsskjema skjema, BindingResult bindingResult, RedirectAttributes ra, HttpSession session) {
        
    if (!skjema.erPassordLike()) {
        bindingResult.rejectValue("passordRepetert", "passord.ulik", "Passorda må vere like");
    }

    if(bindingResult.hasErrors()){
        return "registrer";
    }

    ra.addFlashAttribute("melding", "Brukar oppretta!");
    return "redirect:/login";
    }


    @GetMapping("/login")
    public String visInnlogging(){
        return "innlogging";
    }

    @PostMapping("/login")
    public String loginn(@RequestParam String epost, @RequestParam String passord, HttpSession session, RedirectAttributes ra){
        



        return "redirect/booking";
    }

     @PostMapping("/utlogging")
    public String loggUt(HttpSession session, RedirectAttributes ra) {
        session.invalidate();
        ra.addFlashAttribute("melding", "Du er no logga ut");
        return "redirect:/innlogging";
    }

}
