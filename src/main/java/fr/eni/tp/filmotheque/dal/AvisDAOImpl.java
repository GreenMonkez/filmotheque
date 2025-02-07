package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Avis;
import fr.eni.tp.filmotheque.bo.Membre;

@Repository
@Primary
public class AvisDAOImpl implements AvisDAO {

	private static final String SELECT_AVIS_BY_IDFILM = "SELECT id, note, commentaire, id_membre, id_film FROM avis WHERE id_film=:idFilm";
	private static final String INSERT = "INSERT INTO AVIS(note,commentaire,id_membre,id_film) VALUES (:note, :commentaire, :idMembre, :idFilm)";
	private static final String COUNT_BY_ID_MEMBRE_FILM = "SELECT count(*) FROM avis WHERE id_membre=:idMembre and id_film=:idFilm";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public AvisDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public void create(Avis avis, long idFilm) {
		// Il n’est pas nécessaire de remonter immédiatement l’identifiant auto-généré.
		// Car les avis sont chargés par rapport à un film
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("note", avis.getNote());
		mapSqlParameterSource.addValue("commentaire", avis.getCommentaire());
		mapSqlParameterSource.addValue("idMembre", avis.getMembre().getId());
		mapSqlParameterSource.addValue("idFilm", idFilm);

		namedParameterJdbcTemplate.update(INSERT, mapSqlParameterSource);

	}

	@Override
	public List<Avis> findByFilm(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idFilm", idFilm);
		return namedParameterJdbcTemplate.query(SELECT_AVIS_BY_IDFILM, namedParameters, new AvisRowMapper());
	}

	class AvisRowMapper implements RowMapper<Avis> {
		@Override
		public Avis mapRow(ResultSet rs, int rowNum) throws SQLException {
			Avis a = new Avis();
			a.setId(rs.getLong("ID"));
			a.setNote(rs.getInt("NOTE"));
			a.setCommentaire(rs.getString("COMMENTAIRE"));

			// Association vers le membre
			Membre membre = new Membre();
			membre.setId(rs.getInt("id_membre"));
			a.setMembre(membre);
			return a;
		}
	}

	@Override
	public int countByIdMembreFilm(long idMembre, long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idMembre", idMembre);
		namedParameters.addValue("idFilm", idFilm);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_ID_MEMBRE_FILM, namedParameters, Integer.class);
	}

}
