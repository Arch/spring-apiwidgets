package com.arch.apiwidgets.exception;

public class ReentryException extends RuntimeException {
    public ReentryException(String message) {
        super(message);
    }
}
