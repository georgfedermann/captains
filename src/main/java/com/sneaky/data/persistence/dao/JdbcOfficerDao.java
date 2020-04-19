package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import com.sneaky.data.persistence.entities.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class JdbcOfficerDao implements OfficerDao {
    private JdbcTemplate jdbcTemplate;

    private RowMapper<Officer> rowMapper = (rs, rowNum) -> new Officer(rs.getInt("id"),
            Rank.valueOf(rs.getString("rank")),
            rs.getString("first_name"),
            rs.getString("last_name"));

    private SimpleJdbcInsert insertOfficer;

    @Autowired
    public JdbcOfficerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        insertOfficer = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("officers")
                .usingGeneratedKeyColumns("id");
    }

    @Override
    public Officer save(Officer officer) {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("rank", officer.getRank());
        parameters.put("first_name", officer.getFirst());
        parameters.put("last_name", officer.getLast());
        Integer newId = (Integer) insertOfficer.executeAndReturnKey(parameters);
        officer.setId(newId);
        return officer;
    }

    @Override
    public Optional<Officer> findById(Integer id) {
        Optional<Officer> result;
        if (!existsById(id)) {
            result = Optional.empty();
        } else {
            result = Optional.of(jdbcTemplate.queryForObject(
                    "SELECT id, first_name, last_name, rank FROM officers WHERE id = ?",
                    rowMapper, id));
        }
        return result;
    }

    @Override
    public List<Officer> findAll() {
        return jdbcTemplate.query("SELECT id, rank, first_name, last_name FROM officers",
                rowMapper);
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM officers", Long.class);
    }

    @Override
    public void delete(Officer officer) {
        jdbcTemplate.update("DELETE FROM officers WHERE id = ?", officer.getId());
    }

    @Override
    public boolean existsById(Integer id) {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT 1 FROM officers WHERE id = ?)", Boolean.class, id);
    }
}
