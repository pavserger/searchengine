package main.searchengine;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import lombok.Data;
import main.model.*;

import java.util.List;

@Data
public class  GetStatistics {

    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository ;
    private IndexRepository indexRepository;


        public GetStatistics (SiteRepository siteRepository,
                          PageRepository pageRepository,
                          LemmaRepository lemmaRepository,
                          IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
    }

      public String getStatisticsData() {

          JSONObject result = new JSONObject();
          JSONObject statistics = new JSONObject();
          //  JSONArray ar =new JSONArray();
          JSONObject total = new JSONObject();
         //siteRepository.count();
          total.put("sites",siteRepository.count());
          total.put("pages",pageRepository.count());
          total.put("lemmas",lemmaRepository.count());
          total.put("indexing",true);

          List<Site> sites = siteRepository.findAll();

          JSONArray detals =new JSONArray();

          for (Site site: sites) {
              JSONObject detal = new JSONObject();
              detal.put("url",site.getUrl());
              detal.put("name",site.getName());
              detal.put("status",site.getType());
              detal.put("statusTime",site.getStatusTime());
              detal.put("error",site.getLastError());

              Long i = site.getId();
              detal.put("pages",pageRepository.findBySite_id(i).size());
              // many to many  add cod
              detal.put("lemmas",33);
              detals.put(detal);
          }

          statistics.put("total",total);
          statistics.put("detailed",detals);

          result.put("result",true);
          result.put("statistics",statistics);

          return result.toString();
    }




}
