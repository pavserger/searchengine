package main;

import lombok.Data;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.*;

@ConfigurationProperties(prefix = "spring")
@Component
@Data
public class SQLClass {
    private HashMap <String,String> datasource;


    String url = "";
    //   String url = "jdbc:mysql://localhost:3306/skillbox?useSSL=false&serverTimezone=UTC";
    String usr = "";
    String pass = "";

    /*
    spring:
        datasource:
             username: root
             password: pass4MySQL
             url: jdbc:mysql://localhost:3306/search_engine?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true
    jpa:
    properties:
    hibernate:
    dialect: org.hibernate.dialect.MySQL8Dialect
    hibernate:
    ddl-auto: update
    show-sql: true


    public class ConnectionSettings {
   List<String> names;
   Map<String, String> addresses;
}
*/


    public SQLClass() {
    }
    public void getDataConnection() {

     //   Map<String, String> connect = new HashMap<String, String>(); //  load list sites from application.yaml

/*
        for (var mapSites : datasource.entrySet()) {
                if (mapSites.getKey().contains("url")) {
                    url = mapSites.getValue();
                }
                if (mapSites.getKey().contains("username")) {
                    usr = mapSites.getValue();
                }
                 if (mapSites.getKey().contains("password")) {
                     pass = mapSites.getValue();
                 }


        }
*/

    }


    public void  query() {

        System.out.println("Hi MySQL");

        getDataConnection();

 /*
        String url = "jdbc:mysql://localhost:3306/search_engine?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true";
        //   String url = "jdbc:mysql://localhost:3306/skillbox?useSSL=false&serverTimezone=UTC";
        String usr = "root";
        String pass = "pass4MySQL";
 */

        try {
            Connection connection = DriverManager.getConnection(url, usr, pass);
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM search_engine.index;";
            //     String q2 = "SELECT course_name, MONTH(subscription_date) as month, COUNT(*) AS num_clients FROM purchaselist group by course_name, MONTH(subscription_date);";
            ResultSet resultQury = statement.executeQuery(query);
            resultQury.next();
            var id = resultQury.getInt("id");
            var rank = resultQury.getInt("rank");
            var lemma_id = resultQury.getInt("lemma_id");
            var page_id = resultQury.getInt("page_id");
            System.out.println("SQLClass");

            while (resultQury.next()) {
                System.out.println(id+" !  "+rank+" !  "+ lemma_id+"!"+ page_id );
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        };

    }
}
