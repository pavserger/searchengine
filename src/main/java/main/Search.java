package main;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;
import main.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Search {


    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    private HashMap<String, String> sites;
    Map<String, String> testSites = new HashMap<String, String>(); //  load list sites from application.yaml
    public Search(SiteRepository siteRepository,
                          PageRepository pageRepository,
                          LemmaRepository lemmaRepository,
                          IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

    }

    public void geCotnfig( Map<String, String> testSites)  {
        this.testSites = testSites;
    }


    public String serchLemms ( String sQuery)  {

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


}
