package main.utils;

import com.github.tsohr.JSONObject;
import main.loadbd.FindLemmsInPage;
import main.loadbd.FindMap;
import main.model.*;

import java.io.IOException;
import java.util.List;
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


    }





        public void findPage(String url) throws IOException {         // find page
            var listSites = siteRepository.findAll();

            if (url.equals("all")) {
                listSites = dataProcessing.findSite("all");
                /*
                for (Site updateSite : listSites ) {
                    dataProcessing.updateSite(updateSite.getUrl());
                }
                listSites = siteRepository.findAll();

                 */
            } else {
                listSites = dataProcessing.findSite (url);
                /*
                  for (Site site : listSites) {
                      dataProcessing.updateSite(site.getUrl());

                  }

                 */
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










