package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {


    List<Index> findBylemma_id (int lemma_id);


    //        SELECT * FROM search_engine.index where lemma_id in (135,142);

    @Query(
            value = "select *" +
                    "from index " +
                    "where lemma_id in : (lemmasID)",
            nativeQuery = true
    )
    List<Index> findByLemmas_id(
            List<Integer> lemmasId);


/*
    @Query(
            value = "select i.page_id " +
                    "from index i " +
                    "join lemma l on l.id = i.lemma_id " +
                    "where l.site_id in :siteIds " +
                    "and l.lemma = :lemma " +
                    "and i.page_id in (:pageIds)",
            nativeQuery = true
    )
    List<Long> findPageIdsBySiteInAndLemmaAndPageIdsIn(
            List<Long> siteIds,
            String lemma,
            List<Long> pageIds);


*/

}
