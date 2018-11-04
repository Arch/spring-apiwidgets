/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.exception;

/**
 * This exception class will be used in those scenarios when an exception should be thrown but provide
 * a custom status code.
 */
public class StatusCodeException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230194L;

    private int statusCode;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public StatusCodeException(int statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public StatusCodeException(int statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }
}
