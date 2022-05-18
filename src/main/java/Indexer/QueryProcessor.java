package Indexer;

import Crawler.Database;
import Ranker.Ranker;

import java.io.IOException;
import java.net.UnknownHostException;
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
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class QueryProcessor {
    SearchIndexDBManager SearchIndexDB;
    RankerDBManager RankerDB;
    public QueryProcessor() throws UnknownHostException {
        SearchIndexDB = new SearchIndexDBManager();
        RankerDB = new RankerDBManager();
    }

    public LinkedHashMap<UrlData, Double> getResults(String S){
        List <String> ListOfWords = List.of(S.split(" "));
        List <String> ListOfQueries = new ArrayList<>();
        PorterStemmer Stemmer = new PorterStemmer();
        List <SearchWord> searchedWords = new ArrayList<>();
        for(String Word : ListOfWords)
        {
            searchedWords.add(SearchIndexDB.getSearchWordExact(Stemmer.stemWord(Word)));
        }
        Ranker rank = new Ranker(RankerDB.getDocumentsSize(), searchedWords);
        LinkedHashMap<UrlData, Double> reverseSortedMap = rank.sortSearched();
        return reverseSortedMap;
    }

    public String getDescription(Document doc , String word){
        for (Element metaTag : doc.getElementsByTag("meta")) {
            if (metaTag.attr("name").toLowerCase().equals("description")) {
                //Get the Description
                String Description = metaTag.attr("content");
                if(Description.isEmpty())
                    break;
                return Description;
            }
        }
        //We will Get first Sentence that contains this word
        if(doc.title().contains(word))
            return doc.title();
        //Check P
        List<String> ParagraphText = doc.getElementsByTag("p").eachText();
        for(String Paragraph : ParagraphText)
        {
            if(Paragraph.contains(word))
            {
                return Paragraph;
            }
        }
        //Check span
        ParagraphText = doc.getElementsByTag("span").eachText();
        for(String Paragraph : ParagraphText)
        {
            if(Paragraph.contains(word))
            {
                return Paragraph;
            }
        }
        //Check Headers
        Elements docBodyElements = doc.body().getAllElements();
        //Since we only have six h levels we will loop over them
        for(int i = 0 ; i < 6 ; i++)
        {
            Elements headerElements = docBodyElements.select("h"+(i+1));
            List<String> Text = headerElements.eachText();
            for(String Paragraph : Text)
            {
                if(Paragraph.contains(word))
                {
                    return Paragraph;
                }
            }
        }
        //Check li
        ParagraphText = doc.getElementsByTag("li").eachText();
        for(String Paragraph : ParagraphText)
        {
            if(Paragraph.contains(word))
            {
                return Paragraph;
            }
        }
        //check dt
        ParagraphText = doc.getElementsByTag("dt").eachText();
        for(String Paragraph : ParagraphText)
        {
            if(Paragraph.contains(word))
            {
                return Paragraph;
            }
        }
        return "No Description";
    }

    public static void main(String[] args) throws IOException {

    }
}
