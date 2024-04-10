package main.utils;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import lombok.Data;
import main.model.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public void sinchronData( Map<String, String> testSites) {
        //   Sinchronization table Application.yaml and table Sites

        Site site = new Site();
        int i = 0;
        //  for (HashMap.Entry <String, String> mapSites : startSites.entrySet()) {
        List <Site> sitesInBD = siteRepository.findAll(); // list sites in BD site
        List <Site> delSitesBD = siteRepository.findAll(); // list sites in BD site clear
        delSitesBD.clear();

        LocalDateTime dateTime = LocalDateTime.now();

        Map<String, String> delSites = new HashMap<String, String>();
        Map<String, String> addSites = new HashMap<String, String>();
       // Map<String, String> testSites = new HashMap<String, String>();

        String urlSite = "";
        String nameSite = "";

        for (Map.Entry<String, String> testUrlSite : testSites.entrySet()) { // list add sites in BD
            urlSite = testUrlSite.getKey();
            nameSite = testUrlSite.getValue();

            if (sitesInBD.isEmpty()) {
                addSites.put(urlSite, nameSite); // need add site
            }
            for (Site siteBD : sitesInBD) {
                String urlBD = siteBD.getUrl();
                if (!urlBD.contains(urlSite)) {
                    addSites.put(urlSite, nameSite); // need add site
                } else {
                    delSitesBD.add(siteBD);
                }

            }
        }
            /*
            List <Site> tempSites = siteRepository.findByurl(urlSite); // list sites in BD site
            if (tempSites.isEmpty())
            {  // site is present in table and no BD
                addSites.put(urlSite,nameSite); // need add site
            }

        }

        for (var siteBD : sitesInBD) {             // list delete sites from BD
            String test = siteBD.getUrl();
            if (!testSites.containsKey(test)) {
                nameSite = siteBD.getName();
                urlSite = siteBD.getUrl();
                delSitesBD.add(siteBD);
            }
        }
                 */
        for (var delSiteBD1 : delSitesBD) {      // delete sites from BD
            siteRepository.delete(delSiteBD1);
        }

        for (Map.Entry<String, String> addSite : addSites.entrySet()) { // add sites to BD
            nameSite = addSite.getValue();
            urlSite = addSite.getKey();
            Site site1 = new Site();
            site1.setUrl(urlSite);
            site1.setName(nameSite);
            site1.setType("INDEXING");
            site1.setStatusTime(dateTime);
            siteRepository.save(site1);
        }

    }


        public String getStatisticsData() {


            //  Out statistics
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
            //  detal.put("lemmas",66);
              var listLemms = lemmaRepository.findBySite_id(site.getId());
              detal.put("lemmas",listLemms.size());
              detals.put(detal);
          }

          statistics.put("total",total);
          statistics.put("detailed",detals);

          result.put("result",true);
          result.put("statistics",statistics);

          return result.toString();
    }




}
