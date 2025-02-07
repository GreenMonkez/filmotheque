package fr.eni.tp.filmotheque.bll.contexte;

import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.dal.MembreDAO;
import fr.eni.tp.filmotheque.exception.BusinessException;

@Service
@Primary
public class ContexteServiceImpl implements ContexteService {

	private MembreDAO membreDAO;

	public ContexteServiceImpl(MembreDAO membreDAO) {
		this.membreDAO = membreDAO;
	}

	@Override
	public Membre charger(String email) {
		return this.membreDAO.read(email);
	}

	@Override
	public void creerMembre(Membre membre) throws BusinessException {

		BusinessException be = new BusinessException();
		boolean valide = membreExiste(membre.getPseudo(), be);

		try {
			if (valide) {
				PasswordEncoder passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
				membre.setMotDePasse(passwordEncoder.encode(membre.getMotDePasse()));
				this.membreDAO.create(membre);
			} else {
				throw be;
			}
		} catch (Exception e) {
			e.printStackTrace();
			be.addCleErreur("bll.membre.creer.erreur");
			throw be;
		}

	}

	private boolean membreExiste(String email, BusinessException be) {
		if (membreDAO.countByEmail(email) == 1) {
			be.addCleErreur("validation.membre.unique");
			return false;
		}
		return true;
	}

}
