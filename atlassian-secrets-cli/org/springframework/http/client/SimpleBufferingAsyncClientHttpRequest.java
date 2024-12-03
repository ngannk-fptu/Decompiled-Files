/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;
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
    protected ListenableFuture<ClientHttpResponse> executeInternal(final HttpHeaders headers, final byte[] bufferedOutput) throws IOException {
        return this.taskExecutor.submitListenable(new Callable<ClientHttpResponse>(){

            @Override
            public ClientHttpResponse call() throws Exception {
                SimpleBufferingClientHttpRequest.addHeaders(SimpleBufferingAsyncClientHttpRequest.this.connection, headers);
                if (SimpleBufferingAsyncClientHttpRequest.this.getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.setDoOutput(false);
                }
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput() && SimpleBufferingAsyncClientHttpRequest.this.outputStreaming) {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
                }
                SimpleBufferingAsyncClientHttpRequest.this.connection.connect();
                if (SimpleBufferingAsyncClientHttpRequest.this.connection.getDoOutput()) {
                    FileCopyUtils.copy(bufferedOutput, SimpleBufferingAsyncClientHttpRequest.this.connection.getOutputStream());
                } else {
                    SimpleBufferingAsyncClientHttpRequest.this.connection.getResponseCode();
                }
                return new SimpleClientHttpResponse(SimpleBufferingAsyncClientHttpRequest.this.connection);
            }
        });
    }
}

