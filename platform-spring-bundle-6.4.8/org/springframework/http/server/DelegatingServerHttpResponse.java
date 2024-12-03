/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;

public class DelegatingServerHttpResponse
implements ServerHttpResponse {
    private final ServerHttpResponse delegate;

    public DelegatingServerHttpResponse(ServerHttpResponse delegate) {
        Assert.notNull((Object)delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    public ServerHttpResponse getDelegate() {
        return this.delegate;
    }

    @Override
    public void setStatusCode(HttpStatus status) {
        this.delegate.setStatusCode(status);
    }

    @Override
    public void flush() throws IOException {
        this.delegate.flush();
    }

    @Override
    public void close() {
        this.delegate.close();
    }

    @Override
    public OutputStream getBody() throws IOException {
        return this.delegate.getBody();
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.delegate.getHeaders();
    }
}

