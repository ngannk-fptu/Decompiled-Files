/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  org.apache.http.HttpRequestInterceptor
 *  org.apache.http.HttpResponseInterceptor
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.util.zipkin.api;

import com.atlassian.annotations.Internal;
import java.net.URI;
import java.util.function.BiFunction;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponseInterceptor;
import org.checkerframework.checker.nullness.qual.NonNull;

@Internal
public interface ZipkinHttpClientInterceptorManager {
    public @NonNull Iterable<HttpRequestInterceptor> requestInterceptors(String var1);

    public @NonNull Iterable<HttpRequestInterceptor> requestInterceptors(String var1, BiFunction<URI, String, String> var2);

    public @NonNull Iterable<HttpResponseInterceptor> responseInterceptors();
}

