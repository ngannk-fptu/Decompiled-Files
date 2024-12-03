/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public abstract class AbstractClientHttpRequest
implements ClientHttpRequest {
    private final HttpHeaders headers = new HttpHeaders();
    private boolean executed = false;
    @Nullable
    private HttpHeaders readOnlyHeaders;

    @Override
    public final HttpHeaders getHeaders() {
        if (this.readOnlyHeaders != null) {
            return this.readOnlyHeaders;
        }
        if (this.executed) {
            this.readOnlyHeaders = HttpHeaders.readOnlyHttpHeaders(this.headers);
            return this.readOnlyHeaders;
        }
        return this.headers;
    }

    @Override
    public final OutputStream getBody() throws IOException {
        this.assertNotExecuted();
        return this.getBodyInternal(this.headers);
    }

    @Override
    public final ClientHttpResponse execute() throws IOException {
        this.assertNotExecuted();
        ClientHttpResponse result = this.executeInternal(this.headers);
        this.executed = true;
        return result;
    }

    protected void assertNotExecuted() {
        Assert.state(!this.executed, "ClientHttpRequest already executed");
    }

    protected abstract OutputStream getBodyInternal(HttpHeaders var1) throws IOException;

    protected abstract ClientHttpResponse executeInternal(HttpHeaders var1) throws IOException;
}

