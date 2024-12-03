/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.mywork.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

public class GlobalIdFactory {
    public static String encode(List<String> keys, Map<String, String> values) {
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            if (key == null) {
                throw new IllegalArgumentException("Keys cannot be null");
            }
            String value = values.get(key);
            sb.append(GlobalIdFactory.urlEncode(key)).append('=').append(value != null ? GlobalIdFactory.urlEncode(value) : "");
            sb.append('&');
        }
        return sb.substring(0, sb.length() - 1);
    }

    private static String urlEncode(String value) {
        try {
            return URLEncoder.encode(value, "UTF-8");
        }
        catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}

