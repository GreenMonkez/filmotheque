package fr.eni.tp.filmotheque.dal;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import fr.eni.tp.filmotheque.bo.Genre;

@Repository
@Primary
public class GenreDAOImpl implements GenreDAO {

	private static final String SELECT_BY_ID = "SELECT id, titre FROM GENRE WHERE id=:idGenre";
	private static final String SELECT_ALL = "SELECT id, titre FROM GENRE";
	private static final String COUNT_BY_ID = "SELECT count(*) FROM genre WHERE id=:idGenre";

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public GenreDAOImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	@Override
	public Genre read(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idGenre", id);
		return namedParameterJdbcTemplate.queryForObject(SELECT_BY_ID, namedParameters,
				new BeanPropertyRowMapper<>(Genre.class));
	}

	@Override
	public List<Genre> findAll() {
		return namedParameterJdbcTemplate.query(SELECT_ALL, new BeanPropertyRowMapper<>(Genre.class));
	}

	@Override
	public int countById(long id) {
		MapSqlParameterSource namedParameters = new MapSqlParameterSource();
		namedParameters.addValue("idGenre", id);
		return namedParameterJdbcTemplate.queryForObject(COUNT_BY_ID, namedParameters, Integer.class);
	}

}
