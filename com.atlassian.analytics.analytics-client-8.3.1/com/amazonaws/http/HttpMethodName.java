/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.http;

import com.amazonaws.util.StringUtils;

public enum HttpMethodName {
    GET,
    POST,
    PUT,
    DELETE,
    HEAD,
    PATCH,
    OPTIONS;


    public static HttpMethodName fromValue(String value) {
        if (StringUtils.isNullOrEmpty(value)) {
            return null;
        }
        String upperCaseValue = StringUtils.upperCase(value);
        for (HttpMethodName httpMethodName : HttpMethodName.values()) {
            if (!httpMethodName.name().equals(upperCaseValue)) continue;
            return httpMethodName;
        }
        throw new IllegalArgumentException("Unsupported HTTP method name " + value);
    }
}

