package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import com.sneaky.data.persistence.entities.Rank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@SpringBootTest
@Transactional
class JpaOfficerDaoTest {

    @Autowired
    @Qualifier("jpaOfficerDao")
    private OfficerDao dao;

    @Autowired
    private JdbcTemplate template;

    @Test
    public void saveTest() throws Exception {
        Officer officer = new Officer(Rank.LIEUTENANT, "Nyota", "Uhura");
        assertThat(officer.getId()).isNull();
        dao.save(officer);
        assertAll(
                () -> assertThat(officer.getId()).isEqualTo(6)
        );
    }

    @Test
    public void findByIdThatExists() throws Exception {
        IntStream.rangeClosed(1, 5).forEach(
                id -> {
                    System.out.println(String.format("Freaking id is %d.", id));
                    Optional<Officer> officer = dao.findById(id);
                    assertAll(
                            () -> assertThat(officer.isPresent()),
                            () -> assertThat(officer.get().getId()).isEqualTo(id)
                    );
                }
        );
    }

    @Test
    public void findByIdThatDoesNotExist() throws Exception {
        Optional<Officer> officer = dao.findById(187);
        assertThat(officer.isEmpty());
    }

    @Test
    public void findAll() throws Exception {
        List<String> officerNames = dao.findAll().stream()
                .map(Officer::getLast)
                .collect(Collectors.toList());
        assertAll(
                () -> assertThat(officerNames).isNotNull(),
                () -> assertThat(officerNames.size()).isEqualTo(5),
                () -> assertThat(officerNames).containsExactly("Kirk", "Picard", "Sisko", "Janeway", "Archer")
        );
    }

    @Test
    public void count() throws Exception {
        assertThat(dao.count()).isEqualTo(5);
    }

    @Test
    public void delete() throws Exception {
        template.query("SELECT id FROM officers", (rs, num) -> rs.getInt("id"))
                .forEach(id -> {
                    Optional<Officer> officer = dao.findById(id);
                    assertThat(officer.isPresent());
                    dao.delete(officer.get());
                    System.out.println(String.format("id of deleted officer is %d", officer.get().getId()));
                    assertThat(dao.existsById(officer.get().getId())).isFalse();
                });
        assertThat(dao.count()).isEqualTo(0);
    }

    @Test
    public void existsByIdWithExistingId() throws Exception {
        template.query("select id from officers", (rs, rowNum) -> rs.getInt("id"))
                .forEach(id -> assertThat(dao.existsById(id)));
    }

    @Test
    public void existsByIdWithNotExistingId() throws Exception {
        assertThat(dao.existsById(197)).isFalse();
    }

    @Test
    public void findOneThatExists() throws Exception {
        System.out.println("Starting findOneThatExists");
        assertThat(template).isNotNull();
        template.query("select id from officers", (resultSet, rownum) -> resultSet.getInt("id"))
                .forEach(id -> {
                    System.out.println(String.format("found id %d", id));
                    Optional<Officer> officer = dao.findById(id);
                    assertThat(officer.isPresent());
                    assertThat(officer.get().getId()).isEqualTo(id);
                });
    }

}