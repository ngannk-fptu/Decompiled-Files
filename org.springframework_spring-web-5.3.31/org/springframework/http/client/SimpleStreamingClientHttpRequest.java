/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.StreamUtils
 */
package org.springframework.http.client;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleBufferingClientHttpRequest;
import org.springframework.http.client.SimpleClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

final class SimpleStreamingClientHttpRequest
extends AbstractClientHttpRequest {
    private final HttpURLConnection connection;
    private final int chunkSize;
    @Nullable
    private OutputStream body;
    private final boolean outputStreaming;

    SimpleStreamingClientHttpRequest(HttpURLConnection connection, int chunkSize, boolean outputStreaming) {
        this.connection = connection;
        this.chunkSize = chunkSize;
        this.outputStreaming = outputStreaming;
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
    protected ClientHttpResponse executeInternal(HttpHeaders headers) throws IOException {
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
    }
}

