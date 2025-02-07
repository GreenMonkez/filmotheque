package fr.eni.tp.filmotheque.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.contexte.ContexteService;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@SessionAttributes({ "membreEnSession" })
public class LoginController {
	private ContexteService contexteService;

	public LoginController(ContexteService contexteService) {
		this.contexteService = contexteService;
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/session")
	public String session(@ModelAttribute("membreEnSession") Membre membreEnSession, Principal user) {

		Membre membre = this.contexteService.charger(user.getName());
		if (membre != null) {
			membreEnSession.setId(membre.getId());
			membreEnSession.setNom(membre.getNom());
			membreEnSession.setPrenom(membre.getPrenom());
			membreEnSession.setPseudo(membre.getPseudo());
			membreEnSession.setAdmin(membre.isAdmin());
		} else {
			membreEnSession.setId(0);
			membreEnSession.setNom(null);
			membreEnSession.setPrenom(null);
			membreEnSession.setPseudo(null);
			membreEnSession.setAdmin(false);
		}
		return "/index";
	}

	@GetMapping("/signin")
	public String signInGet(Model model) {

		model.addAttribute("membre", new Membre());

		return "/signin";
	}

	@PostMapping("/signin")
	public String signInPost(@Valid @ModelAttribute("membre") Membre membre, BindingResult bindingResult) {

		if (bindingResult.hasErrors()) {
			return "signin";
		} else {
			try {
				this.contexteService.creerMembre(membre);
				return "redirect:/login";
			} catch (BusinessException e) {
				e.printStackTrace();
				e.getClesErreurs().forEach(cle -> {
					ObjectError error = new ObjectError("globalError", cle);
					bindingResult.addError(error);
				});
			}
			return "signin";

		}
	}

	@ModelAttribute("membreEnSession")
	public Membre addMembreEnSession() {
		System.out.println("Add membre en session");
		return new Membre();
	}
}
