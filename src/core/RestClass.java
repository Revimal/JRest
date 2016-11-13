package core;

import org.json.simple.JSONObject;

import java.util.*;

/**
 * Created by HyeonHo on 2016-11-12.
 */
public abstract class RestClass {
    private static final Map<Class<? extends RestClass>, RestClass> instances
            = new HashMap<Class<? extends RestClass>, RestClass>();

    public static RestClass getInstance(Class<? extends RestClass> cls) throws InstantiationException, IllegalAccessException {
        synchronized (instances) {
            if (instances.get(cls) == null) {
                instances.put(cls, cls.newInstance());
            }
        }
        return instances.get(cls);
    }

    protected JSONArray jsonArray = new JSONObject();
    protected Set<String> branches = new HashSet<String>();

    protected RestClass accessBranch(String branchName) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
        if(branches.contains(branchName)){
            return RestClass.getInstance(Class.forName("rest.RestClass_"+branchName).asSubclass(RestClass.class));
        }
        else{
            return null;
        }
    }

    public abstract void hello();
}
