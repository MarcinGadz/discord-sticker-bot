package com.zzpj.dc.app.exceptions;

public class UserLimitExceededException extends RuntimeException {
    public UserLimitExceededException() {
    }

    public UserLimitExceededException(String message) {
        super(message);
    }
}
