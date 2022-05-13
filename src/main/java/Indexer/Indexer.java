package Indexer;
import Crawler.Database;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Indexer {
    Database crawlerDB;

    public static void main(String[] args) throws UnknownHostException {
        Database test = new Database();
        List<String> toBeIndexed = new ArrayList<String>();
        test.getIndexed(toBeIndexed);

    }

}
