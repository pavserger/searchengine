package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {

    public final static String myQuery = "SELECT * FROM search_engine.index";

    List<Index> findBylemma_id (int lemma_id);


    //        SELECT * FROM search_engine.index where lemma_id in (135,142);


    /*
            SELECT * FROM search_engine.index
            WHERE page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 433)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 789)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 464)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 266);
*/


    @Query(myQuery
         //   value = ":sQuery"
           // value = ("select * from ?1")
           // ,nativeQuery = true
    )
    List<Index> findByLemmas_id( String sQuery);


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
