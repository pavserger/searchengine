package main.controllers;

//import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import lombok.Data;
import main.FindMap;

import main.model.*;
import netscape.javascript.JSObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ForkJoinPool;

@RestController

@ConfigurationProperties(prefix = "indexing-settings")
@Component
@Data



public class SiteController {

    private HashMap<String,String> sites;

    private final SiteRepository siteRepository;
   // Site site = new Site();


    private PageRepository pageRepository;
    Page pageRec = new Page();


 //   private LemmaRepository lemmaRepository;
 //   Lemma lemmaRec = new Lemma();





    // Рекомендуемый вариант внедрения зависимости:
    // внедрение зависимости в класс через конструктор
    public SiteController(SiteRepository siteRepository, PageRepository pageRepository) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
    }

    @GetMapping("/init/")
    public void init() {

        Site site = new Site();
        int i = 0;

        LocalDateTime dateTime = LocalDateTime.now();


        for (HashMap.Entry<String, String> mapSites : sites.entrySet()) {
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


       // site = new Site();

     /*
        site.setName("Булгаковский дом");
        site.setUrl("https://dombulgakova.ru/");
        site.setType("INDEXED");
        site.setStatusTime(dateTime);
        siteRepository.save(site);
    */
    }
    @GetMapping("/api/startIndexing")
    public String startIndexing() {
        //   Site site = new Site();
        Long li = Long.valueOf(1);
        String sUrl = "";
        Optional<Site> site = siteRepository.findById(li);
        if (site.isPresent()) {
            sUrl = site.get().getUrl();
            String siteMap = new ForkJoinPool().invoke(new FindMap(site.get(),pageRepository));
            // System.out.println(siteMap);
            JSONObject result = new JSONObject();
            result.put("result", false);
            result.put("error", "Все хорошо");
            return result.toString();

        } else {
            JSONObject result = new JSONObject();
            result.put("result", false);
            result.put("error", "Нет такого сайта !");
            return result.toString();
        }
    }


/*

        {
            try {
                FileWriter writer = new FileWriter(new File("src/main/resources/site_map.txt"));

                writer.write(siteMap);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
*/



        /*
        JSONObject result = new JSONObject();
        result.put("result",false);
        result.put("error", "Индексация уже запущена Ха-Ха");

        return result.toString();
       //return "'result': false\n";

         */

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
