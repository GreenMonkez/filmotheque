package fr.eni.tp.filmotheque.dal;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Film;
import fr.eni.tp.filmotheque.dal.rowmapper.FilmRowMapper;

@Repository
@Primary
public class FilmDAOImpl implements FilmDAO {

	private static final String INSERT = "INSERT INTO FILM (titre, annee, duree, synopsis, id_realisateur, id_genre) VALUES (:titre, :annee, :duree, :synopsis, :idRealisateur, :idGenre)";
	private static final String SELECT_BY_ID = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM WHERE id=:idFilm";
	private static final String SELECT_ALL = "SELECT id, titre, annee, duree, synopsis, id_realisateur, id_genre FROM FILM";
	private static final String SELECT_TITRE_BY_ID = "SELECT id, titre FROM FILM WHERE id=:idFilm";
	private static final String COUNT_BY_TITRE = "SELECT count(*) FROM film WHERE titre LIKE :titre";
	private static final String COUNT_BY_ID = "SELECT count(*) FROM film WHERE id=:idFilm";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public FilmDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public void create(Film film) {
		KeyHolder keyHolder = new GeneratedKeyHolder();

		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("titre", film.getTitre());
		namedParameters.addValue("annee", film.getAnnee());
		namedParameters.addValue("duree", film.getDuree());
		namedParameters.addValue("synopsis", film.getSynopsis());
		namedParameters.addValue("idRealisateur", film.getRealisateur().getId());
		namedParameters.addValue("idGenre", film.getGenre().getId());

		namedParameterJdbcTemplate.update(INSERT, namedParameters, keyHolder);

		if (keyHolder != null && keyHolder.getKey() != null) {
			film.setId(keyHolder.getKey().longValue());
		}

	}

	@Override
	public Film read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idFilm", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters, new FilmRowMapper());
	}

	@Override
	public List<Film> findAll() {
		return namedParameterJdbcTemplate.query(SELECT_ALL, new FilmRowMapper());
	}

	@Override
	public String findTitre(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idFilm", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_TITRE_BY_ID, namedParameters, String.class);
	}

	@Override
	public int countByTitre(String titre) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("titre", titre);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_TITRE, namedParameters, Integer.class);
	}

	@Override
	public int countById(long idFilm) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idFilm", idFilm);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_ID, namedParameters, Integer.class);
	}

}
