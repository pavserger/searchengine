package main;

import main.model.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.RecursiveTask;

public class FindLemmsInPage extends RecursiveTask<String> {
    private SiteRepository siteRepository;
    private PageRepository pageRepository;
    private LemmaRepository lemmaRepository ;
    private IndexRepository indexRepository;

    private DataProcessing dataProcessing;


    List<Site> listSites;

    String url;

    public FindLemmsInPage(String url, SiteRepository siteRepository, PageRepository pageRepository,
                           LemmaRepository lemmaRepository, IndexRepository indexRepository,
                           DataProcessing dataProcessing) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.dataProcessing = dataProcessing;
        this.url = url;


        listSites = siteRepository.findAll();

        if (!url.equals("all")) {
            listSites = dataProcessing.findSite(url);
        }

    }

    @Override
    protected String compute() {

        LuceneMorphology luceneMorph =
                null;
        try {
            luceneMorph = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);



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

        for (FindMap link : allTask) {
            stringBuilder.append(link.join());
        }



        return null;
    }
}
