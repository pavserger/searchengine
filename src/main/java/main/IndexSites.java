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

    private DataProcessing dataProcessing;

    public IndexSites(SiteRepository siteRepository, PageRepository pageRepository,
                      LemmaRepository lemmaRepository, IndexRepository indexRepository) throws IOException {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

        dataProcessing = new DataProcessing(siteRepository,pageRepository,
                lemmaRepository,indexRepository);


    }
  //  public IndexSites() throws IOException {
  //      findPage();
  //      findLemms();
  //  };

    public void findLemmas(String url) throws IOException {      // find lemms

        List<Site> listSites = siteRepository.findAll();
      //  List<Page> listPage = pageRepository.findAll();
        List<Lemma> listLemms = lemmaRepository.findAll();


        if (!url.equals("all")) {
            listSites = dataProcessing.findSite(url);
        }

   //     LuceneMorphology luceneMorph =
    //            new RussianLuceneMorphology();
    //    LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);

        for (var site : listSites) {
            String siteMap = new ForkJoinPool().invoke(new FindLemmsInPage(site,siteRepository,pageRepository,
                    lemmaRepository,indexRepository));
        }


/*
        for (Site site : listSites) {
            Long iSite = site.getId();
            var listPage = pageRepository.findBySite_id(iSite);
          //  pageLemmas.clear();
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
                    Integer iLemms = entry.getValue();


                    if (sKey.length() >= 2) {
                        // int num = entry.getValue();

                        List<Lemma> lemmas = lemmaRepository.findBylemma(sKey); // будет заполняться
                        lemmas.clear();
                        List<Lemma> lemmas2 = lemmaRepository.findBylemma(sKey);


                        for (Lemma lem : lemmas2) {
                            if (lem.getSite().getId() == iSite) {
                                lemmas.add(lem);
                            }
                        }

                        if (lemmas.isEmpty()) {              // the  lemma is no present
                            Lemma lemma = new Lemma();

                            lemma.setSite(site);
                            lemma.setLemma(sKey);
                            lemma.setFrequency(iLemms);
                            lemmaRepository.save(lemma);

                            Index index = new Index();
                            index.setLemma(lemma);
                            float f = entry.getValue();
                            index.setRank(f);
                            index.setPage(page);

                            indexRepository.save(index);


                        } else {

                            for (Lemma lem2 : lemmas) {  // the  lemma is present
                                int fr = lem2.getFrequency() + iLemms;
                                lem2.setFrequency(fr);
                                lemmaRepository.save(lem2);

                                Index index = new Index();
                                index.setLemma(lem2);
                                float f = entry.getValue();
                                index.setRank(f);
                                index.setPage(page);

                                indexRepository.save(index);

                            }
                        }
                    } // sKey > 2
                }

            } // page
        }  // site

 */
    }





        public void findPage(String url) throws IOException {         // find page
            var listSites = siteRepository.findAll();
            if (!url.equals("all")) {
                listSites = dataProcessing.findSite(url);
            }

            for (Site site : listSites) {

                //      Optional<Site> site = siteRepository.findById(li);

                if (site.getUrl() != null) {


                   String siteMap = new ForkJoinPool().invoke(new FindMap(site, pageRepository));
                  //  List <Page> pages = new ForkJoinPool().invoke(new FindMap(site, pageRepository));
                    site.setLastError(siteMap);
                    if (siteMap.equals("Все хорошо")) {
                        site.setType("INDEXED");
                    } else {
                        site.setType("FAILED");
                    };

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










