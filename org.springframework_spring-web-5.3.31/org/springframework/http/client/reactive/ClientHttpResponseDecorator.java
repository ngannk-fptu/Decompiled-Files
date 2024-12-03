/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.util.Assert
 *  org.springframework.util.MultiValueMap
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.client.reactive;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;

public class ClientHttpResponseDecorator
implements ClientHttpResponse {
    private final ClientHttpResponse delegate;

    public ClientHttpResponseDecorator(ClientHttpResponse delegate) {
        Assert.notNull((Object)delegate, (String)"Delegate is required");
        this.delegate = delegate;
    }

    public ClientHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override
    public String getId() {
        return this.delegate.getId();
    }

    @Override
    public HttpStatus getStatusCode() {
        return this.delegate.getStatusCode();
    }

    @Override
    public int getRawStatusCode() {
        return this.delegate.getRawStatusCode();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        return this.delegate.getCookies();
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.delegate.getBody();
    }

    public String toString() {
        return this.getClass().getSimpleName() + " [delegate=" + this.getDelegate() + "]";
    }
}

