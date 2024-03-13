package main.controllers;

//import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import lombok.Data;
import main.DataProcessing;
import main.FindMap;

import main.IndexSites;
import main.LemmaFinder;
import main.model.*;
import main.searchengine.GetStatistics;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ForkJoinPool;

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
        dataProcessing.sinchronData();

        IndexSites indexSites = new IndexSites(siteRepository, pageRepository,
               lemmaRepository, indexRepository);

        indexSites.findPage("all");
        indexSites.findLemms("all");

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
        if (dataProcessing.conainUrlConfig(url))   {
            dataProcessing.updateSite(url);
            result.put("result",true);
            IndexSites indexSites = new IndexSites (siteRepository, pageRepository,
                    lemmaRepository, indexRepository);

            // GetStatistics getStatistics = new GetStatistics(siteRepository,pageRepository,
            //         lemmaRepository,indexRepository);
            //          getStatistics.sinchronData( testSites);
            indexSites.findPage(url);
            indexSites.findLemms("all");


        }else {
            result.put("result",false);
            result.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле  " + url);

        };
        return result.toString();
    }
    @GetMapping("/api/search")
    public String search(@RequestParam String query) {
        String sQuery = query;

        List<Lemma> lemmas = lemmaRepository.findBylemma(sQuery);
        for (Lemma lemma : lemmas){
          int lem = lemma.getId();
          List <Index> indexList = indexRepository.findBylemma_id(lem);
          System.out.println(indexList);
        }

        // find lemma



        JSONObject result = new JSONObject();

        JSONObject data = new JSONObject();
        data.put( "site", "http://www.site.com");
        data.put(  "siteName", "Имя сайта");
        data.put( "uri", "/path/to/page/6784");
        data.put(  "title", "Заголовок страницы, которую выводим");
        data.put(  "snippet", "Фрагмент текста,в котором найдены совпадения, <b>"+sQuery+"</b>");
        data.put( "relevance", 0.93362);

        JSONArray datas =new JSONArray();
        datas.put(data);


        result.put("result",true);
        result.put("count",532);
        result.put("data",datas);
        return result.toString();
    }

    /*
    public ResponseEntity<Boolean> startIndexing() throws IOException {
     //   IndexingStatusResponse status = indexingService.startIndexing();
    //    if (indexingService.isIndexing()) {
            return ResponseEntity.ok(false);
        }
   //     throw new UnknownIndexingStatusException("Неизвестная ошибка индексирования");

*/
}
