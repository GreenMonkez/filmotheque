package fr.eni.tp.filmotheque.dal;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Participant;

@Repository
@Primary
public class ParticipantDAOImpl implements ParticipantDAO {

	private static final String SELECT_BY_ID = "SELECT id, nom, prenom FROM PARTICIPANT WHERE id=:idParticipant";
	private static final String SELECT_ALL = "SELECT id, nom, prenom FROM PARTICIPANT";
	private static final String SELECT_ACTEURS_BY_IDFILM = "SELECT id, nom, prenom FROM participant JOIN acteurs ON participant.id=acteurs.id_participant WHERE id_film=:idFilm";
	private static final String INSERT = "INSERT INTO ACTEURS(id_film, id_participant) VALUES(:idFilm, :idParticipant)";
	private static final String COUNT_BY_ID = "SELECT count(*) FROM participant WHERE id=:idParticipant";
	private static final String COUNT_BY_LIST_ID = "SELECT count(*) FROM participant WHERE id IN (:listIdParticipant)";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ParticipantDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Participant read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idParticipant", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters,
				new BeanPropertyRowMapper<>(Participant.class));
	}

	@Override
	public List<Participant> findAll() {
		return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Participant.class));
	}

	@Override
	public List<Participant> findActeurs(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idFilm", idFilm);
		return namedParameterJdbcTemplate.query(SELECT_ACTEURS_BY_IDFILM, namedParameters,
				new BeanPropertyRowMapper<>(Participant.class));
	}

	@Override
	public void createActeur(long idParticipant, long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idParticipant", idParticipant);
		namedParameters.addValue("idFilm", idFilm);

		namedParameterJdbcTemplate.update(INSERT, namedParameters);
	}

	@Override
	public int countById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idParticipant", id);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_ID, namedParameters, Integer.class);
	}

	@Override
	public boolean validerListeActeurs(List<Participant> listActeurs) {
		List<Long> listeIds = listActeurs.stream().map(Participant::getId).toList();

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("listIdParticipant", listeIds);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_LIST_ID, namedParameters, Integer.class) == listeIds
				.size();
	}

}
