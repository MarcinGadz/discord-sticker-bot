package com.zzpj.dc.app.exceptions;

public class WrongFileTypeException extends RuntimeException {
    public WrongFileTypeException() {
    }

    public WrongFileTypeException(String message) {
        super(message);
    }
}
