package Api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
    public static void main(String[] args) {
        int port = 9000;
        HttpServer server = null;
        try {
            server = HttpServer.create(new InetSocketAddress(port), 0);
            System.out.println("server started at " + port);
            server.createContext("/", new RootHandler());
            server.createContext("/echoHeader", new EchoHeaderHandler());
            server.createContext("/search", new SearchPostHandler());
            server.createContext("/suggest", new SuggestionPostHandler());
            server.setExecutor(null);
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
