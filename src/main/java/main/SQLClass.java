package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class SQLClass {

    public SQLClass() {
    }
    public void  query() {

        System.out.println("Hi MySQL");
        String url = "jdbc:mysql://localhost:3306/search_engine?useSSL=false&requireSSL=false&allowPublicKeyRetrieval=true";
        //   String url = "jdbc:mysql://localhost:3306/skillbox?useSSL=false&serverTimezone=UTC";
        String usr = "root";
        String pass = "pass4MySQL";
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

