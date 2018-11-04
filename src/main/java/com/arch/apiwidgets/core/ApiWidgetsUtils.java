/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.core;

import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.http.HttpServletRequest;

public class ApiWidgetsUtils {
    private static final String SKIP_API_RESULT_PROCESSING = "SKIP.APIRESULT.PROCESSING";

    public static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static boolean isAjaxRequest(NativeWebRequest request) {
        return "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
    }

    public static void disableApiResultProcessing(HttpServletRequest request) {
        request.setAttribute(SKIP_API_RESULT_PROCESSING, Boolean.TRUE);
    }

    public static boolean apiResultProcessingDisabled(NativeWebRequest request) {
        Object flag = request.getAttribute(SKIP_API_RESULT_PROCESSING, RequestAttributes.SCOPE_REQUEST);
        if (flag instanceof Boolean) {
            return (boolean)flag;
        }

        return false;
    }
}