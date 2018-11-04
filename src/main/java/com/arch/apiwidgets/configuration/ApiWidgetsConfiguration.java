/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.configuration;

import com.arch.apiwidgets.core.ExceptionNotifier;
import com.arch.apiwidgets.filter.CaseInsensitiveRequestFilter;
import com.arch.apiwidgets.filter.FilterExceptionHandlerFilter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.arch.apiwidgets.converter.StringToDateConverter;
import com.arch.apiwidgets.core.ApiExceptionHandler;
import com.arch.apiwidgets.core.ApiWidgetsInitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportAware;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.CommonsRequestLoggingFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.text.SimpleDateFormat;
import java.util.Map;

@Configuration
public class ApiWidgetsConfiguration implements WebMvcConfigurer, ImportAware {
    private String corsPathPattern;

    @Bean
    public FilterRegistrationBean filterExceptionHandlerFilter(ExceptionNotifier exceptionNotifier) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        FilterExceptionHandlerFilter filter = new FilterExceptionHandlerFilter(exceptionNotifier);
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("FilterExceptionHandlerFilter");
        registration.setOrder(Integer.MIN_VALUE);
        return registration;
    }

    @Bean
    public FilterRegistrationBean caseInsensitiveFilter() {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        CaseInsensitiveRequestFilter filter = new CaseInsensitiveRequestFilter();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("CaseInsensitiveRequestFilter");
        registration.setOrder(Integer.MIN_VALUE + 1);
        return registration;
    }

    @Bean
    public CommonsRequestLoggingFilter logFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter();
        filter.setIncludeClientInfo(true);
        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setMaxPayloadLength(10000);
        filter.setIncludeHeaders(false);
        filter.setAfterMessagePrefix("REQUEST DATA : ");
        return filter;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return objectMapper;
    }

    @Bean
    @ConditionalOnMissingBean
    public ExceptionNotifier exceptionNotifier() {
        return new ExceptionNotifier();
    }

    @Bean
    public ApiExceptionHandler apiExceptionHandler(ExceptionNotifier exceptionNotifier) {
        return new ApiExceptionHandler(exceptionNotifier);
    }

    @Bean
    public StringToDateConverter stringToDateConverter() {
        return new StringToDateConverter();
    }

    @Bean
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public ApiWidgetsInitializingBean apiWidgetsInitializingBean(RequestMappingHandlerAdapter adapter) {
        return new ApiWidgetsInitializingBean(adapter);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping(corsPathPattern)
                .allowedOrigins("*")
                .allowedHeaders("*")
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void configurePathMatch(PathMatchConfigurer configurer) {
        AntPathMatcher matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
        configurer.setPathMatcher(matcher);
    }

    @Override
    public void setImportMetadata(AnnotationMetadata importMetadata) {
        Map<String, Object> map = importMetadata.getAnnotationAttributes(EnableApiWidgets.class.getName());
        AnnotationAttributes attributes = AnnotationAttributes.fromMap(map);
        corsPathPattern = attributes.getString("corsPathPattern");
    }
}