/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.task.AsyncListenableTaskExecutor
 *  org.springframework.util.FileCopyUtils
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
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractBufferingAsyncClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleBufferingClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.concurrent.ListenableFuture;

@Deprecated
final class SimpleBufferingAsyncClientHttpRequest
extends AbstractBufferingAsyncClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;
    private final AsyncListenableTaskExecutor taskExecutor;

    SimpleBufferingAsyncClientHttpRequest(HttpURLConnection connection, boolean outputStreaming, AsyncListenableTaskExecutor taskExecutor) {
        this.connection = connection;
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
    protected ListenableFuture<ClientHttpResponse> executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        return this.taskExecutor.submitListenable(() -> {
            SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
            if (this.getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
                this.connection.setDoOutput(false);
            }
            if (this.connection.getDoOutput() && this.outputStreaming) {
                this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
            }
            this.connection.connect();
            if (this.connection.getDoOutput()) {
                FileCopyUtils.copy((byte[])bufferedOutput, (OutputStream)this.connection.getOutputStream());
            } else {
                this.connection.getResponseCode();
            }
            return new SimpleClientHttpResponse(this.connection);
        });
    }
}

