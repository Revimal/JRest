package core;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import org.json.simple.JSONObject;

/**
 * Created by HyeonHo on 2016-11-12.
 */
public abstract class RestClass {
    private static final Map<Class<? extends RestClass>, RestClass> instances
            = new HashMap<Class<? extends RestClass>, RestClass>();
    public final static RestClass getInstance(Class<? extends RestClass> cls) throws Exception {
        synchronized (instances) {
            if (instances.get(cls) == null) {
                instances.put(cls, cls.newInstance());
            }
        }
        return instances.get(cls);
    }

    private Map<String, Class<? extends RestClass>> branch = new HashMap<String, Class<? extends RestClass>>();
    private JSONObject jsonObject = new JSONObject();

    private void responseGet(JSONObject jsonObject, HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        resHeader.set("charset", "UTF-8");
        exchange.sendResponseHeaders(200, 0);
        OutputStream resStream = exchange.getResponseBody();
        resStream.write(jsonObject.toJSONString().getBytes());
        resStream.close();
    }
    private void responseSuccess(HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        resHeader.set("charset", "UTF-8");
        exchange.sendResponseHeaders(200, 0);
        OutputStream resStream = exchange.getResponseBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "200");
        jsonObject.put("message", "Success");
        resStream.write(jsonObject.toJSONString().getBytes());
        resStream.close();
    }
    void responseErr(HttpExchange exchange) throws IOException{
        Headers resHeader = exchange.getResponseHeaders();
        resHeader.set("Content-Type", "application/json");
        resHeader.set("charset", "UTF-8");
        exchange.sendResponseHeaders(400, 0);
        OutputStream resStream = exchange.getResponseBody();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("status", "400");
        jsonObject.put("message", "Bad Request");
        jsonObject.put("info", "REST request fault");
        resStream.write(jsonObject.toJSONString().getBytes());
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
                if(defaultGetMethod(jsonObject, tmpToken)){
                    if(path.hasMoreElements()){
                        tmpToken = path.nextToken();
                        if(branch.containsKey(tmpToken)){
                            accessBranch(tmpToken).procRest(path, exchange);
                        }
                    }
                    else{
                        responseGet(jsonObject, exchange);
                    }
                }
                else
                    responseErr(exchange);
            }
        }else{
            if(defaultGetMethod(jsonObject))
                responseGet(jsonObject, exchange);
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
                if(defaultPostMethod(tmpToken)) {
                    if(path.hasMoreElements()){
                        tmpToken = path.nextToken();
                        if(branch.containsKey(tmpToken)){
                            accessBranch(tmpToken).procRest(path, exchange);
                        }
                    }
                    else
                        responseSuccess(exchange);
                }
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
                if(defaultPutMethod(tmpToken)){
                    if(path.hasMoreElements()){
                        tmpToken = path.nextToken();
                        if(branch.containsKey(tmpToken)){
                            accessBranch(tmpToken).procRest(path, exchange);
                        }
                    }
                    else
                        responseSuccess(exchange);
                }
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
                if(defaultDeleteMethod(tmpToken)){
                    if(path.hasMoreElements()){
                        tmpToken = path.nextToken();
                        if(branch.containsKey(tmpToken)){
                            accessBranch(tmpToken).procRest(path, exchange);
                        }
                    }
                    else
                        responseSuccess(exchange);
                }
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

    public final void dependOn(String subPath, Class<? extends RestClass> getClass) {
        branch.put(subPath, getClass);
    }
    public final void procRest(StringTokenizer path, HttpExchange exchange) throws Exception {
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

    protected abstract boolean defaultGetMethod(JSONObject jsonObject);
    protected abstract boolean defaultGetMethod(JSONObject jsonObject, String arg);
    protected abstract boolean defaultPostMethod();
    protected abstract boolean defaultPostMethod(String arg);
    protected abstract boolean defaultPutMethod();
    protected abstract boolean defaultPutMethod(String arg);
    protected abstract boolean defaultDeleteMethod();
    protected abstract boolean defaultDeleteMethod(String arg);
}
