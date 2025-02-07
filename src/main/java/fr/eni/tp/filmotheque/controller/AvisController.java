package fr.eni.tp.filmotheque.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

import fr.eni.tp.filmotheque.bll.FilmService;
import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.exception.BusinessException;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/avis")
@SessionAttributes({ "membreEnSession" })
public class AvisController {

	private FilmService filmService;

	public AvisController(FilmService filmService) {
		this.filmService = filmService;
	}

	// Création d'un nouvel avis
	@GetMapping("/creer")
	public String creerAvis(@RequestParam("idFilm") long idFilm, Model model,
			@ModelAttribute("membreEnSession") Membre membreEnSession) {
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			// Il y a un membre en session
			Film f = this.filmService.consulterFilmParId(idFilm);

			if (f != null) {
				model.addAttribute("film", f);

				Avis avis = new Avis();
				model.addAttribute(avis);
				return "view-avis-creer";
			}
		}
		return "redirect:/films";

	}

	@PostMapping("/creer")
	public String creerAvis(@ModelAttribute(name = "membreEnSession") Membre membreEnSession,
			@Valid @ModelAttribute(name = "avis") Avis avis, BindingResult bindingResult,
			@RequestParam(name = "idFilm") long idFilm, Model model) {
		if (membreEnSession != null && membreEnSession.getId() >= 1) {
			// Il y a un membre en session
			avis.setMembre(membreEnSession);
			System.out.println(avis);
			// Sauvegarde de l’avis avec l’identifiant du film :
			if (bindingResult.hasErrors()) {
				Film f = this.filmService.consulterFilmParId(idFilm);
				model.addAttribute("film", f);
				return "view-avis-creer";

			} else {
				try {
					filmService.publierAvis(avis, idFilm);
					return "redirect:/films";
				} catch (BusinessException e) {
					e.printStackTrace();
					e.getClesErreurs().forEach(cle -> {
						ObjectError error = new ObjectError("globalError", cle);
						bindingResult.addError(error);
					});
				}
				Film f = this.filmService.consulterFilmParId(idFilm);
				model.addAttribute("film", f);
				return "view-avis-creer";
			}
		} else {

			ObjectError error = new ObjectError("globalError", "validation.membre");
			bindingResult.addError(error);
		}

		// Redirection vers la liste des films :
		Film f = this.filmService.consulterFilmParId(idFilm);
		model.addAttribute("film", f);
		return "view-avis-creer";

	}

}
