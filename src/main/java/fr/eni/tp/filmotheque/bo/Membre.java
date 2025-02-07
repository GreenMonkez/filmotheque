package fr.eni.tp.filmotheque.bo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class Membre extends Personne {
	/**
	 * Numéro de sérialisation
	 */
	private static final long serialVersionUID = 1L;

	@NotBlank
	@Email
	@Pattern(regexp = "^.+@campus-eni\\.fr$")
	private String pseudo;

	@NotBlank
	@Size(min = 8, max = 256)
	private String motDePasse;
	private boolean admin;

	public Membre() {
	}

	public Membre(String nom, String prenom, String pseudo, String motDePasse) {
		super(nom, prenom);
		this.pseudo = pseudo;
		this.motDePasse = motDePasse;
	}

	public Membre(long id, String nom, String prenom, String pseudo, String motDePasse) {
		super(id, nom, prenom);
		this.pseudo = pseudo;
		this.motDePasse = motDePasse;
	}

	public String getPseudo() {
		return pseudo;
	}

	public void setPseudo(String pseudo) {
		this.pseudo = pseudo;
	}

	public String getMotDePasse() {
		return motDePasse;
	}

	public void setMotDePasse(String motDePasse) {
		this.motDePasse = motDePasse;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(super.toString());
		builder.append(" - Membre (pseudo=");
		builder.append(pseudo);
		builder.append(", admin=");
		builder.append(admin);
		builder.append(") ");
		return builder.toString();
	}

}
