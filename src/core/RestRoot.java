package core;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by revdev on 16. 11. 13.
 */
public class RestRoot extends RestClass{
    @Override
    void responseErr(HttpExchange exchange) throws IOException {
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        resHeader.set("charset", "UTF-8");
        exchange.sendResponseHeaders(404, 0);
        OutputStream resStream = exchange.getResponseBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "404");
        jsonObject.put("message", "Not Found");
        jsonObject.put("info", "REST handler not found");
        resStream.write(jsonObject.toJSONString().getBytes());
        resStream.close();
    }
    @Override
    protected boolean defaultGetMethod(JSONObject jsonObject) {
        return false;
    }
    @Override
    protected boolean defaultGetMethod(JSONObject jsonObject, String arg) {
        return false;
    }
    @Override
    protected boolean defaultPostMethod() {
        return false;
    }
    @Override
    protected boolean defaultPostMethod(String arg) {
        return false;
    }
    @Override
    protected boolean defaultPutMethod() {
        return false;
    }
    @Override
    protected boolean defaultPutMethod(String arg) {
        return false;
    }
    @Override
    protected boolean defaultDeleteMethod() {
        return false;
    }
    @Override
    protected boolean defaultDeleteMethod(String arg) {
        return false;
    }
}
