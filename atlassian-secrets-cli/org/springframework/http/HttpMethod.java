/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http;

import java.util.HashMap;
import java.util.Map;
import org.springframework.lang.Nullable;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    PATCH,
    DELETE,
    OPTIONS,
    TRACE;

    private static final Map<String, HttpMethod> mappings;

    @Nullable
    public static HttpMethod resolve(@Nullable String method) {
        return method != null ? mappings.get(method) : null;
    }

    public boolean matches(String method) {
        return this == HttpMethod.resolve(method);
    }

    static {
        mappings = new HashMap<String, HttpMethod>(16);
        for (HttpMethod httpMethod : HttpMethod.values()) {
            mappings.put(httpMethod.name(), httpMethod);
        }
    }
}

