package main.utils;
import main.findinbd.SQLClass;
import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
//import lombok.Value;
import org.springframework.stereotype.Component;

//@Value
//@Data
@Component
public class GetDataSource {
    //datasource:
    @Value("${spring.datasource.username}")
    private  String username;
    @Value("${spring.datasource.password}")
    private  String password;
    @Value("${spring.datasource.url}")
    private  String url;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public GetDataSource(String username,String password,String url) {
        this.username = username;
        this.password = password;
        this.url = url;

        SQLClass sqlClass = new SQLClass(username,password,url);
    }
}
