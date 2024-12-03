/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
abstract class AbstractAsyncClientHttpRequest
implements AsyncClientHttpRequest {
    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;

    AbstractAsyncClientHttpRequest() {
    }

    @Override
    public final HttpHeaders getHeaders() {
        return this.executed ? HttpHeaders.readOnlyHttpHeaders(this.headers) : this.headers;
    }

    @Override
    public final OutputStream getBody() throws IOException {
        this.assertNotExecuted();
        return this.getBodyInternal(this.headers);
    }

    @Override
    public ListenableFuture<ClientHttpResponse> executeAsync() throws IOException {
        this.assertNotExecuted();
        ListenableFuture<ClientHttpResponse> result = this.executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    protected void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }

    protected abstract OutputStream getBodyInternal(HttpHeaders var1) throws IOException;

    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders var1) throws IOException;
}

