/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package org.springframework.http.client.support;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.http.HttpLogging;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInitializer;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.Assert;

public abstract class HttpAccessor {
    protected final Log logger = HttpLogging.forLogName(this.getClass());
    private ClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
    private final List<ClientHttpRequestInitializer> clientHttpRequestInitializers = new ArrayList<ClientHttpRequestInitializer>();

    public void setRequestFactory(ClientHttpRequestFactory requestFactory) {
        Assert.notNull((Object)requestFactory, "ClientHttpRequestFactory must not be null");
        this.requestFactory = requestFactory;
    }

    public ClientHttpRequestFactory getRequestFactory() {
        return this.requestFactory;
    }

    public void setClientHttpRequestInitializers(List<ClientHttpRequestInitializer> clientHttpRequestInitializers) {
        if (this.clientHttpRequestInitializers != clientHttpRequestInitializers) {
            this.clientHttpRequestInitializers.clear();
            this.clientHttpRequestInitializers.addAll(clientHttpRequestInitializers);
            AnnotationAwareOrderComparator.sort(this.clientHttpRequestInitializers);
        }
    }

    public List<ClientHttpRequestInitializer> getClientHttpRequestInitializers() {
        return this.clientHttpRequestInitializers;
    }

    protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
        ClientHttpRequest request = this.getRequestFactory().createRequest(url, method);
        this.initialize(request);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("HTTP " + method.name() + " " + url));
        }
        return request;
    }

    private void initialize(ClientHttpRequest request) {
        this.clientHttpRequestInitializers.forEach(initializer -> initializer.initialize(request));
    }
}

