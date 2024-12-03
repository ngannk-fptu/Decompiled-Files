/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.core.rest;

import com.atlassian.upm.core.rest.InvalidKeyException;

public abstract class UpmUriEscaper {
    public static final String SUFFIX = "-key";

    public static String unescape(String original) {
        if (original.endsWith(SUFFIX)) {
            return original.substring(0, original.lastIndexOf(SUFFIX));
        }
        throw new InvalidKeyException(original);
    }

    public static String escape(String original) {
        return original + SUFFIX;
    }
}

