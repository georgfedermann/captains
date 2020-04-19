package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import com.sneaky.data.persistence.entities.Rank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcOfficerDao implements OfficerDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcOfficerDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Officer save(Officer officer) {
        return null;
    }

    @Override
    public Optional<Officer> findById(Integer id) {
        Optional<Officer> result;
        if (!existsById(id)) {
            result = Optional.empty();
        } else {
            result = Optional.of(jdbcTemplate.queryForObject(
                    "SELECT id, first_name, last_name, rank FROM officers WHERE id = ?",
                    new RowMapper<Officer>() {
                        @Override
                        public Officer mapRow(ResultSet rs, int rowNum) throws SQLException {
                            return new Officer(rs.getInt("id"),
                                    Rank.valueOf(rs.getString("rank")),
                                    rs.getString("first_name"),
                                    rs.getString("last_name"));
                        }
                    }, id));
        }
        return result;
    }

    @Override
    public List<Officer> findAll() {
        return jdbcTemplate.query("SELECT id, rank, first_name, last_name FROM officers",
                (rs, rowNum) -> new Officer(rs.getInt("id"),
                        Rank.valueOf(rs.getString("rank")),
                        rs.getString("first_name"),
                        rs.getString("last_name")));
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
