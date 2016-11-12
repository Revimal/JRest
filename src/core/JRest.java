package core;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

/**
 * Created by HyeonHo on 2016-09-03.
 */

public class JRest{
    private static InetSocketAddress addr;
    private static HttpServer server;

    private JRest(){}

    private static class Singleton{
        private static final JRest instance = new JRest();
    }
    public static JRest getInstance(){
        return Singleton.instance;
    }

    public static void init(String root, int port) throws IOException{
        addr = new InetSocketAddress(port);
        server = HttpServer.create(addr, 0);
        server.createContext(root, new RestHandler());
        server.setExecutor(Executors.newCachedThreadPool());
    }

    public static void run(){
        server.start();
    }

    public static void stop(){
        server.stop(0);
    }
}