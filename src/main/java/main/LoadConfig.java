package main;

import lombok.Data;
import main.model.Site;
import main.model.SiteRepository;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@ConfigurationProperties(prefix = "indexing-settings.sites")
@Component
@Data
public class LoadConfig {

   private List <String> sites;
 //   Map <String, String> url;
 //   Map <String, String> name;

 //   @Autowired
    private SiteRepository siteRepository;

    //SiteStorage siteStorage = new SiteStorage(siteRepository);

   private  LoadConfig() {
       /*
       SiteConf siteConf = new SiteConf();
       siteConf.setUrl(" - url","https://www.lenta.ru" );
       siteConf.setName("name","Лента.ру");
       sites.add(siteConf);

        */

       Site site = new Site();
       LocalDateTime dateTime = LocalDateTime.now();
    //   for (int i = 0; i< 20; i++) {


            site.setName("Лента.ру");
            site.setUrl("https://www.lenta.ru");
           site.setType("INDEXED");
           site.setStatusTime(dateTime);
     //      siteStorage.addSite(site);

   //    }
   }


}



//public class Config {
//}
