package Indexer;

import Crawler.Crawler;
import Crawler.Database;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IndexerEngine {
    //Databases that we use to access

    public static void main(String[] args) throws IOException {
        Database CrawlerDB = new Database();
        List <UrlData> toBeIndexed = CrawlerDB.getAllURLsData();
        File input = new File(toBeIndexed.get(0).FilePath);
        Document doc = Jsoup.parse(input,"UTF-8");
        System.out.println(doc);

    }

}
