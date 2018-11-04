/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.core;

import com.arch.apiwidgets.exception.ApiResultException;
import com.arch.apiwidgets.exception.ReentryException;
import com.arch.apiwidgets.exception.StatusCodeException;
import com.arch.apiwidgets.standard.ApiResult;
import com.arch.apiwidgets.standard.ApiResultFactory;
import com.fasterxml.jackson.core.JsonParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.web.servlet.error.AbstractErrorController;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Map;

/**
 * This exceptions handler will only convert all exceptions that thrown by API to {@link ApiResult ApiResult}.
 * The exceptions logging or notifying to system maintain developers is delegating to {@link ExceptionNotifier ExceptionNotifier}.
 */
@RestControllerAdvice
public class ApiExceptionHandler extends AbstractErrorController {
    private final static String ErrorPath = "/error";
    private final ExceptionNotifier exceptionNotifier;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ApiExceptionHandler(ExceptionNotifier exceptionNotifier) {
        super(new DefaultErrorAttributes());

        this.exceptionNotifier = exceptionNotifier;
    }

    @Override
    public String getErrorPath() {
        return ErrorPath;
    }

    @GetMapping(value = ErrorPath)
    public ApiResult handleError(HttpServletRequest request) {
        Map<String, Object> attributes = getErrorAttributes(request, true);

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Object> attribute : attributes.entrySet()) {
            sb.append(attribute.getKey()).append(": ").append(attribute.getValue());
            sb.append(";");
        }
        if (logger.isDebugEnabled()) {
            logger.debug(sb.toString());
        }

        HttpStatus httpStatus = getStatus(request);

        // using exception from attributes
        Object exception = attributes.get("exception");
        if (exception instanceof RuntimeException) {
            RuntimeException runtimeException = (RuntimeException) exception;
            return ApiResultFactory.systemError(String.format("%s, exception: %s", httpStatus, runtimeException.getMessage()));
        }

        // using message from attributes
        Object msg = attributes.get("message");
        if (msg != null) {
            return ApiResultFactory.systemError(String.format("%s, %s", httpStatus, msg));
        }

        // using error from attributes
        Object error = attributes.get("error");
        if (error != null) {
            return ApiResultFactory.systemError(String.format("%s, error: %s", httpStatus, error));
        }

        return ApiResultFactory.systemError(sb.toString());
    }

    @ExceptionHandler(StatusCodeException.class)
    public ApiResult handleApiWidgetsException(HttpServletRequest request, StatusCodeException ex) {
        exceptionNotifier.notify(request, ex);

        return ApiResultFactory.failure(ex);
    }

    @ExceptionHandler(ApiResultException.class)
    public ApiResult handleApiWidgetsApiResultException(ApiResultException ex) {
        return ex.getApiResult();
    }

    @ExceptionHandler(RestClientException.class)
    public ApiResult handleRestClientException(HttpServletRequest request, RestClientException ex) {
        String message = String.format("Calling external API failure: %s", ex.getMessage());

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.failure(message);
    }

    @ExceptionHandler(ReentryException.class)
    public ApiResult handleReInException(HttpServletRequest request, ReentryException ex) {
        exceptionNotifier.notify(request, ex);

        return ApiResultFactory.reentry(ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ApiResult handleAllExceptions(HttpServletRequest request, Exception ex) {
        // prompt the root exception message
        String message = ex.getMessage();
        while (ex.getCause() instanceof Exception) {
            message = ex.getCause().getMessage();
        }

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.systemError(String.format("请联系管理员, 系统未处理异常: %s", message));
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResult handleDuplicateKeyException(HttpServletRequest request, DuplicateKeyException ex) {
        String promptMessage = "数据已经存在";

        exceptionNotifier.notify(request, ex, promptMessage);

        return ApiResultFactory.failure(promptMessage);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResult handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException ex) {
        exceptionNotifier.notify(request, ex);

        return ApiResultFactory.badRequest(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResult handleMethodArgumentNotValid(HttpServletRequest request, MethodArgumentNotValidException ex) {
        StringBuilder sb = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            sb.append(error.getField()).append(": ").append(error.getDefaultMessage());
            sb.append(";");
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            sb.append(error.getObjectName()).append(": ").append(error.getDefaultMessage());
            sb.append(";");
        }

        exceptionNotifier.notify(request, ex, sb.toString());

        return ApiResultFactory.badRequest(sb.toString());
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ApiResult handleConstraintViolation(HttpServletRequest request, ConstraintViolationException ex) {
        StringBuilder sb = new StringBuilder();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            sb.append(violation.getRootBeanClass().getName()).append(" ").append(violation.getPropertyPath()).append(": ").append(violation.getMessage());
            sb.append(";");
        }

        exceptionNotifier.notify(request, ex, sb.toString());

        return ApiResultFactory.badRequest(sb.toString());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ApiResult handleHttpMediaTypeNotSupported(HttpServletRequest request, HttpMediaTypeNotSupportedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getContentType());
        sb.append(" media type is not supported. Supported media types are ");

        ex.getSupportedMediaTypes().forEach(t -> sb.append(t).append(", "));

        exceptionNotifier.notify(request, ex, sb.toString());

        return ApiResultFactory.badRequest(sb.toString());
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ApiResult handleMissingServletRequestParameterException(HttpServletRequest request, MissingServletRequestParameterException ex) {
        String message = String.format("缺少必要参数: %s, 参数类型: %s", ex.getParameterName(), ex.getParameterType());

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.badRequest(message);
    }

    @ExceptionHandler(JsonParseException.class)
    public ApiResult handleJsonParseException(HttpServletRequest request, JsonParseException ex) {
        String message = String.format("请求Body不是有效Json数据, 更多信息:%s", ex.getMessage());

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.badRequest(message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResult handleHttpMessageNotReadableException(HttpServletRequest request, HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();
        String message;
        if (cause instanceof JsonParseException) {
            message = String.format("请求Body不是有效Json数据, 更多信息:%s", cause.getMessage());
        } else {
            message = String.format("请求Body不是有效数据, 更多信息:%s", ex.getMessage());
        }

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.badRequest(message);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ApiResult handleHttpRequestMethodNotSupported(HttpServletRequest request, HttpRequestMethodNotSupportedException ex) {
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getMethod());
        sb.append(" method is not supported for this request. Supported methods are ");

        ex.getSupportedHttpMethods().forEach(t -> sb.append(t).append(" "));

        exceptionNotifier.notify(request, ex, sb.toString());

        return ApiResultFactory.badRequest(sb.toString());
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ApiResult handleNoHandlerFoundException(HttpServletRequest request, NoHandlerFoundException ex) {
        String message = String.format("No handler found for '%s' , Url: %s", ex.getHttpMethod(), ex.getRequestURL());

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.badRequest(message);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResult handleMethodArgumentTypeMismatch(HttpServletRequest request, MethodArgumentTypeMismatchException ex) {
        String message = ex.getName() + " should be of type " + ex.getRequiredType().getName();

        exceptionNotifier.notify(request, ex, message);

        return ApiResultFactory.badRequest(message);
    }
}