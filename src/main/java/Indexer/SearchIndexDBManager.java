package Indexer;

import com.mongodb.*;

import com.mongodb.client.*;
import org.bson.Document;


import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;


//Manager Class for the Search Index Database
public class SearchIndexDBManager {
    MongoClient mongoClient;
    MongoDatabase SearchIndexDB;
    MongoCollection<Document> wordsCollection ;
    MongoCollection<Document> spamCollection;
    MongoCollection<Document> DateCollection;

    //Constructor which also connects to the DB
    public SearchIndexDBManager() throws UnknownHostException {
        System.setProperty("jdk.tls.trustNameService", "true");
        ConnectionString connectionString = new ConnectionString("mongodb+srv://Ali:1234asd@search-index.sdm5w.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("SearchIndex");
        this.mongoClient  = mongoClient;
        SearchIndexDB =  database;
        wordsCollection = SearchIndexDB.getCollection("keywords");
        spamCollection = SearchIndexDB.getCollection("Spam");
    }

    //This function takes a word as input and return its JSON object
    public JSONObject getKeyWord(String keyword){
        MongoCursor<Document> cur =  wordsCollection.find(new BasicDBObject("word", keyword)).cursor();
        JSONObject output = null;
        if(cur.hasNext())
             output = new JSONObject(cur.next().toJson());
        return output;
    }

    //This function takes a word as input and return its JSON object
    public JSONObject getKeyWordRegex(String keyword){
        MongoCursor<Document> cur =  wordsCollection.find(new BasicDBObject("word", "/^"+ keyword+"/")).cursor();
        JSONObject output = null;
        if(cur.hasNext())
            output = new JSONObject(cur.next().toJson());
        return output;
    }


    public SearchWord getSearchWordExact(String keyword) {
        MongoCursor<Document> cur =  wordsCollection.find(new BasicDBObject("word", keyword)).cursor();
        SearchWord searchWord = new SearchWord();
        searchWord.word = keyword;
        while (cur.hasNext()) {
            Document doc = cur.next();
            WordData currentData = new WordData((Document) doc.get("position"));
            currentData.url = (String) doc.get("url");
            currentData.count = (int) doc.get("count");
            currentData.lengthOfDoc = (int) doc.get("lengthOfDocument");
            currentData.popularity = (double) doc.get("popularity");
            currentData.filepath = (String) doc.get("filepath");
            searchWord.data.add(currentData);
        }
        searchWord.df = searchWord.data.size();
        return searchWord;
    }

    //this function will insert a hashMap to the DataBase
    public void insertDocumentMap(Map<String,WordData> DocumentMap,UrlData currentData){
        //First we need delete all data related to this index then add the need data
        wordsCollection.deleteMany(new BasicDBObject("url",currentData.URL));
        List <Document> indexerEntry = new ArrayList<>();
        for(Map.Entry<String, WordData> entry: DocumentMap.entrySet())
        {
            indexerEntry.add(new Document("word", entry.getKey())
                            .append("url", entry.getValue().url)
                            .append("count", entry.getValue().count)
                            .append("lengthOfDocument", entry.getValue().lengthOfDoc)
                            .append("popularity" , currentData.popularity)
                            .append("filepath" , currentData.FilePath)
                            .append("position", entry.getValue().position));
        }
        wordsCollection.insertMany(indexerEntry);
    }

    public void insertSpam(String Url){
        spamCollection.insertOne(new Document("url" ,Url));
    }

}
