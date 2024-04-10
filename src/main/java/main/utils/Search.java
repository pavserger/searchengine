package main.utils;

import com.github.tsohr.JSONArray;
import com.github.tsohr.JSONObject;

import lombok.Data;
import main.findinbd.LemmaFinder;
import main.findinbd.SQLClass;
import main.model.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.*;

@Data

public class Search {


    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository;
    private IndexRepository indexRepository;

    private HashMap<String, String> sites;
    Map<String, String> testSites = new HashMap<String, String>(); //  load list sites from application.yaml

    public Map<String, Integer> listLemmas4Find = new HashMap<String, Integer>(); //  load list lemmas for find

    private SortedMap<Integer, String> sortedLemmas = new TreeMap<>(); // sortet lemmas

    private Map<String, Integer> orderLemmas = new HashMap<String, Integer>();       //  orderLemmas

    private Long siteID;

    private List<Index> indexList;

    String sQuery = "";


    public Search(SiteRepository siteRepository,
                  PageRepository pageRepository,
                  LemmaRepository lemmaRepository,
                  IndexRepository indexRepository) {

        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

    }

    public String serchLemmas(String sQuery, Long iSite) throws IOException {

        this.siteID = iSite;

        getListLemmas(sQuery);

        //sortListLemmas();

        findListPage();

        //   String resalt =formResult();

        List<Lemma> lemmas = lemmaRepository.findBylemma(sQuery);

        int numLemmas = 0;

        for (Lemma lemma : lemmas) {
            int lem = lemma.getId();
            var indexList = indexRepository.findBylemma_id(lem);
            numLemmas = indexList.size();
            //          System.out.println(indexList);
        }


        JSONObject result = new JSONObject();
        result.clear();
        result.put("result", true);
        result.put("count", numLemmas);
        result.put("data", formResult());
        return result.toString();

    }

    public void getCotnfig(Map<String, String> testSites) {
        this.testSites = testSites;
    }


    public void getListLemmas(String qString) throws IOException {
        this.listLemmas4Find.clear();

        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);

        String sText = qString;
        listLemmas4Find = (HashMap<String, Integer>)
                lemmaFinder.collectLemmas(sText);

    }

    public void sortListLemmas() {
        sortedLemmas.clear();

        Iterator mapIterator = listLemmas4Find.entrySet().iterator();

        while (mapIterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) mapIterator.next();
            // заполнение массива лемм
            String lemma = entry.getKey();
            Integer iLemmas = entry.getValue();

            var listLemmasFind = lemmaRepository.findBylemma(lemma);

            for (var lemmaFind : listLemmasFind) {
                if (siteID.equals(lemmaFind.getSite().getId())) {
                    sortedLemmas.put(lemmaFind.getFrequency(), lemmaFind.getLemma());
                }
            }
        }
    }


    public void findListPage() {
        Iterator mapIterator = listLemmas4Find.entrySet().iterator();
        List<String> listFindLemmas = new ArrayList<>();
        while (mapIterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) mapIterator.next();
            // заполнение массива леммами
            String lemma = entry.getKey();
            Integer iLemmas = entry.getValue();
            listFindLemmas.add(lemma);

        }

        //   var listLemmasFind = lemmaRepository.findBylemma(lemma);

        if (listFindLemmas.size() == 1) {
            List<Lemma> lemmas = lemmaRepository.findBylemma(listFindLemmas.get(0));
            Lemma l = lemmas.get(0);
            indexList = indexRepository.findBylemma_id(l.getId()); //list page
        } else {   // string query and query
            String stringQuery = "";
            String s = "";
            int num = 0;
            //   ArrayList<Integer> listLemmasIndex = new ArrayList<Integer>();
            String sQ = "select * FROM search_engine.index WHERE page_id IN";
            for (var nameLemma : listFindLemmas) {
                List<Lemma> lemmas = lemmaRepository.findBylemma(nameLemma);
                if (lemmas.size() == 1) {
                    int indexLemma = lemmas.get(0).getId();
                    if (num == 0) {
                        sQ = sQ + "(SELECT page_id FROM search_engine.index where lemma_id =" + indexLemma + ")";
                    } else {
                        sQ = sQ + "and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = " + indexLemma + ")";
                    }
                    ;
                }
                num++;
                //       listLemmasIndex.add(lemmas.get(0).getId());
            }


            sQ = sQ + ";";

         //   indexList = indexRepository.retrieveMultipleRecords();
            System.out.println("Search" + "Indxlist");

            SQLClass sqlClass = new SQLClass();
            sqlClass.query();

/*
            if (!sQ.equals(";")) {

                String url = "jdbc:mysql://localhost:3306/";
                String usr = "root";
                String pass = "pass4MySQL";


                SQLClass sqlClass = new SQLClass();
                sqlClass.query();

                /*
                System.out.println("Search.findlistpage !!"+ sQ);
                 List<Index> indexList2 = new ArrayList<>();
                 sQ = "SELECT * FROM search_engine.index;";
                indexList2 = indexRepository.findByLemmas_id(sQ);
                System.out.println("Search.findlistpage");

        }

/*
            SELECT * FROM search_engine.index
            WHERE page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 433)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 789)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 464)
            and page_id IN (SELECT page_id FROM search_engine.index where lemma_id = 266);
*/

    }
}








//          при наличии других лемм провести пересечение





    public JSONArray formResult() throws IOException {
        String strText = "";
        String title = "";
        String uri = "";

        String site = "";
        String siteName = "";

        JSONArray datas = new JSONArray();
        datas.clear();

        for (var page : indexList) {
            strText = page.getPage().getContent().toString();
            title = page.getPage().getTitlepage().toString();
            uri = page.getPage().getPath().toString();

            site = page.getPage().getSite().getUrl().toString();
            siteName = page.getPage().getSite().getName().toString();

            JSONObject data = new JSONObject();
            data.put("site", site);
            data.put("siteName", siteName);
            data.put("uri", uri);
            data.put("title", title);
            // data.put(  "snippet", "Фрагмент текста,в котором найдены совпадения, <b>"+sQuery+"</b>\n"+ str);
            String s = serchStrigOut(strText);
            data.put("snippet", s);
            data.put("relevance", 0.93362);
            datas.put(data);

        }
        return datas;
    }

    public String serchStrigOut(String text) throws IOException {


        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);


        //    System.out.println(lemmaFinder.getLemmaSet(sQuery).toString());
        // String wordSearch = lemmaFinder.getLemmaSet(sQuery).toString();
        String wordSearch = sQuery;


        String stringOut = "";
        String[] strings = text.split(" ");

        int numWord = 0;
        for (String wordTest : strings) {
            var  wordsArray = lemmaFinder.getLemmaSet(wordTest).toArray();
            String word;
            if (wordsArray.length > 0) {
                word = wordsArray[0].toString();
            } else {
                word = "";
            }
            numWord++;
            if (word.equals(wordSearch)) {
                String stringFirst = "";
                for (int i = -5; i < -1; i++) {
                    if ((numWord + i) > 0) {
                        stringFirst = stringFirst + strings[numWord + i] + " ";
                    }
                }
                stringOut = stringFirst + " <b>" + strings[numWord - 1] + "</b>" + "  ";
                for (int i = 0; i < 15; i++) {
                    if ((numWord + i) < strings.length) {
                        stringOut = stringOut + strings[numWord + i] + " ";
                    }
                }

                //  stringOut = strings[numWord-2] + " <b>"+ strings[numWord-1]+"</b>"+
                //          " "+  strings[numWord] +" "+  strings[numWord+1];
            }
        }
        return stringOut;
    }
}
