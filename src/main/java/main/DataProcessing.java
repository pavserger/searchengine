package main;

import lombok.Data;
import main.model.*;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
 @Data
 //@Component
// @ConfigurationProperties(prefix = "indexing-settings")

 public class DataProcessing {

    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    private HashMap<String, String> sites;

    Map<String, String> testSites = new HashMap<String, String>(); //  load list sites from application.yaml


    public DataProcessing(SiteRepository siteRepository,
                          PageRepository pageRepository,
                          LemmaRepository lemmaRepository,
                          IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

    }


     public void sinchronData() {

         Site site = new Site();
         int i = 0;
         List <Site> sitesInBD = siteRepository.findAll(); // list sites in BD site
         List <Site> delSitesBD = siteRepository.findAll(); // list sites in BD site clear

         delSitesBD.clear();
         pageRepository.deleteAll(); // delete all pages


         LocalDateTime dateTime = LocalDateTime.now();

         Map<String, String> delSites = new HashMap<String, String>();
         Map<String, String> addSites = new HashMap<String, String>();

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








     public void geCotnfig( Map<String, String> testSites)  {
        this.testSites = testSites;
    }

    public boolean conainUrlConfig(String url) throws IOException {

        if (testSites.containsKey(url)) {
            return true;
        } else return false;
    }

    public void deleteSite(String url) {
        var listSites = siteRepository.findAll();
        for (var site : listSites) {
            if (site.getUrl().contains(url)) {
                siteRepository.delete(site);
            }
        }
    }

    ;

    public void addteSite(String url) {
        String urlSite;
        String nameSite;
        LocalDateTime dateTime = LocalDateTime.now();
        for (Map.Entry<String, String> testUrlSite : testSites.entrySet()) {
            urlSite = testUrlSite.getKey();
            nameSite = testUrlSite.getValue();
            if (urlSite.equals(url)) {
                Site site = new Site();
                site.setUrl(urlSite);
                site.setName(nameSite);
                site.setType("INDEXING");
                site.setStatusTime(dateTime);
                siteRepository.save(site);
            }
        }
    }

    ;

    public void updateSite(String url) {
        deleteSite(url);
        addteSite(url);
    }


    public List<Site> findSite(String url) {

        if (!url.equals("all")) {
            var listSites = siteRepository.findAll();
            var listSites2 = siteRepository.findAll();
            listSites2.clear();
            for (var site : listSites) {
                if (site.getUrl().contains(url)) {
                    listSites2.add(site);
                }
            }
            return listSites2;
        }else {
            var listSites2 = siteRepository.findAll();
            return listSites2;
        }
    }
}
