package Api;

import Indexer.SearchIndexDBManager;
import Indexer.UrlData;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;
import org.json.*;
import Indexer.QueryProcessor;
import org.jsoup.Jsoup;

import org.jsoup.nodes.Document;


public class SearchPostHandler implements HttpHandler {
    Database DB;
    QueryProcessor QP;
    SearchIndexDBManager SearchIndexDB;
    @Override

    public void handle(HttpExchange he) throws IOException {
        // parse request
        //Map<String, Object> parameters = new HashMap<String, Object>();
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        //parseQuery(query, parameters);
        DB = new Database();
        QP = new QueryProcessor();
        SearchIndexDB = new SearchIndexDBManager();
        // send response
        String response = "";
        JSONArray ja = new JSONArray();
        LinkedHashMap <UrlData, Double> Results = QP.getResults(query);
        for (Map.Entry<UrlData, Double> entry : Results.entrySet())
        {
            JSONObject jo = new JSONObject();
            jo.put("url", entry.getKey().URL);
            File input = new File(entry.getKey().FilePath);
            Document CurrentDoc = Jsoup.parse(input,"UTF-8");
            jo.put("title", CurrentDoc.title());
            jo.put("description", QP.getDescription(CurrentDoc,query));
            ja.put(jo);
        }
        response = ja.toString();
        SearchIndexDB.insertSearchWord(query);
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }


}