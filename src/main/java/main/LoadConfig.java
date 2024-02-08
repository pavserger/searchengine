package main;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@ConfigurationProperties(prefix = "indexing-settings")
@Component
@Data
public class LoadConfig {


   private List <SiteProps> sites;


 //   @Autowired

   public List<SiteProps> getSites() {
      return sites;
   }


   @Override
   public String toString() {
      return "{" + this.getSites() + "}";
   }
}

@Data
@Component
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

