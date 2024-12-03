/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.AsyncClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingAsyncClientHttpRequestFactory;
import org.springframework.http.client.support.AsyncHttpAccessor;
import org.springframework.util.CollectionUtils;

@Deprecated
public abstract class InterceptingAsyncHttpAccessor
extends AsyncHttpAccessor {
    private List<AsyncClientHttpRequestInterceptor> interceptors = new ArrayList<AsyncClientHttpRequestInterceptor>();

    public void setInterceptors(List<AsyncClientHttpRequestInterceptor> interceptors) {
        this.interceptors = interceptors;
    }

    public List<AsyncClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override
    public AsyncClientHttpRequestFactory getAsyncRequestFactory() {
        AsyncClientHttpRequestFactory delegate = super.getAsyncRequestFactory();
        if (!CollectionUtils.isEmpty(this.getInterceptors())) {
            return new InterceptingAsyncClientHttpRequestFactory(delegate, this.getInterceptors());
        }
        return delegate;
    }
}

