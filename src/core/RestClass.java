package core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public abstract void hello();
}
