package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PageRepository extends JpaRepository<Page, Long> {
    List<Page> findBySite_id (Long site_id);
    Optional<Page> findById (int id);
    List<Page> findAllById(Long iSite);
}