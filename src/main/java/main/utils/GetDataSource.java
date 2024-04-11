package main.utils;
import main.findinbd.SQLClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import lombok.Data;
//import lombok.Value;
import org.springframework.stereotype.Component;

//@Value
//@Data
@Component
public class GetDataSource {
    //datasource:

/*
    @Value("${spring.datasource.username}")
    public   String username;
    @Value("${spring.datasource.password}")
    public   String password;
    @Value("${spring.datasource.url}")
    public String url;
*/

    public static   String username;
    public static   String password;
    public static   String url;

    @Value("${spring.datasource.username}")
    @Autowired
    public void setUsername(String username) {
        this.username = username;
    }
    @Value("${spring.datasource.password}")
    @Autowired
    public void setPassword(String password) {
        this.password = password;
    }
    @Value("${spring.datasource.url}")
    @Autowired
    public void setUrl(String url) {
        this.url = url;
    }

    @Autowired
    public GetDataSource() {}


    @Autowired
    public String getUsername() {
        return username;
    }
    @Autowired

    public String getPassword() {
        return password;
    }
    @Autowired
    public String getUrl() {
        return url;
    }

}
