/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.eclipse.jetty.client.api.Request
 *  org.eclipse.jetty.client.api.Response
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.http.client.reactive;

import java.lang.reflect.Method;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.api.Response;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.reactive.JettyClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

abstract class Jetty10HttpFieldsHelper {
    private static final boolean jetty10Present;
    private static final Method requestGetHeadersMethod;
    private static final Method responseGetHeadersMethod;
    private static final Method getNameMethod;
    private static final Method getValueMethod;

    Jetty10HttpFieldsHelper() {
    }

    public static boolean jetty10Present() {
        return jetty10Present;
    }

    public static HttpHeaders getHttpHeaders(Request request) {
        Iterable iterator = (Iterable)ReflectionUtils.invokeMethod((Method)requestGetHeadersMethod, (Object)request);
        return Jetty10HttpFieldsHelper.getHttpHeadersInternal(iterator);
    }

    public static HttpHeaders getHttpHeaders(Response response) {
        Iterable iterator = (Iterable)ReflectionUtils.invokeMethod((Method)responseGetHeadersMethod, (Object)response);
        return Jetty10HttpFieldsHelper.getHttpHeadersInternal(iterator);
    }

    private static HttpHeaders getHttpHeadersInternal(@Nullable Iterable<?> iterator) {
        Assert.notNull(iterator, (String)"Iterator must not be null");
        HttpHeaders headers = new HttpHeaders();
        for (Object field : iterator) {
            String name = (String)ReflectionUtils.invokeMethod((Method)getNameMethod, field);
            Assert.notNull((Object)name, (String)"Header name must not be null");
            String value = (String)ReflectionUtils.invokeMethod((Method)getValueMethod, field);
            headers.add(name, value);
        }
        return headers;
    }

    static {
        try {
            ClassLoader classLoader = JettyClientHttpResponse.class.getClassLoader();
            Class<?> httpFieldsClass = classLoader.loadClass("org.eclipse.jetty.http.HttpFields");
            jetty10Present = httpFieldsClass.isInterface();
            requestGetHeadersMethod = Request.class.getMethod("getHeaders", new Class[0]);
            responseGetHeadersMethod = Response.class.getMethod("getHeaders", new Class[0]);
            Class<?> httpFieldClass = classLoader.loadClass("org.eclipse.jetty.http.HttpField");
            getNameMethod = httpFieldClass.getMethod("getName", new Class[0]);
            getValueMethod = httpFieldClass.getMethod("getValue", new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException ex) {
            throw new IllegalStateException("No compatible Jetty version found", ex);
        }
    }
}

