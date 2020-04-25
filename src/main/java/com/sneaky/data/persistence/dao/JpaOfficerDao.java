package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Optional;

// specifying the class name in @Repository like so has the same effect as
// using the default value for the spring bean as provided by spring.
@Repository("jpaOfficerDao")
public class JpaOfficerDao implements OfficerDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Officer save(Officer officer) {
        entityManager.persist(officer);
        return officer;
    }

    @Override
    public Optional<Officer> findById(Integer id) {
        return Optional.ofNullable(entityManager.find(Officer.class, id));
    }

    @Override
    public List<Officer> findAll() {
        return entityManager.createQuery("SELECT o FROM Officer o", Officer.class).getResultList();
    }

    @Override
    public long count() {
        return entityManager.createQuery("SELECT COUNT(o.id) FROM Officer o", Long.class)
                .getSingleResult();
    }

    @Override
    public void delete(Officer officer) {
        entityManager.remove(officer);
    }

    @Override
    public boolean existsById(Integer id) {
        return !entityManager.createQuery(
                "SELECT 1 FROM Officer o where o.id =: id")
                .setParameter("id", id)
                .getResultList().isEmpty();
    }
}
