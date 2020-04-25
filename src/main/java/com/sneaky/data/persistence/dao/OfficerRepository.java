package com.sneaky.data.persistence.dao;

import com.sneaky.data.persistence.entities.Officer;
import com.sneaky.data.persistence.entities.Rank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OfficerRepository extends JpaRepository<Officer, Integer> {
    List<Officer> findAllByLastLikeAndRankEquals(String like, Rank rank);
}
