package main;//import org.apache.commons.lang3.StringUtils;
import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveTask;

public class FindMap extends RecursiveTask<String> {
    private String url;//адрес
    private String tabulate = "";
int i = 0;
    private  Site site;

    private static   PageRepository pageRepository;
    Page pageRecord;
    List<Page> pages = new ArrayList<Page>();

    private CopyOnWriteArraySet<String> allLinks;//список всех ссылок

    public FindMap(Site site, PageRepository pageRepository) {//инициализируем новый список
        this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.pageRepository = pageRepository;
        this.url = site.getUrl();
        this.allLinks.add(url);
      //  this.pageRecord = new Page();

    }
    public FindMap( Site site, CopyOnWriteArraySet<String> allLinks) {//инициализируем новый список
       this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.allLinks = allLinks;

        //     this.pageRepository = pageRepository;
        this.url = site.getUrl();
      //  this.pageRecord = new Page();

    }



    @Override
    protected String compute() {


        StringBuilder stringBuilder = new StringBuilder(tabulate + url + "\n");
        Set<FindMap> allTask = new TreeSet<>(Comparator.comparing(o -> o.url));

        try {
            Thread.sleep(100);//чтобы не заблокировали

            Document document = Jsoup.connect(url).ignoreContentType(true).maxBodySize(204800).get();
            // Jsoup.connect(url).
            int con =  document.connection().execute().statusCode();

            pageRecord = new Page();
            pageRecord.setSite(site);
            url = site.getUrl();

            pageRecord.setCode(con);


            // pageRecord.setContent(document.body().html());

            pageRecord.setTitlepage(document.title().toString());
           // System.out.println("title"+ "-------------------------------");
           // System.out.println(document.title().toString());

           // pageRecord.setContent(document.body().text().toString());
            String s = document.body().text().toString();
            s.replaceAll("[^а-яёА-ЯЁ\\s]", "");
            pageRecord.setContent(s);

          //  System.out.println("body"+ "-------------------------------");
          //  System.out.println (s);

            pageRecord.setPath(document.location().toString());
          //  System.out.println("location"+ "-------------------------------");
          //  System.out.println(document.location().toString());
/*
            String spage = pageRecord.toString();
            if ((pageRepository != null) & ( pageRecord != null) ) {
                pageRepository.save(pageRecord);
            }

 */
            pages.add(pageRecord);
        //    i = i++;

            Elements elements = document.select("a[href]");

            for (Element element : elements) {
                String attributeUrl = element.absUrl("href");//все ссылки сайта

                if (attributeUrl.startsWith(url)
                        && !allLinks.isEmpty()
                        && !attributeUrl.contains("#")
                        && !allLinks.contains(attributeUrl)
                        && ! attributeUrl.equals(null))
                {

                    site.setUrl(attributeUrl);
                    FindMap links = new FindMap(site, allLinks);
                    links.fork();
                    allTask.add(links);
                    allLinks.add(attributeUrl);
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.out.println(url);
          //  pageRecord.setCode(e);
          //  for (var pg: pages){
                //  pageRecord = new Page ();
                //  System.out.println(pg.toString());
             //   pageRepository.save(pg);
          //  }

        }

        for (FindMap link : allTask) {
            stringBuilder.append(link.join());
        }
        for (var pg: pages){
            if (pg != null) {
                String s = pg.getContent();
                s.replaceAll("[^а-яёА-ЯЁ\\s]", "");
          //      System.out.println(s);
                pg.setContent(s.toString());

                s= pg.getTitle();
                if (s != null) {
                    s.replaceAll("[^а-яёА-ЯЁ\\s]", "");
                } else {
                    s = "без названия";
                }
                pg.setTitle(s);
                pageRepository.save(pg);
            }
        }
        return stringBuilder.toString();

    }
}