/**
 * Created by HyeonHo on 2016-09-03.
 */

import core.JRest;
import rest.hello;

import java.io.IOException;

public class Core {
    public static void main(String args[]) throws Exception{
        hello.init();
        JRest.getInstance().init("/", 9000);
        JRest.getInstance().run();
    }
}
