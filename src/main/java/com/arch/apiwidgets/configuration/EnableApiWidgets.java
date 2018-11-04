/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
@Documented
@Import({ ApiWidgetsConfiguration.class, PropertiesConfiguration.class, JdbcConfiguration.class, RedisConfiguration.class })
@Configuration
public @interface EnableApiWidgets {
    String corsPathPattern() default "/api/**";
}
