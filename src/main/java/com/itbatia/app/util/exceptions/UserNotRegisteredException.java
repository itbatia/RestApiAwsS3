package com.itbatia.app.util.exceptions;

public class UserNotRegisteredException extends RuntimeException {
    public UserNotRegisteredException(String msg) {
        super(msg);
    }
}
