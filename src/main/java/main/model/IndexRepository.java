package main.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import javax.transaction.Transactional;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public interface IndexRepository extends JpaRepository<Index, Long> {

    // public final static String myQuery = "SELECT * FROM search_engine.index";

    List<Index> findBylemma_id(int lemma_id);

    List<Index> findByPage_id(int  page_id);
}



