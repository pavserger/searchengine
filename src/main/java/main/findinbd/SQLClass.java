package main.findinbd;

import lombok.Data;
import main.model.*;
import main.utils.DataProcessing;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//@ConfigurationProperties(prefix = "spring")
@Component
@Data
//@Value
public class SQLClass {
    private HashMap <String,String> datasource;

    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    public SQLClass(SiteRepository siteRepository, PageRepository pageRepository,
                    LemmaRepository lemmaRepository, IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

    private List<Integer> indexList =  new ArrayList<Integer>();
    private Index index;

    public List<Integer> query(String sQ) {


        DataProcessing dataProcessing = new DataProcessing();

        String url = dataProcessing.url;
        String usr = dataProcessing.user;
        String pass = dataProcessing.password;

        List<Integer> pageList = new ArrayList<>();


        try {
            Connection connection = DriverManager.getConnection(url, usr, pass);
            Statement statement = connection.createStatement();
            String query = sQ;
            ResultSet resultQury = statement.executeQuery(query);
          //  resultQury.next();
            System.out.println("SQLClass");

           // indexList = new ArrayList<Integer>();

            while (resultQury.next()) {
                var id = resultQury.getInt("page_id");

                indexList.add(id);
                System.out.println("page_id =  "  +  id );


/*
                var rank = resultQury.getInt("rank");
                var lemma_id = resultQury.getInt("lemma_id");
                var page_id = resultQury.getInt("page_id");

                System.out.println(id+" !  "+rank+" !  "+ lemma_id+"!"+ page_id );


               // pageList.add(new Integer(page_id));

                index = new Index();
                index.setId(id);
                index.setRank(rank);

               // long l = page_id;
                var listPageFind = pageRepository.findById(page_id);
                index.setPage(listPageFind.get());

               // long l2 = lemma_id;
                var listLemmasFind = lemmaRepository.findById(lemma_id);
                index.setLemma(listLemmasFind.get());
*/
        //        indexList.add(id);

            }



        } catch (Exception ex) {
            ex.printStackTrace();
        };


        return indexList;
    }
}

