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
import java.util.*;
import java.util.List;

public class Indexer {
    //Databases that we use to access
    Database crawlerDB;
    SearchIndexDBManager SearchIndexDB;
    //Url of the current Page
    String CurrentURL;
    //Lists of Strings that are in the Page
    List<String> titleWords;
    //this matrix will contain each thing in a matrix where the row will represent the header it was found in
    //ex:row 1 -> h1
    List <List <String>> headerMatrix;
    //this contains rest of the words in the document which are less important, which are in paragraphs and lists
    List <String> ParagraphListsWords;
    //this map contains all words in the Document
    Map<String,WordData> DocumentMap = new HashMap<>();
    //Stop Words Set to check if a word is a stop word or no
    String [] StopWords = {"a", "about", "above", "after", "again", "against", "all", "am", "an", "and", "any", "are", "as", "at", "be", "because", "been", "before", "being", "below", "between", "both", "but", "by", "could", "did", "do", "does", "doing", "down", "during", "each", "few", "for", "from", "further", "had", "has", "have", "having", "he", "he'd", "he'll", "he's", "her", "here", "here's", "hers", "herself", "him","himself", "his", "how", "how's", "i", "i'd", "i'll", "i'm", "i've", "if", "in", "into", "is", "it", "it's", "its", "itself", "let's", "me", "more", "most", "my", "myself", "nor", "of", "on", "once", "only", "or", "other", "ought", "our", "ours", "ourselves", "out", "over", "own", "same", "she", "she'd", "she'll", "she's", "should", "so", "some", "such", "than", "that", "that's", "the", "their", "theirs", "them", "themselves", "then", "there", "there's", "these", "they", "they'd", "they'll", "they're", "they've", "this", "those", "through", "to", "too", "under", "until", "up", "very", "was", "we", "we'd", "we'll", "we're", "we've", "were", "what", "what's", "when", "when's", "where", "where's", "which", "while", "who", "who's", "whom", "why", "why's", "with", "would", "you", "you'd", "you'll", "you're", "you've", "your", "yours", "yourself", "yourselves"};
    Set <String> StopWordsSet = new HashSet<String>(List.of(StopWords));
    int LengthOfDoc;

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
        //indexTest.CurrentURL = toBeIndexed.get(0);
        indexTest.ParseDocument(doc);
        indexTest.AddWordsToHashMap();
        //Index.insertDocumentMap(indexTest.DocumentMap);
    }



    void AddWordsToHashMap(){
        AddTitleToHashMap();
        AddHeaderMatrixToHashMap();
        AddBodyToHashMap();
        DocumentMap.remove("");
    }


    void AddTitleToHashMap() {
        for(String Word : titleWords)
        {
            //First we will get the Word form the Map
            WordData CurrentWordData = DocumentMap.get(Word);
            //This mean this word doesn't exist we will create a new object for it and add it
            if(CurrentWordData == null)
            {
                //We will Create a new WordData and Add the Data of Document to it
                CurrentWordData = new WordData();
                CurrentWordData.lengthOfDoc = LengthOfDoc;
                CurrentWordData.url = CurrentURL;
            }
            //Update the Count and Position
            CurrentWordData.count += 1;
            CurrentWordData.position.put("title" , CurrentWordData.position.get("title")+1);
            //Add it Back to the Map
            DocumentMap.put(Word,CurrentWordData);
        }
    }
    void AddHeaderMatrixToHashMap() {
        //First We will get the Header Matrix
        for(int i = 0 ; i < 6 ; i++)
        {
            for (String Word : headerMatrix.get(i))
            {
                //First we will get the Word form the Map
                WordData CurrentWordData = DocumentMap.get(Word);
                //This mean this word doesn't exist we will create a new object for it and add it
                if (CurrentWordData == null) {
                    //We will Create a new WordData and Add the Data of Document to it
                    CurrentWordData = new WordData();
                    CurrentWordData.lengthOfDoc = LengthOfDoc;
                    CurrentWordData.url = CurrentURL;
                }
                //Update the Count and Position
                CurrentWordData.count += 1;
                CurrentWordData.position.put("h"+i, CurrentWordData.position.get("h"+i) + 1);
                //Add it Back to the Map
                DocumentMap.put(Word, CurrentWordData);
            }
        }
    }
    void AddBodyToHashMap() {
        for(String Word : ParagraphListsWords)
        {
            //First we will get the Word form the Map
            WordData CurrentWordData = DocumentMap.get(Word);
            //This mean this word doesn't exist we will create a new object for it and add it
            if(CurrentWordData == null)
            {
                //We will Create a new WordData and Add the Data of Document to it
                CurrentWordData = new WordData();
                CurrentWordData.lengthOfDoc = LengthOfDoc;
                CurrentWordData.url = CurrentURL;
            }
            //Update the Count and Position
            CurrentWordData.count += 1;
            CurrentWordData.position.put("body" , CurrentWordData.position.get("body")+1);
            //Add it Back to the Map
            DocumentMap.put(Word,CurrentWordData);
        }
    }


    //this function will fill the arrays with words
    public void ParseDocument(Document DocFile){
        //first we get the words in the title
        titleWords = RemoveStopWords(List.of(StringPreProcessing(DocFile.title()).split(" ")));
        headerMatrix = new ArrayList<List<String>>();
        Elements docBodyElements = DocFile.body().getAllElements();
        //Now we will populate the headerMatrix
        //Since we only have six h levels we will loop over them
        for(int i = 0 ; i < 6 ; i++)
        {
            headerMatrix.add(new ArrayList<String>());
            Elements headerElements = docBodyElements.select("h"+(i+1));
            headerMatrix.set(i, ListOfWordPreProcessing(headerElements.eachText()));
            LengthOfDoc += headerMatrix.get(i).size();
        }
        //Now we will Select paragraphs and span and list items and dt -> term/name
        ParagraphListsWords = new ArrayList<>();
        ParagraphListsWords.addAll(ListOfWordPreProcessing(docBodyElements.select("p").eachText()));
        ParagraphListsWords.addAll(ListOfWordPreProcessing(docBodyElements.select("span").eachText()));
        ParagraphListsWords.addAll(ListOfWordPreProcessing(docBodyElements.select("li").eachText()));
        ParagraphListsWords.addAll(ListOfWordPreProcessing(docBodyElements.select("dt").eachText()));
        LengthOfDoc += ParagraphListsWords.size() + titleWords.size();
    }

    List <String> ListOfWordPreProcessing(List <String> ListOfWords){
        List <String> toBeAdded = new ArrayList<>();
        for(String text : ListOfWords)
        {
            toBeAdded.addAll(RemoveStopWords(List.of(StringPreProcessing(text).split(" "))));
        }
        return toBeAdded;
    }

    String StringPreProcessing(String S){
        S = S.replaceAll("[^a-zA-Z0-9]", " ");
        S = S.trim().replaceAll(" +", " ");
        return S.toLowerCase();
    }

    List <String> RemoveStopWords(List <String> ListOfWords)
    {
        List <String> ListOfWordsWithoutStopWords = new ArrayList<>();
        for(String Word : ListOfWords)
        {
            if(StopWordsSet.contains(Word) == false)
            {
                ListOfWordsWithoutStopWords.add(Word);
            }
        }
        return ListOfWordsWithoutStopWords;
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
