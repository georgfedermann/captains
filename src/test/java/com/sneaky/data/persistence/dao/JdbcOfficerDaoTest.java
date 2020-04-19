package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import com.sneaky.data.persistence.entities.Rank;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.google.common.truth.Truth.assertThat;

@SpringBootTest
@Transactional
class JdbcOfficerDaoTest {

    @Autowired
    private OfficerDao dao;

    @Test
    public void saveTest() throws Exception {
        Officer officer = new Officer(Rank.LIEUTENANT, "Nyota", "Uhura");
        assertThat(officer.getId()).isNull();
        officer = dao.save(officer);
        assertThat(officer.getId()).isGreaterThan(0);
    }

    @Test
    public void findAllTest() throws Exception {
        List<Officer> result = dao.findAll();
        assertThat(result).isNotEmpty();
        result.stream().forEach(System.out::println);

        List<String> officerNames = result.stream()
                .map(Officer::getLast)
                .collect(Collectors.toList());
        assertThat(officerNames)
                .containsExactly("Kirk", "Picard", "Sisko", "Janeway", "Archer");
    }

    @Test
    public void findByIdThatExistsTest() throws Exception {
        Optional<Officer> officer = dao.findById(1);
        assertThat(officer.isPresent());
        assertThat(officer.get().getId()).isEqualTo(1);
    }

    @Test
    public void findByIdThatDoesNotExistTest() throws Exception {
        Optional<Officer> officer = dao.findById(18703);
        assertThat(officer.isEmpty());
    }

    @Test
    public void deleteTest() throws Exception {
        IntStream.rangeClosed(1, 5)
                .forEach(id -> {
                    Optional<Officer> officer = dao.findById(id);
                    assertThat(officer.isPresent());
                    dao.delete(officer.get());
                });
        assertThat(dao.count()).isEqualTo(0);
    }

    @Test
    public void countTest() throws Exception {
        assertThat(dao.count()).isEqualTo(5);
    }

    @Test
    public void existsByIdTest() throws Exception {
        IntStream.rangeClosed(1, 5).forEach(id -> assertThat(dao.existsById(id)).isTrue());
    }
}