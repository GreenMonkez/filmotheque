package fr.eni.tp.filmotheque.dal;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Membre;

@Repository
@Primary
public class MembreDAOImpl implements MembreDAO {

	private static final String SELECT_BY_ID = "SELECT id, nom, prenom, email, admin FROM membre WHERE id=:idMembre";
	private static final String SELECT_BY_EMAIL = "SELECT id, nom, prenom, email, admin FROM membre WHERE email=:emailMembre";
	private static final String COUNT_BY_ID = "SELECT count(*) FROM membre WHERE id=:idMembre";
	private static final String COUNT_BY_EMAIL = "SELECT count(*) FROM membre WHERE email=:emailMembre";
	private static final String INSERT = "insert into membre (nom, prenom, email, password) values (:nom, :prenom, :email, :password)";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public MembreDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Membre read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idMembre", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters, new MembreRowMapper());
	}

	@Override
	public Membre read(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("emailMembre", email);
		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_EMAIL, namedParameters, new MembreRowMapper());
	}

	class MembreRowMapper implements RowMapper<Membre> {

		@Override
		public Membre mapRow(ResultSet rs, int rowNum) throws SQLException {
			Membre m = new Membre();
			m.setId(rs.getLong("id"));
			m.setPseudo(rs.getString("email"));
			m.setNom(rs.getString("nom"));
			m.setPrenom(rs.getString("prenom"));
			m.setAdmin(rs.getBoolean("admin"));

			return m;
		}

	}

	@Override
	public int countById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idMembre", id);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_ID, namedParameters, Integer.class);
	}

	@Override
	public int countByEmail(String email) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("emailMembre", email);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_EMAIL, namedParameters, Integer.class);
	}

	@Override
	public void create(Membre membre) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("nom", membre.getNom());
		namedParameters.addValue("prenom", membre.getPrenom());
		namedParameters.addValue("email", membre.getPseudo());
		namedParameters.addValue("password", membre.getMotDePasse());

		namedParameterJdbcTemplate.update(INSERT, namedParameters);
	}

}
