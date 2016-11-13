package core;

/**
 * Created by revdev on 16. 11. 13.
 */

public class RestException extends Exception {
    public RestException(){}
    public RestException(String message){
        super(message);
    }
}
