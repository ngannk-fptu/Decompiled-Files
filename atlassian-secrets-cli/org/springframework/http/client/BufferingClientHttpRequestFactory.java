/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractClientHttpRequestFactoryWrapper;
import org.springframework.http.client.BufferingClientHttpRequestWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;

public class BufferingClientHttpRequestFactory
extends AbstractClientHttpRequestFactoryWrapper {
    public BufferingClientHttpRequestFactory(ClientHttpRequestFactory requestFactory) {
        super(requestFactory);
    }

    @Override
    protected ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod, ClientHttpRequestFactory requestFactory) throws IOException {
        ClientHttpRequest request = requestFactory.createRequest(uri, httpMethod);
        if (this.shouldBuffer(uri, httpMethod)) {
            return new BufferingClientHttpRequestWrapper(request);
        }
        return request;
    }

    protected boolean shouldBuffer(URI uri, HttpMethod httpMethod) {
        return true;
    }
}

