package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LemmaRepository extends JpaRepository<Lemma, Long> {

    List<Lemma> findBylemma (String lemma);

    List<Lemma> findBySite_id (Long site_id);

    Optional<Lemma> findById (int id);


}
