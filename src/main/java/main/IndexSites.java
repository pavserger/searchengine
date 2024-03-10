package main;

import com.github.tsohr.JSONObject;
import main.model.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;

public class IndexSites {

    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository ;
    private IndexRepository indexRepository;

    public IndexSites(SiteRepository siteRepository, PageRepository pageRepository,
                      LemmaRepository lemmaRepository, IndexRepository indexRepository) throws IOException {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

        findPage();
        findLemms();
        // return result.toString();
    }
  //  public IndexSites() throws IOException {
  //      findPage();
  //      findLemms();
  //  };

    public void findLemms() throws IOException { // find lemm

        List<Site> listSites = siteRepository.findAll();
      //  List<Page> listPage = pageRepository.findAll();
        List<Lemma> listLemms = lemmaRepository.findAll();


        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);

        HashMap<String, Integer> pageLemmas = new HashMap<String, Integer>();

        for (Site site : listSites) {
            Long iSite = site.getId();
            var listPage = pageRepository.findBySite_id(iSite);
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

                            for (Lemma lem : lemmas) {
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

        public void findPage() throws IOException {         // find page
            var listSites = siteRepository.findAll();
            for (Site site : listSites) {

                //      Optional<Site> site = siteRepository.findById(li);

                if (site.getUrl() != null) {


                   String siteMap = new ForkJoinPool().invoke(new FindMap(site, pageRepository));
                  //  List <Page> pages = new ForkJoinPool().invoke(new FindMap(site, pageRepository));
                    site.setLastError(siteMap);
                    site.setType("INDEXED");
                    siteRepository.save(site);

                    // System.out.println(siteMap);
                    JSONObject result = new JSONObject();
                    result.put("result", true);

                } else {
                    JSONObject result = new JSONObject();
                    result.put("result", false);
                    result.put("error", "Нет такого сайта !" + site.getUrl());
                }
            }
            JSONObject result = new JSONObject();
            result.put("result", false);
            result.put("error", "Все прошло успешно");

        }  // findPage

       // return result.toString();
    }










