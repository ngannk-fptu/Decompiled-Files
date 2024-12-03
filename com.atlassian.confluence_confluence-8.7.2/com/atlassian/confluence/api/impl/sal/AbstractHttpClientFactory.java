/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.core.net.HttpClientRequestFactory
 *  org.springframework.aop.Advisor
 */
package com.atlassian.confluence.api.impl.sal;

import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.core.net.HttpClientRequestFactory;
import org.springframework.aop.Advisor;

public abstract class AbstractHttpClientFactory
implements NonMarshallingRequestFactory<Request<?, ?>> {
    protected final Advisor classLoaderAdvisor;
    protected final HttpClientRequestFactory httpClientRequestFactory;

    protected AbstractHttpClientFactory(Advisor classLoaderAdvisor) {
        this.classLoaderAdvisor = classLoaderAdvisor;
        this.httpClientRequestFactory = new HttpClientRequestFactory();
    }

    public boolean supportsHeader() {
        return this.httpClientRequestFactory.supportsHeader();
    }
}

