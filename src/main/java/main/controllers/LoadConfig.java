package main.controllers;

import lombok.Builder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@ConfigurationProperties(prefix = "indexing-settings")
@Component
@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
public class LoadConfig {

   private Map <String,String> sites;

   public LoadConfig() {
   }

   //   @Autowired

 //  public List<SiteProps> getSiteProps() {
 //        System.out.println(sites.get(0).getName());
 //     return getSiteProps();
  // }


   @Override

   public String toString() {
      return "{"+sites.toString()+ "}";
   }
}


@Component
//@ConfigurationProperties(prefix = "sites")
@Data
class SiteProps {

   private String url;
   private String name;

}



/*
indexing-settings:
        sites:
        - url: https://www.lenta.ru
        name: Лента.ру
        - url: https://www.skillbox.ru
        name: Skillbox
        - url: https://www.playback.ru
        name: PlayBack.Ru

*/

