/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.util.ArrayList;
import java.util.List;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.InterceptingClientHttpRequestFactory;
import org.springframework.http.client.support.HttpAccessor;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;

public abstract class InterceptingHttpAccessor
extends HttpAccessor {
    private final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
    @Nullable
    private volatile ClientHttpRequestFactory interceptingRequestFactory;

    public void setInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
        if (this.interceptors != interceptors) {
            this.interceptors.clear();
            this.interceptors.addAll(interceptors);
            AnnotationAwareOrderComparator.sort(this.interceptors);
        }
    }

    public List<ClientHttpRequestInterceptor> getInterceptors() {
        return this.interceptors;
    }

    @Override
    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        super.setRequestFactory(requestFactory);
        this.interceptingRequestFactory = null;
    }

    @Override
    public ClientHttpRequestFactory getRequestFactory() {
        List<ClientHttpRequestInterceptor> interceptors = this.getInterceptors();
        if (!CollectionUtils.isEmpty(interceptors)) {
            ClientHttpRequestFactory factory = this.interceptingRequestFactory;
            if (factory == null) {
                this.interceptingRequestFactory = factory = new InterceptingClientHttpRequestFactory(super.getRequestFactory(), interceptors);
            }
            return factory;
        }
        return super.getRequestFactory();
    }
}

