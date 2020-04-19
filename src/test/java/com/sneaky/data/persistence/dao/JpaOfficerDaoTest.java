package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.transaction.Transactional;
import java.util.Optional;

import static com.google.common.truth.Truth.assertThat;

@SpringBootTest
@Transactional
class JpaOfficerDaoTest {

    @Autowired
    @Qualifier("jpaOfficerDao")
    private OfficerDao dao;

    @Autowired
    private JdbcTemplate template;

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