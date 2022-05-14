package Indexer;

import com.mongodb.*;

import com.mongodb.util.JSON;
import org.bson.types.ObjectId;

import org.json.JSONObject;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;


//Manager Class for the Search Index Database
public class SearchIndexDBManager {
    MongoClient mongoClient;
    DB SearchIndexDB;
    DBCollection wordsCollection ;
    DBCollection spamCollection;
    DBCollection DateCollection;

    //Constructor which also connects to the DB
    public SearchIndexDBManager() throws UnknownHostException {
        System.setProperty("jdk.tls.trustNameService", "true");
        MongoClientURI uri = new MongoClientURI("mongodb://Ali:1234asd@search-index-shard-00-00.sdm5w.mongodb.net:27017,search-index-shard-00-01.sdm5w.mongodb.net:27017,search-index-shard-00-02.sdm5w.mongodb.net:27017/myFirstDatabase?ssl=true&replicaSet=atlas-9r92iq-shard-0&authSource=admin&retryWrites=true&w=majority");
        this.mongoClient  = new MongoClient(uri);
        SearchIndexDB =  mongoClient.getDB("SearchIndex");
        wordsCollection = SearchIndexDB.getCollection("keywords");
        spamCollection = SearchIndexDB.getCollection("Spam");
    }

    //This function takes a word as input and return its JSON object
    public JSONObject getKeyWord(String keyword){
        DBCursor cur =  wordsCollection.find(new BasicDBObject("word", keyword));
        JSONObject output = null;
        if(cur.size() != 0)
             output = new JSONObject(JSON.serialize(cur.next()));
        return output;
    }

    //this function takes a new URL (Doc object) and adds it to an existing word
    public void updateWordURLList(String keyword,JSONObject URL){
        //Find Query for the Update
        DBObject findQuery = new BasicDBObject("word", keyword);
        //Parse the object and add the right command for updating
        DBObject url_DBObject = (DBObject) JSON.parse(URL.toString());
        DBObject objQuery = new BasicDBObject("urls", url_DBObject);
        DBObject updateQuery = new BasicDBObject("$push",objQuery );
        wordsCollection.update(findQuery, updateQuery);
    }

    //this function takes a new URL (Doc object) and adds it to a new Word
    public void insertNewWord(String keyword,JSONObject URL){
        DBObject url_DBObject = (DBObject) JSON.parse(URL.toString());
        //Empty array to add to the DB since this word doesn't exist
        List <DBObject> ArrayOfUrls = new ArrayList<>();
        ArrayOfUrls.add(url_DBObject);
        BasicDBObject wordEntry = new BasicDBObject("word", keyword)
                .append("urls",ArrayOfUrls);

        wordsCollection.insert(wordEntry);
    }


}
