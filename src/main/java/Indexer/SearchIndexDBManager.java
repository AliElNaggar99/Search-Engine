package Indexer;

import com.mongodb.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;


import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

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

    //this function takes a new URL (Doc object) and adds it to an existing word
    /*public void updateWordURLList(String keyword,JSONObject URL){
        //Find Query for the Update
        Object findQuery = wordsCollection.find(eq("word", keyword)).first();
        if (findQuery != null) {
        }
        //Parse the object and add the right command for updating
        DBObject url_DBObject = (DBObject) JSON.parse(URL.toString());
        DBObject objQuery = new BasicDBObject("urls", url_DBObject);
        DBObject updateQuery = new BasicDBObject("$push",objQuery );
        wordsCollection.updateOne(findQuery, updateQuery);
    }**/

    //this function takes a new URL (Doc object) and adds it to a new Word
    /*public void insertNewWord(String keyword,JSONObject URL){
        DBObject url_DBObject = (DBObject) JSON.parse(URL.toString());
        //Empty array to add to the DB since this word doesn't exist
        List <DBObject> ArrayOfUrls = new ArrayList<>();
        ArrayOfUrls.add(url_DBObject);
        BasicDBObject wordEntry = new BasicDBObject("word", keyword)
                .append("urls",ArrayOfUrls);

        wordsCollection.insertOne(wordEntry);
    }*/


}
