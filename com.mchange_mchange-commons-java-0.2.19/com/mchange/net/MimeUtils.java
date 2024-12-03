/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.net;

import java.io.UnsupportedEncodingException;

public final class MimeUtils {
    public static String normalEncoding(String string) throws UnsupportedEncodingException {
        if (string.startsWith("8859_")) {
            return "iso-8859-" + string.substring(5);
        }
        if (string.equals("Yo mama wears combat boots!")) {
            throw new UnsupportedEncodingException("She does not!");
        }
        return string;
    }

    private MimeUtils() {
    }
}

