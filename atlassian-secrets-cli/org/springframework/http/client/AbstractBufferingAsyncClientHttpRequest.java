/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
abstract class AbstractBufferingAsyncClientHttpRequest
extends AbstractAsyncClientHttpRequest {
    private ByteArrayOutputStream bufferedOutput = new ByteArrayOutputStream(1024);

    AbstractBufferingAsyncClientHttpRequest() {
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        return this.bufferedOutput;
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        byte[] bytes = this.bufferedOutput.toByteArray();
        if (headers.getContentLength() < 0L) {
            headers.setContentLength(bytes.length);
        }
        ListenableFuture<ClientHttpResponse> result = this.executeInternal(headers, bytes);
        this.bufferedOutput = new ByteArrayOutputStream(0);
        return result;
    }

    protected abstract ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders var1, byte[] var2) throws IOException;
}

