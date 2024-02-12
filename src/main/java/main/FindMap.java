package main;//import org.apache.commons.lang3.StringUtils;
import main.model.Page;
import main.model.PageRepository;
import main.model.Site;
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
    private String url;//адрес
    private String level;
    private String tabulate = "";

    private  Site site;

 //   private PageRepository pageRepository;
  //  Page pageRec = new Page();



    private PageRepository pageRepository;
    Page pageRecord;

    private CopyOnWriteArraySet<String> allLinks;//список всех ссылок

    public FindMap(Site site, PageRepository pageRepository) {//инициализируем новый список
        this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.pageRepository = pageRepository;
        this.url = site.getUrl();
        this.allLinks.add(url);
        this.pageRecord = new Page();

    }
    public FindMap(Site site, PageRepository pageRepository, CopyOnWriteArraySet<String> allLinks) {//инициализируем новый список
        this.site = site;
        this.allLinks = new CopyOnWriteArraySet<>();
        this.allLinks = allLinks;

        this.pageRepository = pageRepository;
        this.url = site.getUrl();
        this.pageRecord = new Page();

    }

/*
    public FindMap(String url, CopyOnWriteArraySet<String> allLinks, String tabulate,PageRepository pageRepository) {//передаем url
        this.url = url;
        this.allLinks = allLinks;
        this.tabulate = tabulate + "     ";
        this.pageRepository = pageRepository;
        this.pageRecord = new Page();

    }
*/

    @Override
    protected String compute() {
        /*
        //получаем уровень отступа
        String tabulate = StringUtils.repeat("\t",
                url.lastIndexOf("/") != url.length() - 1 ? StringUtils.countMatches(url, "/") - 2
                        : StringUtils.countMatches(url, "/") - 3);
*/
    //    tabulate =  tabulate +"    ";

        StringBuilder stringBuilder = new StringBuilder(tabulate + url + "\n");
        Set<FindMap> allTask = new TreeSet<>(Comparator.comparing(o -> o.url));

        try {
            Thread.sleep(100);//чтобы не заблокировали

             // pageRecord.setId(0);
              pageRecord.setSite(site);

             // pageRecord.setCode(404);
             // pageRecord.setPath("path");
            // pageRecord.setTitle("Главная страница");
           //   pageRepository.save(pageRecord);

            //url = site.getUrl();

            Document document = Jsoup.connect(url).ignoreContentType(false).maxBodySize(0).get();
           int con =  document.connection().execute().statusCode();
            pageRecord.setCode(con);


            // pageRecord.setContent(document.body().html());

            pageRecord.setTitlepage(document.title().toString());
            pageRecord.setContent(document.body().toString());
            pageRecord.setPath(document.location());
            pageRepository.save(pageRecord);

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
                    FindMap links = new FindMap(site,  pageRepository, allLinks);
                    links.fork();
                    allTask.add(links);
                    allLinks.add(attributeUrl);
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            System.out.println(url);
          //  pageRecord.setCode(e);

        }

        for (FindMap link : allTask) {
            stringBuilder.append(link.join());
        }
        return stringBuilder.toString();
    }
}