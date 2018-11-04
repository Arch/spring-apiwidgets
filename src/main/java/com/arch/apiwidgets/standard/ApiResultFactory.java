/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.standard;

import com.arch.apiwidgets.exception.StatusCodeException;

/**
 * Represents a factory to produce {@link ApiResult ApiResult}
 */
public class ApiResultFactory {
    public static <T> ApiResult succeed(T result) {
        return new ApiResult<>(20000, "OK", result);
    }

    public static ApiResult reentry(String message) {
        return new ApiResult(20500, message);
    }

    public static ApiResult notContent() {
        return new ApiResult(20400, "not content");
    }

    public static ApiResult redirect(String location) {
        return new ApiResult<>(30200, "redirect", location);
    }

    public static ApiResult notFound() {
        return new ApiResult(40400, "not found");
    }

    public static ApiResult systemError(String message) {
        if (message == null || message.isEmpty()) {
            message = "system internal exception";
        }
        return new ApiResult(50000, message);
    }

    public static ApiResult unauthorized(String message) {
        return new ApiResult(40100, message);
    }

    public static ApiResult tokenError(String message) {
        return new ApiResult(50101, message);
    }

    public static ApiResult appKeyError(String message) {
        return new ApiResult(50102, message);
    }

    public static ApiResult timeFormatError() {
        return new ApiResult(50103, "time format error");
    }

    public static ApiResult badRequest(String message) {
        return new ApiResult(40000, message);
    }

    public static ApiResult failure(String message) {
        return new ApiResult(50100, message);
    }

    public static ApiResult failure(StatusCodeException exception) {
        return new ApiResult(exception.getStatusCode(), exception.getMessage());
    }
}
