/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.util;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Represents an utility to generate an global unique key.
 */
public class CryptoRandom {
    private final static SecureRandom secureRandom = new SecureRandom();

    public static String createUniqueKey(int length) {
        byte[] token = new byte[length];
        secureRandom.nextBytes(token);

        return new BigInteger(1, token).toString(16);
    }
}
