package fr.eni.tp.filmotheque.bll.contexte;

import fr.eni.tp.filmotheque.bo.Membre;
import fr.eni.tp.filmotheque.exception.BusinessException;

public interface ContexteService {

	Membre charger(String email);

	void creerMembre(Membre membre) throws BusinessException;

}
