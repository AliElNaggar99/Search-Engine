package Crawler;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.UnknownHostException;
import java.util.*;

@SuppressWarnings("ALL")
public class Crawler implements Runnable {

    private static int maxPages = 5000;     //max pages to stop at while crawling
    public static int  noOfCrawlers = 7;
    private Set<String> Visited;                //list of visited urls
    private List<String> queue;                 //list of urls to visit
    Map<String, Vector<String>> forbiddenList;
    Map<String, Vector<String>> allowedList;
    int id;
    Database DB;
    int count = 0;
    Date recrawlTime;       //time to recrawl again
    int turn;
    boolean recrawl;

    public Crawler(Database DB, Set<String> Visited, List<String> queue, Map<String, Vector<String>> forbiddenList, Map<String, Vector<String>> allowedList, int id) {
        this.Visited = Visited;
        this.queue = queue;
        this.forbiddenList = forbiddenList;
        this.allowedList = allowedList;
        this.id = id;
        this.DB = DB;
        recrawlTime = new Date();
        turn = 0;
        recrawl = false;
    }

    public Crawler(Database DB, List<String> queue, int id) {
        this.queue = queue;
        this.id = id;
        this.DB = DB;
    }

    public void recrawl() {
        String currentUrl = null;

        while (true) {
            if (Thread.currentThread().getName().equals("0")) { //if im in the main thread
                queue.clear();
                if (turn == 0) {           //each recrawl one turn on the important fields and one on all of them
                    DB.getVisited(queue);
                    turn = 1;
                } else {
                    DB.getImportant(queue);
                    turn = 0;
                }
                DB.getDate(recrawlTime);
            }
            while ((new Date().after(recrawlTime))) ;
            while (true) {  // recrawl every 4 hours

                synchronized (queue) {
                    if (queue.isEmpty()) {
                        Date time2 = new Date();
                        DB.updateDate(time2);
                        break;
                    }
                }
                currentUrl = null;
                synchronized (queue) {
                    if (!queue.isEmpty()) {
                        currentUrl = queue.remove(0);
                    }
                }
                if (currentUrl != null) {
                    Scraper scraper = new Scraper();
                    scraper.Rescrape(DB, currentUrl); //same as scraping but checks for updates
                }
            }

        }
    }


    public void crawl() {
        String currentUrl = null;

        while (true) {
            synchronized (Visited) {    //synchronize on Visited because of other crawlers on other threads
                if (Visited.size() > maxPages)      //if I visited max number of pages I stop crawling
                    break;
            }
            currentUrl = null;
            synchronized (queue) {      //synchronize on queue because of other crawlers on other threads
                if (!queue.isEmpty()) {
                    synchronized (Visited) {
                        try {
                            String nextUrl = queue.remove(0);   //pop nexturl
                            currentUrl = Visited.contains(nextUrl) ? null : nextUrl;    //if nexturl in visited dont add, otherwise do

                        } catch (Exception ignored) {

                        }
                        if (currentUrl != null) {
                            Visited.add(currentUrl);
                        }
                    }
                }
            }
            if (currentUrl != null) {   //if not visited before, start scraping the url
                Scraper scraper = new Scraper();
                scraper.scrape(DB, currentUrl, forbiddenList, allowedList);
                queue.addAll(scraper.getLinks());   //add to the queue all the hrefs and link found to the queue
            }
        }
        Date time = new Date();
        DB.updateDate(time);
    }

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        long beforeTime = System.currentTimeMillis();
        Set<String> Visited = new HashSet<String>();
        List<String> queue = new LinkedList<String>();
        Map<String, Vector<String>> forbiddenList = new HashMap<String, Vector<String>>();
        Map<String, Vector<String>> allowedList = new HashMap<String, Vector<String>>();
        List<Thread> threadList = new LinkedList<>();
        Thread t1 = null;

        Database DB = new Database();
        DB.getQueue(queue);         //get the queue from the database
        DB.getVisited(Visited);     //get the visited set from the database
        if (Visited.size() == 0 && queue.size() == 0) { //beginning of the run
            readSeedList(queue);    //read the initial seed list and fill in the queue
        }
        for (int i = 0; i < noOfCrawlers; i++) {    //each crawler with a thread to crawl in
            t1 = new Thread(new Crawler(DB, Visited, queue, forbiddenList, allowedList, i));
            threadList.add(t1);
            String name = Integer.toString(i);
            t1.setName(name);
            t1.start();         //start thread
        }
        for (int i = 0; i < noOfCrawlers; i++) {
            threadList.get(i).join();           //join all threads
        }
    }


    public static void readSeedList(List<String> queue) {
        try {
            File myObj = new File("seed_list.txt");
            Scanner myReader = new Scanner(myObj);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                queue.add(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred while reading seed list.");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        crawl();
        if (recrawl) {
            recrawl();
        }
    }
}