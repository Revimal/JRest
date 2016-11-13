package core;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.JSONArray;

/**
 * Created by HyeonHo on 2016-11-12.
 */
public abstract class RestClass {
    private static final Map<Class<? extends RestClass>, RestClass> instances
            = new HashMap<Class<? extends RestClass>, RestClass>();
    public static RestClass getInstance(Class<? extends RestClass> cls) throws Exception {
        synchronized (instances) {
            if (instances.get(cls) == null) {
                instances.put(cls, cls.newInstance());
            }
        }
        return instances.get(cls);
    }

    boolean isRoot = false;
    private Map<String, Class<? extends RestClass>> branch = new HashMap<String, Class<? extends RestClass>>();
    private JSONArray jsonArray = new JSONArray();

    private void responseGet(JSONArray jsonArray, HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(jsonArray.toJSONString().getBytes());
        resStream.close();
    }
    private void responseSuccess(HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(("{\n\t" +
                "\"status\":\"200\",\n\t" +
                "\"message\":\"success\",\n\t"+
                "}").getBytes());
        resStream.close();
    }
    private void responseErr(HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        exchange.sendResponseHeaders(400, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(("{\n\t" +
                "\"status\":\"400\",\n\t" +
                "\"message\":\"Bad Request\",\n\t"+
                "\"info\":REST request fault\"\n\t" +
                "}").getBytes());
        resStream.close();
    }
    private RestClass accessBranch(String branchName) throws Exception{
        if(branch.containsKey(branchName)){
            RestClass cls = RestClass.getInstance(branch.get(branchName));
            return cls;
        }
        else{
            throw new ClassNotFoundException("rest.RestClass_" + branchName);
        }
    }

    private void getProc(StringTokenizer path, HttpExchange exchange) throws Exception{
        if(path.hasMoreElements()){
            String tmpToken = path.nextToken();
            if(branch.containsKey(tmpToken)){
                accessBranch(tmpToken).procRest(path, exchange);
            }
            else{
                if(defaultGetMethod(jsonArray, tmpToken))
                    responseGet(jsonArray, exchange);
                else
                    responseErr(exchange);
            }
        }else{
            if(defaultGetMethod(jsonArray))
                responseGet(jsonArray, exchange);
            else
                responseErr(exchange);
        }
    }
    private void postProc(StringTokenizer path, HttpExchange exchange) throws Exception{
        if(path.hasMoreElements()){
            String tmpToken = path.nextToken();
            if(branch.containsKey(tmpToken)){
                accessBranch(tmpToken).procRest(path, exchange);
            }
            else{
                if(defaultPostMethod(tmpToken))
                    responseSuccess(exchange);
                else
                    responseErr(exchange);
            }
        }else{
            if(defaultPostMethod())
                responseSuccess(exchange);
            else
                responseErr(exchange);
        }
    }
    private void putProc(StringTokenizer path, HttpExchange exchange) throws Exception{
        if(path.hasMoreElements()){
            String tmpToken = path.nextToken();
            if(branch.containsKey(tmpToken)){
                accessBranch(tmpToken).procRest(path, exchange);
            }
            else{
                if(defaultPutMethod(tmpToken))
                    responseSuccess(exchange);
                else
                    responseErr(exchange);
            }
        }else{
            if(defaultPutMethod())
                responseSuccess(exchange);
            else
                responseErr(exchange);
        }
    }
    private void deleteProc(StringTokenizer path, HttpExchange exchange) throws Exception{
        if(path.hasMoreElements()){
            String tmpToken = path.nextToken();
            if(branch.containsKey(tmpToken)){
                accessBranch(tmpToken).procRest(path, exchange);
            }
            else{
                if(defaultDeleteMethod(tmpToken))
                    responseSuccess(exchange);
                else
                    responseErr(exchange);
            }
        }else{
            if(defaultDeleteMethod())
                responseSuccess(exchange);
            else
                responseErr(exchange);
        }
    }

    protected void setRoot() {
        isRoot = true;
    }
    protected void dependOn(String subPath, Class<? extends RestClass> getClass) {
        if(isRoot)
            return;

        branch.put(subPath, getClass);
    }

    public void procRest(StringTokenizer path, HttpExchange exchange) throws Exception {
        switch (exchange.getRequestMethod()) {
            case "GET":
                getProc(path, exchange);
                return;
            case "POST":
                postProc(path, exchange);
                return;
            case "PUT":
                putProc(path, exchange);
                return;
            case "DELETE":
                deleteProc(path, exchange);
                return;
            default:
                throw new RestException("HTTP_METHOD_ERR");
        }
    }

    protected abstract boolean defaultGetMethod(JSONArray jsonArray);
    protected abstract boolean defaultGetMethod(JSONArray jsonArray, String arg);
    protected abstract boolean defaultPostMethod();
    protected abstract boolean defaultPostMethod(String arg);
    protected abstract boolean defaultPutMethod();
    protected abstract boolean defaultPutMethod(String arg);
    protected abstract boolean defaultDeleteMethod();
    protected abstract boolean defaultDeleteMethod(String arg);
}
