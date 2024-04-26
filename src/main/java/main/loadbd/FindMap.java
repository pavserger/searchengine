package main.loadbd;//import org.apache.commons.lang3.StringUtils;
import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
import main.utils.DataProcessing;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveTask;

public class FindMap extends RecursiveTask<String> {
    private String url = "";
    private String level;
    private String tabulate = "";

    private  Site site;

 //   private PageRepository pageRepository;
  //  Page pageRec = new Page();



    private PageRepository pageRepository;
    Page pageRecord;

    private DataProcessing dataProcessing = new DataProcessing(false);

    private CopyOnWriteArraySet<String> allLinks;//список всех ссылок

    public FindMap(Site site, PageRepository pageRepository) {//инициализируем новый список
        this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.pageRepository = pageRepository;
        this.url = site.getUrl();
        this.allLinks.add(url);
        this.pageRecord = new Page();

      //  this.dataProcessing = new DataProcessing(false);


    }
    public FindMap(Site site ,String url, PageRepository pageRepository, CopyOnWriteArraySet<String> allLinks) {//инициализируем новый список
        this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.allLinks = allLinks;
        this.pageRepository = pageRepository;
        this.url = url;
     //   this.pageRecord = new Page();
      //  this.dataProcessing = new DataProcessing(false);
    }



    @Override
    protected String compute() {


        StringBuilder stringBuilder = new StringBuilder(tabulate + url + "\n");
        Set<FindMap> allTask = new TreeSet<>(Comparator.comparing(o -> o.url));



        try {
            Thread.sleep(200);//чтобы не заблокировали
          /*

            if (dataProcessing.isStopStopIndexing()) {
                Thread.interrupted();
            }

           */
            pageRecord = new Page();
            pageRecord.setSite(site);
            //url = site.getUrl();

            if (url == "") {url = site.getUrl();}

            Document document = Jsoup.connect(url).ignoreContentType(true).maxBodySize(204800).get();
           // Jsoup.connect(url).
           int con =  document.connection().execute().statusCode();
            pageRecord.setCode(con);
            String s = document.title().toString().replaceAll("[^A-Za-zА-Яа-я0-9.\\s]", "");
            pageRecord.setTitlepage(s);
            String s2 = document.body().text().toString().replaceAll("[^A-Za-zА-Яа-я0-9.\\s]", "");
            pageRecord.setContent(s2);

         //   String s3 = document.location().toString();
            pageRecord.setPath(document.location().toString());
          //  System.out.println("location"+ "-------------------------------");
          //  System.out.println(document.location().toString());
            pageRepository.save(pageRecord);
            Elements elements = document.select("a[href]");

            for (Element element : elements) {
                String attributeUrl = element.absUrl("href");//все ссылки сайта

              //  System.out.println(attributeUrl);

                if (attributeUrl.startsWith(url)
                        && !allLinks.isEmpty()
                        && !attributeUrl.contains("#")
                        && !allLinks.contains(attributeUrl)
                        && ! attributeUrl.equals(null))
                {

                 //   site.setUrl(attributeUrl);
                    if (!dataProcessing.isStopStopIndexing()) {

                    FindMap links = new FindMap(site, attributeUrl, pageRepository, allLinks);

                    links.fork();
                    allTask.add(links);
                    allLinks.add(attributeUrl);
                }
                }
            }
        } catch (InterruptedException | IOException   e) {
            e.printStackTrace();
           // System.out.println(url);
            String sOut = "Ошибка !!  " + e.toString() + " " + url;
            return sOut;

            //  pageRecord.setCode(e);

        }

        for (FindMap link : allTask) {
            stringBuilder.append(link.join());
        }
        return "Все хорошо";
    }
}