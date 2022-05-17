package Indexer;
import Crawler.Database;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Indexer {
    //Databases that we use to access
    Database crawlerDB;
    SearchIndexDBManager SearchIndexDB;
    //Lists of Strings that are in the Page
    List<String> titleWords;
    //this matrix will contain each thing in a matrix where the row will represent the header it was found in
    //ex:row 1 -> h1
    List <List <String>> headerMatrix;
    //this contains rest of the words in the document which are less important, which are in paragraphs and lists
    List <String> ParagraphListsWords;
    //this map contains all words in the Document
    Map<String,WordData> DocumentMap;

    public Indexer(){

    }

    public static void main(String[] args) throws IOException {
        Database test = new Database();
        SearchIndexDBManager Index = new SearchIndexDBManager();
        List<String> toBeIndexed = new ArrayList<String>();
        Indexer indexTest = new Indexer();
        test.getVisited(toBeIndexed);
        Document doc = Jsoup.connect(toBeIndexed.get(0)).get();
        JSONObject word = Index.getKeyWord("hey");
        //System.out.println(doc);
        JSONObject newEntry = new JSONObject();
        newEntry.put("isTitle" , "true");
        newEntry.put("freq" , "123");
        newEntry.put("isHeader" , "true");
        newEntry.put("totalCountInUrl" , "100");
        newEntry.put("header" , "h1");
        newEntry.put("bold" , "143");
        newEntry.put("url" , "www.asjsdad.com");
        //Index.updateWordURLList("hey" , newEntry);
        //Index.updateWordURLList("asdasdasdas",newEntry);
        indexTest.ParseDocument(doc);


    }


    String StringPreProcessing(String S){
        S = S.replaceAll("[^a-zA-Z0-9]", " ");
        S = S.trim().replaceAll(" +", " ");
        return S.toLowerCase();
    }
    //this function will fill the arrays with words
    public void ParseDocument(Document DocFile){
        //first we get the words in the title
        titleWords = List.of(StringPreProcessing(DocFile.title()).split(" "));
        headerMatrix = new ArrayList<List<String>>();
        Elements docBodyElements = DocFile.body().getAllElements();
        //Now we will populate the headerMatrix
        //Since we only have six h levels we will loop over them
        for(int i = 0 ; i < 6 ; i++)
        {
            headerMatrix.add(new ArrayList<String>());
            Elements headerElements = docBodyElements.select("h"+(i+1));
            headerMatrix.set(i, headerElements.eachText());
        }
        //Now we will Select paragraphs and span and list items and dt -> term/name
        ParagraphListsWords = new ArrayList<>();
        ParagraphListsWords.addAll(docBodyElements.select("p").eachText());
        ParagraphListsWords.addAll(docBodyElements.select("span").eachText());
        ParagraphListsWords.addAll(docBodyElements.select("li").eachText());
        ParagraphListsWords.addAll(docBodyElements.select("dt").eachText());
        System.out.println(titleWords);
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
