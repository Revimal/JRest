package core;

public class RestException extends Exception {
    public RestException(){}
    public RestException(String message){
        super(message);
    }
}
