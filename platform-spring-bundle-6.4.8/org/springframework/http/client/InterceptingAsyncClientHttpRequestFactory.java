/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingAsyncClientHttpRequest;
import org.springframework.lang.Nullable;

@Deprecated
public class InterceptingAsyncClientHttpRequestFactory
implements AsyncClientHttpRequestFactory {
    private AsyncClientHttpRequestFactory delegate;
    private List<AsyncClientHttpRequestInterceptor> interceptors;

    public InterceptingAsyncClientHttpRequestFactory(AsyncClientHttpRequestFactory delegate, @Nullable List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.delegate = delegate;
        this.interceptors = interceptors != null ? interceptors : Collections.emptyList();
    }

    @Override
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod method) {
        return new InterceptingAsyncClientHttpRequest(this.delegate, this.interceptors, uri, method);
    }
}

