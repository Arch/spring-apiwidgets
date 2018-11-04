/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.validation;

import javax.validation.ConstraintValidatorContext;

/**
 * Represents an abstract checker for he specified type.
 * @param <T> The type of the should be checking.
 */
public abstract class AbstractChecker<T> {
    public boolean isValid(T value, ConstraintValidatorContext context) {
        return true;
    }

    protected boolean invalid(ConstraintValidatorContext context, String parameterName, String promptMessage) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(promptMessage)
               .addPropertyNode(parameterName)
               .addConstraintViolation();

        return false;
    }
}