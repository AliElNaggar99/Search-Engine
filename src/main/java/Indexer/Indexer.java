package Indexer;
import Crawler.Database;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;


public class Indexer {
    Database crawlerDB;
    SearchIndexDBManager SearchIndexDB;

    public static void main(String[] args) throws IOException {
        Database test = new Database();
        SearchIndexDBManager Index = new SearchIndexDBManager();
        List<String> toBeIndexed = new ArrayList<String>();
        test.getVisited(toBeIndexed);
        Document doc = Jsoup.connect(toBeIndexed.get(0)).get();
        JSONObject word = Index.getKeyWord("hey");
        JSONObject newEntry = new JSONObject();
        newEntry.put("isTitle" , "true");
        newEntry.put("freq" , "123");
        newEntry.put("isHeader" , "true");
        newEntry.put("totalCountInUrl" , "100");
        newEntry.put("header" , "h1");
        newEntry.put("bold" , "143");
        newEntry.put("url" , "www.asjsdad.com");
        //Index.updateWordURLList("hey" , newEntry);
        Index.updateWordURLList("asdasdasdas",newEntry);


        
    }



    public void ParseDocumnet(Document DocFile){

    }


    public Document getDocumentFromURL(String url)
    {
        try {
            Connection con = Jsoup.connect(url);
            Document doc = con.get();
            if (con.response().statusCode() == 200) {
                return doc;
            }
            else {
                return null;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
