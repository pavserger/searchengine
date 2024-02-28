package main.controllers;

//import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import lombok.Data;
import main.FindMap;

import main.LemmaFinder;
import main.model.*;
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
        lemmaRepository.deleteAll();
        this.indexRepository = indexRepository;
        indexRepository.deleteAll();
    }

    @GetMapping("/lem/")
    public void lem() throws IOException {

        List<Site> listSites = siteRepository.findAll();
        List<Page> listPage = pageRepository.findAll();
        List<Lemma> listLemms = lemmaRepository.findAll();



        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);

        HashMap<String, Integer> pageLemmas = new HashMap<String, Integer>();

        for (Site site : listSites) {
            pageLemmas.clear();
            for (Page page : listPage) {

                String sText = page.getTitlepage().toString() + page.getContent();
                HashMap<String, Integer> listLemma = (HashMap<String, Integer>)
                        lemmaFinder.collectLemmas(sText);

                //    Map<String, Integer> map = new HashMap<>();
                Iterator mapIterator = listLemma.entrySet().iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) mapIterator.next();
                    // заполнение массива лемм
                    String sKey = entry.getKey();
                    if (sKey.length() >= 2) {
                       // int num = entry.getValue();
                        Lemma lemma = new Lemma();

                        List<Lemma> lemmas = lemmaRepository.findBylemma(sKey);
                        if (!lemmas.isEmpty()) {              // the  lemma is present

                            for (Lemma lem : lemmas){
                               int fr = lem.getFrequency() + 1;
                                lem.setFrequency(fr);
                                lemmaRepository.save(lem);
                            }

                        } else {                               // new lemma
                            pageLemmas.put(sKey, 1);
                            lemma.setSite(site);
                            lemma.setLemma(sKey);
                            lemma.setFrequency(1);
                            lemmaRepository.save(lemma);

                            Index index = new Index();
                            index.setLemma(lemma);
                            float f = entry.getValue();
                            index.setRank(f);
                            index.setPage(page);

                            indexRepository.save(index);

                        }
                    }
                } // page
            }  // site
    }

    }




        @GetMapping("/init/")
    public void init() {
                                            // код должен быть перенесен в "/api/startIndexing  или /api/indexPage"
        Site site = new Site();
        int i = 0;

        siteRepository.deleteAll();
        pageRepository.deleteAll();


        LocalDateTime dateTime = LocalDateTime.now();

      //  for (HashMap.Entry <String, String> mapSites : startSites.entrySet()) {
        for (var mapSites : sites.entrySet()) {
            if (mapSites.getKey().contains("url")) {
                if  (i > 0) site = new Site();
                i++;
                site.setUrl(mapSites.getValue());
                site.setType("INDEXED");
                site.setStatusTime(dateTime);

            }
            if (mapSites.getKey().contains("name")) {
                site.setName(mapSites.getValue());
                siteRepository.save(site);
            }

        }
    }
    @GetMapping("/api/startIndexing")
    public String startIndexing() {
        init(); // инициализиция
      //     Site site = new Site();

      //  Long li = Long.valueOf(1);
        String sUrl = "";
        long iCount = siteRepository.count();
        List <Site> listSites = siteRepository.findAll();
    //    for (long li = 1; li < iCount; li ++) {


        for (Site site :listSites ) {

      //      Optional<Site> site = siteRepository.findById(li);

            if (site.getUrl() != null) {
               // Site site = new Site();
                sUrl = site.getUrl();
                String siteMap = new ForkJoinPool().invoke(new FindMap(site, pageRepository));
                // System.out.println(siteMap);
                JSONObject result = new JSONObject();
                result.put("result", true);
            //    result.put("error", "Все хорошо");
             //   return result.toString();

            } else {
                JSONObject result = new JSONObject();
                result.put("result", false);
                result.put("error", "Нет такого сайта !"+ sUrl);
                //return result.toString();
            }
        }
        JSONObject result = new JSONObject();
        result.put("result", false);
        result.put("error", "Все прошло успешно");

        return result.toString();
    }



    @GetMapping("/api/statistics")
    public String statistics() {
        JSONObject result = new JSONObject();
        JSONObject statistics = new JSONObject();
      //  JSONArray ar =new JSONArray();
        JSONObject total = new JSONObject();
        total.put("sites",10);
        total.put("pages",436423);
        total.put("lemmas",5127891);
        total.put("indexing",true);

        JSONObject detal = new JSONObject();
        detal.put("url","http//name");
        detal.put("name","name");
        detal.put("status","INDEXED");
        detal.put("statusTime",1600160357);
        detal.put("error","Ошибка индексации: главная страница сайта недоступна");
        detal.put("pages",5764);
        detal.put("lemmas",32115);

        JSONArray detals =new JSONArray();
        detals.put(detal);


        statistics.put("total",total);
        statistics.put("detailed",detals);

        result.put("result",true);
        result.put("statistics",statistics);

        return result.toString();
    }

    @PostMapping("/api/indexPage")
    public String indexPage() {
        JSONObject result = new JSONObject();
     //   result.put("result": true);
        result.put("result",false);
        result.put("error", "Данная страница находится за пределами сайтов, указанных в конфигурационном файле"  );

        return result.toString();
        //return "'result': false\n";
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
