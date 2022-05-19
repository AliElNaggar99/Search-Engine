package Indexer;

import Crawler.Crawler;
import Crawler.Database;
import Ranker.RankerDBManager;
import Ranker.pageRanker;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Thread.currentThread;
import java.util.logging.Logger;
import java.util.logging.Level;


public class IndexerEngine {
    //Databases that we use to access

    public static void main(String[] args) throws IOException {
        Logger mongoLogger = Logger.getLogger( "org.mongodb.driver" );
        mongoLogger.setLevel(Level.SEVERE);
        RankerDBManager RankerDB = new RankerDBManager();
        SearchIndexDBManager SearchIndexDB = new SearchIndexDBManager();
        ///Run PageRanker Algorithms
        pageRanker ranker = new pageRanker(1,0.85);
        List <UrlData> toBeIndexed = RankerDB.getAllURLsData();
        ExecutorService executor = Executors.newFixedThreadPool(20);
        for(int i = 0 ; i < toBeIndexed.size() ; i++)
        {
            executor.execute(new Indexer(toBeIndexed.get(i),RankerDB,SearchIndexDB));
        }
        executor.shutdown();
    }

}
