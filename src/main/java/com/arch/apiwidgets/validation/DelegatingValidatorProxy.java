/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.validation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Represents a validator proxy which will delegating it's checking to the specified {@link AbstractChecker AbstractChecker}
 */
public class DelegatingValidatorProxy implements ConstraintValidator<EnsureChecked, Object> {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final ConcurrentHashMap<Class, AbstractChecker> cached = new ConcurrentHashMap<>();
    private EnsureChecked ensureChecked;

    @Override
    public void initialize(EnsureChecked ensureChecked) {
        this.ensureChecked = ensureChecked;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if(value == null && !ensureChecked.isNullable()) {
            return false;
        }

        try {
            AbstractChecker checker;
            if(cached.containsKey(ensureChecked.checkBy())) {
                checker = cached.get(ensureChecked.checkBy());
            }
            else {
                checker = ensureChecked.checkBy().getDeclaredConstructor().newInstance();
                cached.put(ensureChecked.checkBy(), checker);
            }

            return checker.isValid(value, context);
        } catch (Exception e) {
            logger.error("Fluent validator get checker failure: ", e);

            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("exception: " + e.getMessage())
                   .addPropertyNode("exception")
                   .addConstraintViolation();

            return false;
        }
    }
}