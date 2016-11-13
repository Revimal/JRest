package core;
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

    boolean isRoot = true;
    private Set<String> branch = new HashSet<String>();
//    private Map<String, Method> getMethod = new HashMap<String, Method>();
//    private Map<String, Method> postMethod = new HashMap<String, Method>();
//    private Map<String, Method> putMethod = new HashMap<String, Method>();
//    private Map<String, Method> deleteMethod = new HashMap<String, Method>();

    protected JSONArray jsonArray = new JSONArray();

    private void getProc(StringTokenizer path, HttpExchange exchange) throws Exception{

    }
    private void postProc(StringTokenizer path, HttpExchange exchange) throws Exception{
        if(path.hasMoreElements()){
            String tmpToken = path.nextToken();
            if(branch.contains(tmpToken)){
                accessBranch(tmpToken).procRest(path, exchange);
            }
            else(){
                defaultGetMethod();
            }
        }else{
            return defaultPostMethod();
        }
    }
    private void putProc(StringTokenizer path, HttpExchange exchange) throws Exception{

    }
    private void deleteProc(StringTokenizer path, HttpExchange exchange) throws Exception{

    }
//    public void setBranch(String expr){
//        if(getMethod.containsKey(expr) ||
//                postMethod.containsKey(expr) ||
//                putMethod.containsKey(expr) ||
//                deleteMethod.containsKey(expr))
//            return;
//
//        branch.add(expr);
//    }
//    public void setGetMethod(String expr, String method, Class<? extends RestClass> target) throws Exception{
//        if(branch.contains(expr))
//            return;
//
//        Method tmpMethod = target.getMethod(method);
//        if(tmpMethod.getReturnType().getSimpleName().equals("String"))
//            getMethod.put(expr, tmpMethod);
//    }
//    private String callGetMethod(String expr, Object[] args) throws Exception{
//        if(getMethod.containsKey(expr)) {
//            return (String)getMethod.get(expr).invoke(args);
//        }
//        else
//            throw new NoSuchMethodError("UnknownMethod");
//    }
//    public void setpostMethod(String expr, String method, Class<? extends RestClass> target) throws Exception{
//        if(branch.contains(expr))
//            return;
//
//        Method tmpMethod = target.getMethod(method);
//        if(tmpMethod.getReturnType().getSimpleName().equals("String"))
//            postMethod.put(expr, tmpMethod);
//    }
//    private String callpostMethod(String expr, Object[] args) throws Exception{
//        if(postMethod.containsKey(expr)) {
//            return (String)postMethod.get(expr).invoke(args);
//        }
//        else
//            throw new NoSuchMethodError("UnknownMethod");
//    }
//    public void setputMethod(String expr, String method, Class<? extends RestClass> target) throws Exception{
//        if(branch.contains(expr))
//            return;
//
//        Method tmpMethod = target.getMethod(method);
//        if(tmpMethod.getReturnType().getSimpleName().equals("String"))
//            putMethod.put(expr, tmpMethod);
//    }
//    private String callputMethod(String expr, Object[] args) throws Exception{
//        if(putMethod.containsKey(expr)) {
//            return (String)putMethod.get(expr).invoke(args);
//        }
//        else
//            throw new NoSuchMethodError("UnknownMethod");
//    }
//    public void setdeleteMethod(String expr, String method, Class<? extends RestClass> target) throws Exception{
//        if(branch.contains(expr))
//            return;
//
//        Method tmpMethod = target.getMethod(method);
//        if(tmpMethod.getReturnType().getSimpleName().equals("String"))
//            deleteMethod.put(expr, tmpMethod);
//    }
//    private String calldeleteMethod(String expr, Object[] args) throws Exception{
//        if(deleteMethod.containsKey(expr)) {
//            return (String)deleteMethod.get(expr).invoke(args);
//        }
//        else
//            throw new NoSuchMethodError("UnknownMethod");
//    }

    protected void setRoot() {
        isRoot = false;
    }
    protected RestClass accessBranch(String branchName) throws Exception{
        if(branch.contains(branchName)){
            RestClass cls = RestClass.getInstance(Class.forName("rest.RestClass_"+branchName).asSubclass(RestClass.class));
            return cls;
        }
        else{
            throw new ClassNotFoundException("rest.RestClass_" + branchName);
        }
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
                rputProc(path, exchange);
                return;
            case "DELETE":
                deleteProc(path, exchange);
                return;
            default:
                throw new RestException("HTTP_REQ_ERR");
        }
    }

    protected abstract boolean defaultGetMethod(HttpExchange);
    protected abstract boolean defaultGetMethod(String arg);
    protected abstract boolean defaultPostMethod();
    protected abstract boolean defaultPostMethod(String arg);
    protected abstract boolean defaultPutMethod();
    protected abstract boolean defaultPutMethod(String arg);
    protected abstract boolean defaultDeleteMethod();
    protected abstract boolean defaultDeleteMethod(String arg);
}
