/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.exception;

import com.arch.apiwidgets.standard.ApiResult;

/**
 * This exception class will be used in those scenarios when an exception should be thrown but provide
 * a custom {@link ApiResult}, such as customizing the status code and more meaningful message.
 */
public class ApiResultException extends RuntimeException {
    private static final long serialVersionUID = -8460356990632230195L;
    private ApiResult apiResult;

    public ApiResultException(ApiResult apiResult) {
        super(apiResult.getMessage());
        this.apiResult = apiResult;
    }

    public ApiResult getApiResult() {
        return apiResult;
    }

    public void setApiResult(ApiResult apiResult) {
        this.apiResult = apiResult;
    }
}
