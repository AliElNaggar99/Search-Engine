package Indexer;

import java.util.HashMap;
import java.util.Map;

public class WordData {
    public int count = 0;
    public String url;
    public int lengthOfDoc;
    public Map<String, Integer> position= new HashMap<String, Integer>();

    public WordData(){
        position.put("title" , 0);
        position.put("h1" , 0);
        position.put("h2" , 0);
        position.put("h3" , 0);
        position.put("h4" , 0);
        position.put("h5" , 0);
        position.put("h6" , 0);
        position.put("body" , 0);
    }

}
