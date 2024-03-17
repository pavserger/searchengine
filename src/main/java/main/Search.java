package main;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import lombok.Data;
import main.model.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data

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


    public String serchStrigOut  (String text, String sQuery)  {
        String stringOut = "";
        String[] strings = text.split(" ");
        int numWord = 0;
        for (String word : strings) {
            numWord++;
            if (word.equals(sQuery)) {
                numWord = (numWord <= 2) ? 2 : numWord;
                stringOut = strings[numWord-2] + " <b>"+ strings[numWord-1]+"</b>"+
                        " "+  strings[numWord] +" "+  strings[numWord+1];
            }
        }
        return  stringOut;
    }

    public String serchLemms ( String sQuery)  {
        String str = "";
        String title = "";
        String uri = "";

        String site  = "";
        String siteName  = "";

        List<Lemma> lemmas = lemmaRepository.findBylemma(sQuery);

        for (Lemma lemma : lemmas){
            int lem = lemma.getId();
            List <Index> indexList = indexRepository.findBylemma_id(lem);
            //          System.out.println(indexList);

            for (var page : indexList) {
                str = page.getPage().getContent().toString();
                title = page.getPage().getTitlepage().toString();
                uri = page.getPage().getPath().toString();

                site = page.getPage().getSite().getUrl().toString();
                siteName = page.getPage().getSite().getName().toString();


            }


        }

        // find lemma

        JSONObject result = new JSONObject();

        JSONObject data = new JSONObject();
        data.put( "site", site);
        data.put(  "siteName", siteName);
        data.put( "uri", uri);
        data.put(  "title", title);
       // data.put(  "snippet", "Фрагмент текста,в котором найдены совпадения, <b>"+sQuery+"</b>\n"+ str);
        data.put(  "snippet", serchStrigOut (str,sQuery));
        data.put( "relevance", 0.93362);

        JSONArray datas =new JSONArray();
        datas.put(data);


        result.put("result",true);
        result.put("count",lemmas.size());
        result.put("data",datas);
        return result.toString();

    }


}
