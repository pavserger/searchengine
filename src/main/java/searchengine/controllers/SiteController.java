package main.controllers;

//import com.github.tsohr.JSONArray;

import org.springframework.beans.factory.annotation.Value;
import com.github.tsohr.JSONObject;
import lombok.Data;

import main.model.*;
import main.utils.GetStatistics;
import main.utils.DataProcessing;
import main.utils.IndexSites;
import main.utils.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController

@ConfigurationProperties(prefix = "indexing-settings")
@Component
@Data
public class SiteController {

    private HashMap <String,String> sites;

    private final SiteRepository siteRepository;
   // Site site = new Site();


    private PageRepository pageRepository;
    Page pageRec = new Page();

    private LemmaRepository lemmaRepository ;

    private IndexRepository indexRepository;


    private DataProcessing dataProcessing;


    @Value("${spring.datasource.username}")
    String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.url}")
    private String url;









    //   private LemmaRepository lemmaRepository;
 //   Lemma lemmaRec = new Lemma();

    // Рекомендуемый вариант внедрения зависимости:
    // внедрение зависимости в класс через конструктор

    public SiteController(SiteRepository siteRepository,
                          PageRepository pageRepository,
                          LemmaRepository lemmaRepository,
                          IndexRepository indexRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

        dataProcessing = new DataProcessing(siteRepository,pageRepository,lemmaRepository,indexRepository);

    }

    @GetMapping("/getConfig")
    public String getConfig() throws IOException {

        Map<String, String> testSites = new HashMap<String, String>(); //  load list sites from application.yaml
        String urlSite = "";
        String nameSite = "";

        for (var mapSites : sites.entrySet()) {
            if (mapSites.getKey().contains("url")) {
                urlSite = mapSites.getValue();
            }
            if (mapSites.getKey().contains("name")) {
                nameSite = mapSites.getValue();
            }
            testSites.put(urlSite,nameSite);
        }

        dataProcessing.geCotnfig(testSites);

        dataProcessing.putCotnfig(username,password,url);

        return "OK";
    }



    @GetMapping("/api/startIndexing")
    public String startIndexing() throws IOException {
/*
        Map<String, String> testSites = new HashMap<String, String>(); //  load list sites from application.yaml
        String urlSite = "";
        String nameSite = "";

        for (var mapSites : sites.entrySet()) {
            if (mapSites.getKey().contains("url")) {
                urlSite = mapSites.getValue();
            }
            if (mapSites.getKey().contains("name")) {
                nameSite = mapSites.getValue();
            }
            testSites.put(urlSite,nameSite);
        }
*/
        GetStatistics getStatistics = new GetStatistics(siteRepository,pageRepository,
                lemmaRepository,indexRepository);

        //getConfig();


        siteRepository.deleteAll();
        pageRepository.deleteAll();
        lemmaRepository.deleteAll();
        indexRepository.deleteAll();

        dataProcessing.sinchronData();

        IndexSites indexSites = new IndexSites(siteRepository, pageRepository,
               lemmaRepository, indexRepository);

        //dataProcessing.sinchronData();

        indexSites.findPage("all");
        indexSites.findLemmas("all");

        JSONObject result = new JSONObject();
        result.put("result", false);
        result.put("error", "Все прошло успешно");

        return result.toString();
    }



    @GetMapping("/api/statistics")
    public String statistics() throws IOException {
        getConfig();
        GetStatistics getStatistics = new GetStatistics(siteRepository,pageRepository,
                lemmaRepository,indexRepository);
        return getStatistics.getStatisticsData();
    }

    @PostMapping("/api/indexPage")
    public String indexPage(@RequestParam(required = true) String url) throws IOException {
        JSONObject result = new JSONObject();
        //String s = getConfig();
        if (dataProcessing.conainUrlConfig(url))   {
            dataProcessing.updateSite(url);
            result.put("result",true);
            IndexSites indexSites = new IndexSites (siteRepository, pageRepository,
                    lemmaRepository, indexRepository);

            // GetStatistics getStatistics = new GetStatistics(siteRepository,pageRepository,
            //         lemmaRepository,indexRepository);
            //          getStatistics.sinchronData( testSites);
            indexSites.findPage(url);
            indexSites.findLemmas (url);


        }else {
            result.put("result",false);
            result.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле  " + url);

        };
        return result.toString();
    }
    @GetMapping("/api/search")
    public String search(@RequestParam String  query,
                         @RequestParam(required = false) String site,
                         @RequestParam(required = false) Integer offset,
                         @RequestParam(required = false) Integer limit) throws IOException

    {
        Search search = new Search(siteRepository, pageRepository,
                lemmaRepository, indexRepository);
        String result = "";

        var listSites = siteRepository.findAll();

        Long lSite = 0L;

        if (!(site ==null)) {
            for (var testSite : listSites) {
                if (testSite.getUrl().contains(site)) {
                    lSite = testSite.getId();
                }
            }
            search.datasClear();
            result = search.serchLemmas(query, lSite);
        } else {
            search.datasClear();
            for (var findSite : siteRepository.findAll()) {
                Long iSite = findSite.getId();
                result = search.serchLemmas(query, iSite);
            }
        }

        return result;
    }

}
