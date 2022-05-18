package Indexer;

import Crawler.Database;
import Ranker.Ranker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Ranker.RankerDBManager;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class QueryProcessor {

    public static void main(String[] args) throws IOException {
      String S = "school Music";
      SearchIndexDBManager SearchIndexDB = new SearchIndexDBManager();
      RankerDBManager RankerDB = new RankerDBManager();
      List <String> ListOfWords = List.of(S.split(" "));
      List <String> ListOfQueries = new ArrayList<>();
      PorterStemmer Stemmer = new PorterStemmer();
      for(String Word : ListOfWords)
      {
          String temp = Stemmer.stemWord(Word);
          if(temp.endsWith("li"))
          {
            temp = temp.substring(0,temp.length()-2);
          }
          ListOfQueries.add(temp);
      }

      List <SearchWord> searchedWords = new ArrayList<>();
      for(String word : ListOfQueries)
      {
          searchedWords.add(SearchIndexDB.getSearchWordExact(word));
      }
      Ranker rank = new Ranker(RankerDB.getDocumentsSize(), searchedWords);
      LinkedHashMap<UrlData, Double> reverseSortedMap = rank.sortSearched();
      for(Map.Entry<UrlData,Double> entry : reverseSortedMap.entrySet())
      {
          Connection con = Jsoup.connect(entry.getKey().URL);
          Document doc = con.get();
          System.out.println(doc.title());
      }

    }
}
