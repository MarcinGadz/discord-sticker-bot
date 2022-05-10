package com.zzpj.dc.app.exceptions;

public class ImageDoesntExistException extends Exception {
    public ImageDoesntExistException() {
    }

    public ImageDoesntExistException(String message) {
        super(message);
    }
}
