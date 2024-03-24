package main;

import lombok.Data;
import main.model.*;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveTask;


@Data
public class FindLemmsInPage extends RecursiveTask<String> {
    private static SiteRepository siteRepository;
    private static PageRepository pageRepository;
    protected static  LemmaRepository lemmaRepository ;
    protected static IndexRepository indexRepository;

    private DataProcessing dataProcessing;

    private Site site;

    private List <Page> listPage;

    protected Page page;

    Long iSite;

    private Boolean first = true;


    private List<Site> listSites;

    String url;


    //  private CopyOnWriteArraySet<Page> allLinks;//список всех ссылок


    public  FindLemmsInPage(Site site, SiteRepository siteRepository, PageRepository pageRepository,
                           LemmaRepository lemmaRepository, IndexRepository indexRepository
                           ) {
        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.dataProcessing = new DataProcessing(siteRepository,pageRepository,lemmaRepository,
                indexRepository);
        this.site = site;

       // Long iSite = site.getId();
        //listPage = pageRepository.findBySite_id(iSite);


            first = true;


    }


    public FindLemmsInPage(Page page, Site site, SiteRepository siteRepository, PageRepository pageRepository,
                           LemmaRepository lemmaRepository, IndexRepository indexRepository)
     throws IOException {


        this.siteRepository = siteRepository;
        this.pageRepository = pageRepository;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;

        this.site = site;

        this.page = page;

        first = false;


    }




   /*
    public FindLemmsInPage(String url, SiteRepository siteRepository, PageRepository pageRepository,
                           LemmaRepository lemmaRepository, IndexRepository indexRepository,
                           DataProcessing dataProcessing) {
*/

    @Override
    protected String compute() {

        try {
            if (!first) {
                writeLemms(site,page);
                first = false;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        FindLemmsInPage findLemmsInPage;

        Long iSite = site.getId();
        listPage = pageRepository.findBySite_id(iSite);

        for (Page page: listPage) {
            try {
                findLemmsInPage = new FindLemmsInPage(page,site,siteRepository,pageRepository,
                        lemmaRepository,indexRepository);
                findLemmsInPage.fork();
                System.out.println("page");

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            findLemmsInPage.join();
        }
       return  "Ok";
    }

    private  synchronized  void writeLemms(Site site, Page page) throws IOException {
        LuceneMorphology luceneMorph =
                new RussianLuceneMorphology();
        LemmaFinder lemmaFinder = new LemmaFinder(luceneMorph);

        String sText = page.getContent();
        HashMap<String, Integer> listLemma = (HashMap<String, Integer>)
                lemmaFinder.collectLemmas(sText);

        //    Map<String, Integer> map = new HashMap<>();
        Iterator mapIterator = listLemma.entrySet().iterator();

        Long iSite = site.getId();

        while (mapIterator.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) mapIterator.next();
            // заполнение массива лемм
            String sKey = entry.getKey();
            Integer iLemms = entry.getValue();


            if (sKey.length() >= 2) {
                List<Lemma> lemmas = lemmaRepository.findBylemma(sKey); // будет заполняться
                lemmas.clear();
                List<Lemma> lemmas2 = lemmaRepository.findBylemma(sKey);


                for (Lemma lem : lemmas2) {                // site
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

            } // key > 2
              /*
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

                 */
            } // sKey > 2
        }

    }
