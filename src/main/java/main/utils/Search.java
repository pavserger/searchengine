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

    private List<Integer> indexList;

    String sQuery = "";

    List<String> listFindLemmas = new ArrayList<>();



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

    public void findListPage() {
        Iterator mapIterator = listLemmas4Find.entrySet().iterator();
        listFindLemmas.clear();
       // List<String> listFindLemmas = new ArrayList<>();
        while (mapIterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) mapIterator.next();
            // заполнение массива леммами
            String lemma = entry.getKey();
            Integer iLemmas = entry.getValue();
            listFindLemmas.add(lemma); // lemmas array

        }

        //   var listLemmasFind = lemmaRepository.findBylemma(lemma);
/*
        if (listFindLemmas.size() == 1) {
            List<Lemma> lemmas = lemmaRepository.findBylemma(listFindLemmas.get(0));
            Lemma l = lemmas.get(0);
            indexList = indexRepository.findBylemma_id(l.getId()); //list page
        } else {

 */
           // string query and query
            String sQ = "";
            String s = "";
            int num = 0;
            //   ArrayList<Integer> listLemmasIndex = new ArrayList<Integer>();
            sQ = "select DISTINCT page_id FROM search_engine.index WHERE page_id IN ";
            for (var nameLemma : listFindLemmas) {   // перебираем все слова из строки запроса
                List<Lemma> lemmas = lemmaRepository.findBylemma(nameLemma);
                  // добавить фильтр для сайтов


                    int indexLemma = lemmas.get(0).getId();
                    Long idSite = lemmas.get(0).getSite().getId();


                    if (num == 0) {
                        sQ = sQ + "(SELECT  page_id FROM search_engine.index where lemma_id =" + indexLemma + ")";
                    } else {
                        sQ = sQ + "and page_id IN " +
                                "(SELECT page_id FROM search_engine.index where lemma_id = " + indexLemma + ")";
                    }
                    ;
                num++;
                //       listLemmasIndex.add(lemmas.get(0).getId());
            }    // перебираем все слова из строки запроса


            sQ = sQ + ";";

            //   indexList = indexRepository.retrieveMultipleRecords();
            System.out.println("Search:    " + sQ);

            SQLClass sqlClass = new SQLClass(siteRepository,pageRepository,lemmaRepository,indexRepository);
            indexList =  sqlClass.query(sQ);



       //}

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

        for (Integer id_page : indexList) {
            // Long l_id_page = Long.valueOf(id_page);
            int i = id_page;
            Page page = pageRepository.findById(i).get();

            strText = page.getContent().toString();
            title = page.getTitlepage().toString();
            uri = page.getPath().toString();
            Long idSitePage = page.getSite().getId();

            site = page.getSite().getUrl().toString();
            siteName = page.getSite().getName().toString();

            //   if (idSitePage == siteID) {

                for (var strFind : listFindLemmas) {

                    JSONObject data = new JSONObject();
                    data.put("site", site);
                    data.put("siteName", siteName);
                    data.put("uri", uri);
                    data.put("title", title);
                    // data.put(  "snippet", "Фрагмент текста,в котором найдены совпадения, <b>"+sQuery+"</b>\n"+ str);
                    String s = serchStrigOut(strText, strFind);
                    data.put("snippet", s);
                    data.put("relevance", 0.93362);
                    datas.put(data);
                }
            //}

        }
        return datas;
    }

    public String serchStrigOut(String text, String strFind) throws IOException {


        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);


        //    System.out.println(lemmaFinder.getLemmaSet(sQuery).toString());
        // String wordSearch = lemmaFinder.getLemmaSet(sQuery).toString();
        String wordSearch = strFind;


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
