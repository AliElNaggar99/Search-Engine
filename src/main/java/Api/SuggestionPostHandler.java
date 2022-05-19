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


public class SuggestionPostHandler implements HttpHandler {
    Database DB;
    QueryProcessor QP;
    SearchIndexDBManager SearchIndexDB;
    @Override

    public void handle(HttpExchange he) throws IOException {
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        DB = new Database();
        QP = new QueryProcessor();
        SearchIndexDBManager SIDB = new SearchIndexDBManager();
        SearchIndexDB = new SearchIndexDBManager();
        // send response
        List<String> Results= new ArrayList<String>();
        Results = SIDB.getHistoryWords(query);

        JSONObject jo = new JSONObject();
        jo.put("suggestions", Results);
        String response = jo.toString();
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }
}