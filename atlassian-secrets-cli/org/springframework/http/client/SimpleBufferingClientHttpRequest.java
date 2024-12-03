/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractBufferingClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

final class SimpleBufferingClientHttpRequest
extends AbstractBufferingClientHttpRequest {
    private final HttpURLConnection connection;
    private final boolean outputStreaming;

    SimpleBufferingClientHttpRequest(HttpURLConnection connection, boolean outputStreaming) {
        this.connection = connection;
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
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        SimpleBufferingClientHttpRequest.addHeaders(this.connection, headers);
        if (this.getMethod() == HttpMethod.DELETE && bufferedOutput.length == 0) {
            this.connection.setDoOutput(false);
        }
        if (this.connection.getDoOutput() && this.outputStreaming) {
            this.connection.setFixedLengthStreamingMode(bufferedOutput.length);
        }
        this.connection.connect();
        if (this.connection.getDoOutput()) {
            FileCopyUtils.copy(bufferedOutput, this.connection.getOutputStream());
        } else {
            this.connection.getResponseCode();
        }
        return new SimpleClientHttpResponse(this.connection);
    }

    static void addHeaders(HttpURLConnection connection, HttpHeaders headers) {
        headers.forEach((headerName, headerValues) -> {
            if ("Cookie".equalsIgnoreCase((String)headerName)) {
                String headerValue = StringUtils.collectionToDelimitedString(headerValues, "; ");
                connection.setRequestProperty((String)headerName, headerValue);
            } else {
                for (String headerValue : headerValues) {
                    String actualHeaderValue = headerValue != null ? headerValue : "";
                    connection.addRequestProperty((String)headerName, actualHeaderValue);
                }
            }
        });
    }
}

