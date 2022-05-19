package Ranker;

import Indexer.UrlData;
import Indexer.WordData;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RankerDBManager {

    MongoClient mongoClient;
    MongoDatabase RankerDB;
    MongoCollection<Document> urlsCollection;


    public RankerDBManager(){
        System.setProperty("jdk.tls.trustNameService", "true");
        //For Local connecting to the database
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        //ConnectionString connectionString = new ConnectionString("mongodb+srv://Ali:1234asd@search-index.sdm5w.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("SearchIndexDB");
        this.RankerDB = database;
        this.mongoClient  = mongoClient;
        urlsCollection = RankerDB.getCollection("urls");
    }

    //this function will insert a hashMap to the DataBase
    public void insertUrlMap(Map<String, UrlData> urlMap){
        //First we need delete all data related to this index then add the need data
        urlsCollection.deleteMany(new Document());
        List <Document> urlEntry = new ArrayList<>();
        for(Map.Entry<String, UrlData> entry: urlMap.entrySet())
        {
            urlEntry.add(new Document("url", entry.getKey())
                    .append("filepath", entry.getValue().FilePath)
                    .append("popularity", entry.getValue().popularity)
                    .append("indexed", 0));
        }
        urlsCollection.insertMany(urlEntry);
    }

    //To get the URL with the Text Location
    public List<UrlData> getAllURLsData() {
        MongoCursor<Document> cur = urlsCollection.find(new BasicDBObject("indexed",0)).cursor();
        List<UrlData> DataList = new ArrayList<>();
        while (cur.hasNext()) {
            Document doc = cur.next();
            UrlData currentData = new UrlData();
            currentData.URL = (String) doc.get("url");
            currentData.FilePath = (String) doc.get("filepath");
            currentData.popularity = (double) doc.get("popularity");
            DataList.add(currentData);
        }
        return DataList;
    }
    //To mark that we finished indexing
    public void updateIndex(String URL){
        urlsCollection.updateOne(Filters.eq("url", URL), Updates.set("indexed", 1));
    }

    public int getDocumentsSize() {
        return urlsCollection.find(new BasicDBObject("indexed",1)).cursor().available();
    }

}
