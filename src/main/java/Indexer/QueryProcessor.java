package Indexer;

import Crawler.Database;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class QueryProcessor {

    public static void main(String[] args) throws IOException {
      String S = "unhappy Music";
      SearchIndexDBManager SearchIndexDB = new SearchIndexDBManager();
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

      /*List <SearchWord> searchedWords = new ArrayList<>();
      for(String word : ListOfQueries)
      {
          searchedWords.add(SearchIndexDB.getSearchWordExact(word));
      }*/

      System.out.println(ListOfQueries);

    }
}
