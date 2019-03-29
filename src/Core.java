package jrest;

import core.JRest;
import rest.hello;

public class Core {
    public static void main(String args[]) throws Exception{
        hello.init();
        JRest.getInstance().init("/", 8080);
        JRest.getInstance().run();
    }
}
