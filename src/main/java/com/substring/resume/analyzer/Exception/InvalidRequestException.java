package com.substring.resume.analyzer.Exception;

public class InvalidRequestException extends RuntimeException{
    public InvalidRequestException(String message){
        super(message);
    }
    public InvalidRequestException(){
        super("Your request is invalid");
    }
}
