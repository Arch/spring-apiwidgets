/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.standard;

/**
 * Represents a standard response protocol
 * @param <T> The type of the raw result
 */
public class ApiResult<T> {
    private int statusCode;
    private String message;
    private T result;

    public ApiResult() { }

    public ApiResult(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
        this.result = null;
    }

    public ApiResult(int statusCode, String message, T result) {
        this.statusCode = statusCode;
        this.message = message;
        this.result = result;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getResult() {
        return result;
    }

    public void setResult(T result) {
        this.result = result;
    }
}
