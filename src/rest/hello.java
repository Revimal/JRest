package rest;

import core.RestClass;
import core.RestRoot;
import org.json.simple.JSONObject;

/**
 * Created by revdev on 16. 11. 13.
 */
public class hello extends RestClass{
    public static void init() throws Exception{
        RestClass.getInstance(RestRoot.class).dependOn("hello", hello.class);
    }

    @Override
    protected boolean defaultGetMethod(JSONObject jsonObject) {
        jsonObject.put("status", "200");
        jsonObject.put("message", "Success");
        return true;
    }
    @Override
    protected boolean defaultGetMethod(JSONObject jsonObject, String arg) {
        jsonObject.put("status", "200");
        jsonObject.put("message", "Success");
        return true;
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
