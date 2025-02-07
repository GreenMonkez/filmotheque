package fr.eni.tp.filmotheque.bll;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.bo.Participant;
import fr.eni.tp.filmotheque.dal.AvisDAO;
import fr.eni.tp.filmotheque.dal.FilmDAO;
import fr.eni.tp.filmotheque.dal.GenreDAO;
import fr.eni.tp.filmotheque.dal.MembreDAO;
import fr.eni.tp.filmotheque.dal.ParticipantDAO;
import fr.eni.tp.filmotheque.exception.BusinessException;

@Service
@Primary
public class FilmServiceImpl implements FilmService {

	private AvisDAO avisDAO;
	private FilmDAO filmDAO;
	private GenreDAO genreDAO;
	private MembreDAO membreDAO;
	private ParticipantDAO participantDAO;

	public FilmServiceImpl(AvisDAO avisDAO, FilmDAO filmDAO, GenreDAO genreDAO, MembreDAO membreDAO,
			ParticipantDAO participantDAO) {
		this.avisDAO = avisDAO;
		this.filmDAO = filmDAO;
		this.genreDAO = genreDAO;
		this.membreDAO = membreDAO;
		this.participantDAO = participantDAO;
	}

	@Override
	public List<Film> consulterFilms() {
		List<Film> films = filmDAO.findAll();
		for (Film film : films) {
			film.setGenre(genreDAO.read(film.getGenre().getId()));
			film.setRealisateur(participantDAO.read(film.getRealisateur().getId()));
		}
		return films;
	}

	@Override
	public Film consulterFilmParId(long id) {
		Film film = filmDAO.read(id);
		film.setGenre(genreDAO.read(film.getGenre().getId()));
		film.setRealisateur(participantDAO.read(film.getRealisateur().getId()));
		film.setActeurs(participantDAO.findActeurs(id));
		film.setAvis(avisDAO.findByFilm(id));
		for (Avis a : film.getAvis()) {
			a.setMembre(membreDAO.read(a.getMembre().getId()));
		}
		return film;
	}

	@Override
	public List<Genre> consulterGenres() {
		return genreDAO.findAll();
	}

	@Override
	public List<Participant> consulterParticipants() {
		return participantDAO.findAll();
	}

	@Override
	public Genre consulterGenreParId(long id) {
		return genreDAO.read(id);
	}

	@Override
	public Participant consulterParticipantParId(long id) {
		return participantDAO.read(id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void creerFilm(Film film) throws BusinessException {

//		film.setGenre(new Genre(100, "i"));
//		film.setRealisateur(new Participant(100, "i", "i"));
//		film.getActeurs().add(new Participant(100, "i", "i"));

		BusinessException be = new BusinessException();
		boolean valide = genreExiste(film.getGenre().getId(), be);
		valide &= realisateurExiste(film.getRealisateur().getId(), be);
		valide &= validerListeActeurs(film.getActeurs(), be);
		valide &= filmExiste(film.getTitre(), be);

		try {
			if (valide) {
				filmDAO.create(film);
				for (Participant acteur : film.getActeurs()) {
					participantDAO.createActeur(acteur.getId(), film.getId());
				}
			} else {
				throw be;
			}
		} catch (Exception e) {
			e.printStackTrace();
			be.addCleErreur("bll.film.creer.erreur");
			throw be;
		}

	}

	@Override
	public String consulterTitreFilm(long id) {
		return filmDAO.findTitre(id);
	}

	@Override
	@Transactional(rollbackFor = BusinessException.class)
	public void publierAvis(Avis avis, long idFilm) throws BusinessException {

//		avis.getMembre().setId(0);
//		idFilm = 0;
//		avis.getMembre().setId(2);
//		idFilm = 1;

		BusinessException be = new BusinessException();
		boolean valide = membreExiste(avis.getMembre().getId(), be);
		valide &= filmIdExiste(idFilm, be);
		valide &= avisExiste(avis.getMembre().getId(), idFilm, be);

		try {
			if (valide) {
				avisDAO.create(avis, idFilm);
			} else {
				throw be;
			}
		} catch (Exception e) {
			e.printStackTrace();
			be.addCleErreur("bll.avis.creer.erreur");
			throw be;
		}

	}

	@Override
	public List<Avis> consulterAvis(long idFilm) {
		List<Avis> avis = this.avisDAO.findByFilm(idFilm);
		if (avis != null) {
			// Association avec le membre
			avis.forEach(a -> {
				Membre membre = membreDAO.read(a.getMembre().getId());
				a.setMembre(membre);
			});
		}
		return avis;
	}

	private boolean genreExiste(long id, BusinessException be) {
		if (genreDAO.countById(id) == 0) {
			be.addCleErreur("validation.film.genre.id.inconnu");
			return false;
		}
		return true;
	}

	private boolean realisateurExiste(long id, BusinessException be) {
		if (participantDAO.countById(id) == 0) {
			be.addCleErreur("validation.film.realisateur.id.inconnu");
			return false;
		}
		return true;
	}

	private boolean validerListeActeurs(List<Participant> listeActeurs, BusinessException be) {
		boolean validation = participantDAO.validerListeActeurs(listeActeurs);

		if (!validation) {
			be.addCleErreur("validation.film.acteur.id.inconnu");
		}

		return validation;

	}

	private boolean filmExiste(String titre, BusinessException be) {
		if (filmDAO.countByTitre(titre) == 1) {
			be.addCleErreur("validation.film.unique");
			return false;
		}
		return true;
	}

	private boolean membreExiste(long id, BusinessException be) {
		if (membreDAO.countById(id) == 0) {
			be.addCleErreur("validation.avis.membre.inconnu");
			return false;
		}
		return true;
	}

	private boolean filmIdExiste(long id, BusinessException be) {
		if (filmDAO.countById(id) == 0) {
			be.addCleErreur("validation.film.id.inconnu");
			return false;
		}
		return true;
	}

	private boolean avisExiste(long idMembre, long idFilm, BusinessException be) {
		if (avisDAO.countByIdMembreFilm(idMembre, idFilm) == 1) {
			be.addCleErreur("validation.avis.unique");
			return false;
		}
		return true;
	}
}
