/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StreamUtils
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.core.task.AsyncListenableTaskExecutor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleBufferingClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
final class SimpleStreamingAsyncClientHttpRequest
extends AbstractAsyncClientHttpRequest {
    private final HttpURLConnection connection;
    private final int chunkSize;
    @Nullable
    private OutputStream body;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;

    SimpleStreamingAsyncClientHttpRequest(HttpURLConnection connection, int chunkSize, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
        this.taskExecutor = taskExecutor;
    }

    @Override
    public String getMethodValue() {
        return this.connection.getRequestMethod();
    }

    @Override
    public URI getURI() {
        try {
            return this.connection.getURL().toURI();
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Could not get HttpURLConnection URI: " + ex.getMessage(), ex);
        }
    }

    @Override
    protected OutputStream getBodyInternal(HttpHeaders headers) throws IOException {
        if (this.body == null) {
            if (this.outputStreaming) {
                long contentLength = headers.getContentLength();
                if (contentLength >= 0L) {
                    this.connection.setFixedLengthStreamingMode(contentLength);
                } else {
                    this.connection.setChunkedStreamingMode(this.chunkSize);
                }
            }
            SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
            this.connection.connect();
            this.body = this.connection.getOutputStream();
        }
        return StreamUtils.nonClosing((OutputStream)this.body);
    }

    @Override
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers) throws IOException {
        return this.taskExecutor.submitListenable(() -> {
            try {
                if (this.body != null) {
                    this.body.close();
                } else {
                    SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
                    this.connection.connect();
                    this.connection.getResponseCode();
                }
            }
            catch (IOException iOException) {
                // empty catch block
            }
            return new SimpleClientHttpResponse(this.connection);
        });
    }
}

