/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.core;

import com.arch.apiwidgets.standard.ApiResult;
import com.arch.apiwidgets.standard.ApiResultFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * This class will intercept all method's return value and convert that value to {@link ApiResult ApiResult}
 */
public class ApiResultProcessor implements HandlerMethodReturnValueHandler {
    private final HandlerMethodReturnValueHandler delegate;

    public ApiResultProcessor(HandlerMethodReturnValueHandler delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean supportsReturnType(MethodParameter returnType) {
        return delegate.supportsReturnType(returnType);
    }

    @Override
    public void handleReturnValue(Object returnValue, MethodParameter returnType, ModelAndViewContainer mavContainer, NativeWebRequest webRequest) throws Exception {
        if(ApiWidgetsUtils.apiResultProcessingDisabled(webRequest)) {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
            return;
        }

        if(returnValue == null) {
            if (returnType.getParameterType().equals(Void.TYPE)) {
                delegate.handleReturnValue(ApiResultFactory.succeed(null), returnType, mavContainer, webRequest);
            }
            else {
                delegate.handleReturnValue(ApiResultFactory.notContent(), returnType, mavContainer, webRequest);
            }
        }
        else if(returnValue instanceof ApiResult) {
            delegate.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
        } else {
            delegate.handleReturnValue(ApiResultFactory.succeed(returnValue), returnType, mavContainer, webRequest);
        }
    }
}