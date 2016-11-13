package core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;


/**
 * Created by HyeonHo on 2016-09-03.
 */
public class RestHandler implements HttpHandler{
    private void responseErr(HttpExchange exchange, int errCode, String title, String target, String info) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(errCode, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(("{\n\t" +
                "\"status\":\"" + String.valueOf(errCode) + "\",\n\t" +
                "\"message\":\"" + target + "\",\n\t"+
                "\"info\":" + info +"\"\n\t" +
                "}").getBytes());
        resStream.close();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException{
        StringTokenizer tokenizerPath = new StringTokenizer(exchange.getRequestURI().getPath(), "/");

        if(tokenizerPath.hasMoreElements()) {
            try {
                RestClass restClass =
                        RestClass.getInstance(Class.forName("rest.RestClass_" + tokenizerPath.nextToken()).asSubclass(RestClass.class));
                if(restClass.isRoot)
                        throw new ClassNotFoundException(restClass.getClass().getSimpleName());

                restClass.procRest(tokenizerPath, exchange);

            } catch (ClassNotFoundException e) {
                responseErr(exchange, 404, "Not Found", e.getMessage(), "REST handler not found.");
            } catch (InstantiationException e) {
                responseErr(exchange, 404, "Not Found", e.getMessage(), "REST handler instantiation fault.");
            } catch (IllegalAccessException e) {
                responseErr(exchange, 403, "Forbidden", e.getMessage(), "REST handler forbidden.");
            } catch (NoSuchMethodException e) {
                responseErr(exchange, 404, "Not Found", e.getMessage(), "REST handler not found.");
            } catch (InvocationTargetException e){
                responseErr(exchange, 500, "Internal Server Error", e.getMessage(), "REST handler invocation fault.");
            } catch(RestException e){
                responseErr(exchange, 400, "Bad Request", e.getMessage(), "REST request fault.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            Headers resHeader = exchange.getResponseHeaders();
            resHeader.set("Content-Type", "text/html");
            exchange.sendResponseHeaders(200, 0);
            OutputStream resStream = exchange.getResponseBody();
            resStream.write("<h1>REST Service Running...</h1>".getBytes());
            resStream.close();
        }
    }
}
