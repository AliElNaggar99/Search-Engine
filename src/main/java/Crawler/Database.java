package Crawler;

import com.mongodb.*;

import com.mongodb.client.*;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Updates;
import org.bson.Document;

import javax.print.Doc;
import java.net.UnknownHostException;
import java.util.*;

import static com.mongodb.client.model.Filters.eq;

@SuppressWarnings("ALL")
public class Database {
    MongoClient mongoClient;
    MongoDatabase crawlerDB;
    MongoCollection<Document> crawlerCollection ;
    MongoCollection<Document> hrefCollection;
    MongoCollection<Document> DateCollection;

    public Database() throws UnknownHostException {
        System.setProperty("jdk.tls.trustNameService", "true");
//        ConnectionString connectionString = new ConnectionString("mongodb+srv://sherno:asd123@cluster0.u308m.mongodb.net/?retryWrites=true&w=majority");
//        MongoClientSettings settings = MongoClientSettings.builder()
//                .applyConnectionString(connectionString)
//                .serverApi(ServerApi.builder()
//                        .version(ServerApiVersion.V1)
//                        .build())
//                .build();
//        String uri = "mongodb://localhost:27017";
//        MongoClient mongoClient = MongoClients.create(uri);
        //For Local connecting to the database
        ConnectionString connectionString = new ConnectionString("mongodb://localhost:27017");
        //ConnectionString connectionString = new ConnectionString("mongodb+srv://sherno:asd123@cluster0.u308m.mongodb.net/?retryWrites=true&w=majority");
        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(connectionString)
                .serverApi(ServerApi.builder()
                        .version(ServerApiVersion.V1)
                        .build())
                .build();
        MongoClient mongoClient = MongoClients.create(settings);
        MongoDatabase database = mongoClient.getDatabase("CrawlerDB");
        this.crawlerDB = database;
        this.mongoClient  = mongoClient;
        crawlerCollection = crawlerDB.getCollection("Links");
        hrefCollection = crawlerDB.getCollection("hrefs");
    }
    void updateHref(List<String> Link,String baseURL){

        Object objID = crawlerCollection.find(eq("URL", baseURL)).first().get("_id");
        String ID = objID.toString();
        hrefCollection.deleteOne(Filters.eq("refTo",ID));

        for(int i = 0 ;i<Link.size(); i++) {
            Document crawlerEntry = new Document("URL", Link.get(i))
                    .append("refTo", ID);
            hrefCollection.insertOne(crawlerEntry);
        }

    }

    public Set<String> getHref(String baseURL){
        Object objID = crawlerCollection.find(eq("URL", baseURL)).first().get("_id");
        String ID = objID.toString();

        MongoCursor<Document> cur2 = hrefCollection.find(eq("refTo", ID)).cursor();
        Set<String> linkedHashSet = new LinkedHashSet<>();
        while(cur2.hasNext())
        {
            linkedHashSet.add(cur2.next().get("URL").toString());
        }
        return linkedHashSet;
    }

    public void insertHref(List<String> Link,String baseURL,long crc,String filepath) {
        Document doc = crawlerCollection.find(eq("URL", baseURL)).first();
        crawlerCollection.updateOne(doc,
                Updates.combine(Updates.set("crc", crc), Updates.set("filepath", filepath)));
        Object objID = doc.get("_id");
        String ID = objID.toString();
        List<Document> crawlerEntry = new ArrayList<>();
        for(int i = 0 ;i<Link.size(); i++) {
            crawlerEntry.add(new Document("URL", Link.get(i))
                    .append("refTo", ID));

        }
        hrefCollection.insertMany(crawlerEntry);

    }
    public void insertLink(List<String> Link){
        List<Document> crawlerEntry = new ArrayList<>();

        for(int i = 0 ;i<Link.size(); i++) {
            crawlerEntry.add(new Document("URL", Link.get(i))
                    .append("Visited", 0)
                    .append("indexed", 0)
                    .append("importance",0)
                    .append("PageRank",(double)0.0)
                    .append("crc",(long)0)
                    .append("filepath",""));

        }
        crawlerCollection.insertMany(crawlerEntry);
    }
    public void insertDate(Date time){
        Document crawlerEntry = new Document("Date", time)
                .append("type","date");
        crawlerCollection.insertOne(crawlerEntry);
    }
    public void getDate(Date time) {
        Date T =(Date) crawlerCollection.find(eq("type", "date")).first().get("Date");
        time.setTime(T.getTime());
        time.setHours(time.getHours()+1); //set recrawl time to each 4 hour
    }
    public void updateDate(Date time){
        Object obj = crawlerCollection.find(eq("type", "date")).first().get("Date");
        if (obj == null) {
            insertDate(time);
        } else {
            crawlerCollection.updateOne(
                    Filters.eq("type", "date"),
                    Updates.set("Date", time));
        }
    }
    public void visitLink(String Link,int importance){
        Object findQuery = crawlerCollection.find(eq("URL", Link)).first();
        if (findQuery != null) {
            crawlerCollection.updateOne(Filters.eq("URL", Link),
                    Updates.combine(Updates.set("Visited", 1), Updates.set("importance", importance)));
        }
        else{
            Document crawlerEntry = new Document("URL", Link)
                    .append("Visited", 1)
                    .append("indexed", 0)
                    .append("importance",importance)
                    .append("PageRank",(double) 0.0);


            crawlerCollection.insertOne(crawlerEntry);
        }
    }
    void getQueue(List<String> queue){

        MongoCursor<Document> cur =  crawlerCollection.find(new BasicDBObject("Visited", 0)).cursor();
        while(cur.hasNext()){
            Document doc = cur.next();
            String URL = (String) doc.get("URL");
            queue.add(URL);
        }
    }
    void getVisited(Set<String> Visited){
        MongoCursor<Document> cur =  crawlerCollection.find(new BasicDBObject("Visited", 1)).cursor();
        while(cur.hasNext()){
            Document doc = cur.next();
            String URL = (String) doc.get("URL");
            Visited.add(URL);
        }
    }
    public void getVisited(List<String> queue){
        MongoCursor<Document> cur =  crawlerCollection.find(new BasicDBObject("Visited", 1)).cursor();
        while(cur.hasNext()){
            Document doc = cur.next();
            String URL = (String) doc.get("URL");
            queue.add(URL);
        }
    }
    void getImportant(List<String> queue){
        MongoCursor<Document> cur =  crawlerCollection.find(new BasicDBObject("Visited", 1)
                .append("importance",1)).cursor();
        while(cur.hasNext()){
            Document doc = cur.next();
            String URL = (String) doc.get("URL");
            queue.add(URL);
        }
    }
    void updateLink(String URL){
        crawlerCollection.updateOne(Filters.eq("URL", URL),
                Updates.combine(Updates.set("indexed", 0), Updates.set("PageRank",0.0)));

    }

    public void getIndexed(List<String> queue){
        MongoCursor<Document> cur =  crawlerCollection.find(new BasicDBObject("indexed", 0)).cursor();
        while (cur.hasNext()){
            Document doc = cur.next();
            String URL = (String) doc.get("URL");
            queue.add(URL);
        }
    }

    public boolean crcExists(long crc){
        Object objID = crawlerCollection.find(eq("crc", crc)).first();
        if (objID==null)
            return false;
        return true;
    }

}




