/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

public class StringToLocalDateTimeConverter implements Converter<String, LocalDateTime> {
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public LocalDateTime convert(String value) {
        if(StringUtils.isEmpty(value)) {
            return null;
        }

        value = value.trim();

        try {
            if(value.contains("-")) {
                if(value.contains(":")) {
                    return LocalDateTime.parse(value, timeFormatter);
                }else {
                    return LocalDateTime.parse(value, dateFormatter);
                }
            } else if(value.matches("^\\d+$")) {
                return LocalDateTime.ofInstant(Instant.ofEpochMilli(Long.valueOf(value)), TimeZone.getDefault().toZoneId());
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("parser %s to LocalDateTime fail", value));
        }

        throw new RuntimeException(String.format("parser %s to LocalDateTime fail", value));
    }
}
