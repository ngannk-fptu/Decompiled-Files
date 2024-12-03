/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.interceptor.httpmethod;

public enum HttpMethod {
    GET,
    HEAD,
    POST,
    PUT,
    DELETE,
    TRACE,
    OPTIONS,
    CONNECT,
    PATCH;


    public static HttpMethod parse(String httpRequestMethod) {
        return HttpMethod.valueOf(httpRequestMethod.toUpperCase());
    }
}

