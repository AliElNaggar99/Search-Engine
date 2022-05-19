package Ranker;

import java.net.UnknownHostException;
import java.util.*;

import Crawler.Database;
import Indexer.UrlData;

public class pageRanker {

    Database d1;
    RankerDBManager RankerDB;
    {
        try {
            d1 = new Database();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }


    public Map<String, Set<String>> href= new HashMap<String,Set<String>>();
    public Map<String, Set<String>> parents= new HashMap<String,Set<String>>();
    public Map<String, UrlData> popularity= new HashMap<String,UrlData>();
    //public Map <String , String > UrlandFilePath = new HashMap<>();


    public pageRanker(int noOfIter,double f)
    { double value=0;
        RankerDB = new RankerDBManager();
        List<UrlData> urls=d1.getAllURLsData();
        for(UrlData url : urls)
        {
            href.put(url.URL, d1.getHref(url.URL));
            parents.put(url.URL, d1.getParentURLs(url.URL));
        }
        for(int i=0;i<noOfIter;i++)
        {
            for(UrlData url : urls)
            {
                System.out.println(url);
                for(String parent : parents.get(url.URL))
                {

                    value+=((popularity.get(parent)==null)?0:popularity.get(parent).popularity)/href.get(parent).size();
                }
                double rank=(1-f)+f*(value);
                url.popularity=rank;
                popularity.put(url.URL,url);
                value=0;
                System.out.println(popularity);
            }
        }
        RankerDB.insertUrlMap(popularity);
    }
    public static void main(String []args) {
        pageRanker r1= new pageRanker(1000,0.85);
        System.out.println("popularity " + r1.popularity);
        System.out.println(r1.popularity.size());
        System.out.println(r1.d1.getAllURLs().size());
    }
}
