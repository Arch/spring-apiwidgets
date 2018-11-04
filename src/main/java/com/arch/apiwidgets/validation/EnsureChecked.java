/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { DelegatingValidatorProxy.class })
public @interface EnsureChecked {
    String message() default "The post JSON is incorrect";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    boolean isNullable() default false;

    Class<? extends AbstractChecker> checkBy();
}