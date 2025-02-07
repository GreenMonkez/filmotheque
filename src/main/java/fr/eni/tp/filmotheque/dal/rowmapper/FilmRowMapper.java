package fr.eni.tp.filmotheque.dal.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.bo.Genre;
import fr.eni.tp.filmotheque.bo.Participant;

public class FilmRowMapper implements RowMapper<Film> {

	@Override
	public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
		Film f = new Film();
		f.setId(rs.getInt("id"));
		f.setTitre(rs.getString("titre"));
		f.setAnnee(rs.getInt("annee"));
		f.setDuree(rs.getInt("duree"));
		f.setSynopsis(rs.getString("synopsis"));

		Genre g = new Genre();
		g.setId(rs.getLong("id_genre"));
		f.setGenre(g);

		Participant p = new Participant();
		p.setId(rs.getLong("id_realisateur"));
		f.setRealisateur(p);

		return f;
	}

}
