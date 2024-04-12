package main.findinbd;

import lombok.Data;
import main.utils.DataProcessing;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//@ConfigurationProperties(prefix = "spring")
@Component
@Data
//@Value
public class SQLClass {
    private HashMap <String,String> datasource;

    public List <Integer>  query(String sQ) {


        DataProcessing dataProcessing = new DataProcessing();

        String url = dataProcessing.url;
        String usr = dataProcessing.user;
        String pass = dataProcessing.password;

        List<Integer> pageList = new ArrayList<>();


        try {
            Connection connection = DriverManager.getConnection(url, usr, pass);
            Statement statement = connection.createStatement();
            String query = sQ;
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
                pageList.add(new Integer(page_id));
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        };


        return pageList;
    }
}

