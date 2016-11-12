package core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;


/**
 * Created by HyeonHo on 2016-09-03.
 */
public class RestHandler implements HttpHandler{
    private void responseErr(HttpExchange exchange, int errCode, String title, String target, String info) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "text/html");
        exchange.sendResponseHeaders(404, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(("<h1>" + String.valueOf(errCode) + " - " + title + "</h1>" + "#Err : [" + target + "]_" + info).getBytes());
        resStream.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        StringTokenizer tokenizerPath = new StringTokenizer(exchange.getRequestURI().getPath(), "/");

        try {
            RestClass restClass =
                RestClass.getInstance(Class.forName("rest.RestClass_"+ tokenizerPath.nextToken()).asSubclass(RestClass.class));

            restClass.hello();

            Headers resHeader = exchange.getResponseHeaders();
            resHeader.set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);
            OutputStream resStream = exchange.getResponseBody();
            resStream.write("<h1>Success!</h1>".getBytes());
            resStream.close();

        }catch(ClassNotFoundException e){
            responseErr(exchange, 404, "Not Found", e.getMessage(), "REST handler class not found.");
        }catch(InstantiationException e){
            responseErr(exchange, 404, "Not Found", e.getMessage(), "REST handler instantiation fault.");
        }catch(IllegalAccessException e){
            responseErr(exchange, 403, "Forbidden", e.getMessage(), "REST handler forbidden.");
        }
    }
}
