/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.util;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class GUID {
    private static SecureRandom rnd;

    public static String generateFormattedGUID() {
        String guid = GUID.generateGUID();
        return guid.substring(0, 8) + '-' + guid.substring(8, 12) + '-' + guid.substring(12, 16) + '-' + guid.substring(16, 20) + '-' + guid.substring(20);
    }

    public static String generateGUID() {
        return new BigInteger(165, rnd).toString(36).toUpperCase();
    }

    static {
        try {
            rnd = SecureRandom.getInstance("SHA1PRNG");
        }
        catch (NoSuchAlgorithmException e) {
            rnd = new SecureRandom();
        }
        byte[] seed = rnd.generateSeed(64);
        rnd.setSeed(seed);
    }
}

