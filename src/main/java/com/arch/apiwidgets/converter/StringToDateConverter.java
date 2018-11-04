/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDateConverter implements Converter<String, Date> {
    private static final String DATE_FORMATTER = "yyyy-MM-dd";
    private static final String TIME_FORMATTER = "yyyy-MM-dd HH:mm:ss";

    @Override
    public Date convert(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        value = value.trim();

        try {
            if(value.contains("-")) {
                if(value.contains(":")) {
                    // avoid multi-thread issues
                    DateFormat timeFormatter = new SimpleDateFormat(TIME_FORMATTER);
                    return timeFormatter.parse(value);
                }else {
                    DateFormat dateFormatter = new SimpleDateFormat(DATE_FORMATTER);
                    return dateFormatter.parse(value);
                }
            } else if(value.matches("^\\d+$")) {
                return new Date(Long.valueOf(value));
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to Date fail", value));
        }

        throw new RuntimeException(String.format("parser %s to Date fail", value));
    }
}