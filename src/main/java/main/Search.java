package main;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import lombok.Data;
import main.model.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
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


    public String serchStrigOut  (String text, String sQuery) throws IOException {


        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);


    //    System.out.println(lemmaFinder.getLemmaSet(sQuery).toString());
        String wordSearch = lemmaFinder.getLemmaSet(sQuery).toString();


        String stringOut = "";
        String[] strings = text.split(" ");

        int numWord = 0;
        for (String wordTest : strings) {
            String word = lemmaFinder.getLemmaSet(wordTest).toString();
            numWord++;
            if (word.equals(wordSearch)) {
                String stringFirst = "";
                for (int i = -5; i < -1 ; i++ ) {
                    if ((numWord+i) > 0) {
                        stringFirst = stringFirst + strings[numWord + i] + " ";
                    }
                }
                stringOut = stringFirst+ " <b>"+ strings[numWord-1]+"</b>" + "  ";
                for (int i = 0; i < 15 ; i++ ) {
                    if ((numWord+i) < strings.length) {
                        stringOut = stringOut + strings[numWord + i] + " ";
                    }
                }

              //  stringOut = strings[numWord-2] + " <b>"+ strings[numWord-1]+"</b>"+
              //          " "+  strings[numWord] +" "+  strings[numWord+1];
            }
        }
        return  stringOut;
    }

    public String serchLemms ( String sQuery) throws IOException {





        String str = "";
        String title = "";
        String uri = "";

        String site  = "";
        String siteName  = "";

        List<Lemma> lemmas = lemmaRepository.findBylemma(sQuery);
        JSONArray datas =new JSONArray();
        datas.clear();

       int numLemmas = 0;

        for (Lemma lemma : lemmas){
            int lem = lemma.getId();
             var indexList = indexRepository.findBylemma_id(lem);
            numLemmas = indexList.size();
            //          System.out.println(indexList);

            for (var page : indexList) {
                str = page.getPage().getContent().toString();
                title = page.getPage().getTitlepage().toString();
                uri = page.getPage().getPath().toString();

                site = page.getPage().getSite().getUrl().toString();
                siteName = page.getPage().getSite().getName().toString();

                JSONObject data = new JSONObject();
                data.put( "site", site);
                data.put(  "siteName", siteName);
                data.put( "uri", uri);
                data.put(  "title", title);
                // data.put(  "snippet", "Фрагмент текста,в котором найдены совпадения, <b>"+sQuery+"</b>\n"+ str);
                data.put(  "snippet", serchStrigOut (str,sQuery));
                data.put( "relevance", 0.93362);
                datas.put(data);

            }
        }

        JSONObject result = new JSONObject();
        result.clear();
        result.put("result",true);
        result.put("count",numLemmas);
        result.put("data",datas);
        return result.toString();

    }


}
