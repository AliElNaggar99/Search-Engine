package Api;

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


public class EchoPostHandler implements HttpHandler {
    Database DB;
    QueryProcessor QP;
    @Override

    public void handle(HttpExchange he) throws IOException {
        // parse request
        Map<String, Object> parameters = new HashMap<String, Object>();
        InputStreamReader isr = new InputStreamReader(he.getRequestBody(), "utf-8");
        BufferedReader br = new BufferedReader(isr);
        String query = br.readLine();
        parseQuery(query, parameters);
        DB = new Database();
        QP = new QueryProcessor();
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
        System.out.println(ja);
        response = ja.toString();
        he.sendResponseHeaders(200, response.length());
        OutputStream os = he.getResponseBody();
        os.write(response.toString().getBytes());
        os.close();
    }

    public static void parseQuery(String query, Map<String, Object> parameters) throws UnsupportedEncodingException {

        if (query != null) {
            String pairs[] = query.split("[&]");
            for (String pair : pairs) {
                String param[] = pair.split("[=]");
                String key = null;
                String value = null;
                if (param.length > 0) {
                    key = URLDecoder.decode(param[0],
                            System.getProperty("file.encoding"));
                }

                if (param.length > 1) {
                    value = URLDecoder.decode(param[1],
                            System.getProperty("file.encoding"));
                }

                if (parameters.containsKey(key)) {
                    Object obj = parameters.get(key);
                    if (obj instanceof List<?>) {
                        List<String> values = (List<String>) obj;
                        values.add(value);

                    } else if (obj instanceof String) {
                        List<String> values = new ArrayList<String>();
                        values.add((String) obj);
                        values.add(value);
                        parameters.put(key, values);
                    }
                } else {
                    parameters.put(key, value);
                }
            }
        }
    }
}