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
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractBufferingClientHttpRequest;
import org.springframework.http.client.BufferingClientHttpResponseWrapper;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.util.StreamUtils;

final class BufferingClientHttpRequestWrapper
extends AbstractBufferingClientHttpRequest {
    private final ClientHttpRequest request;

    BufferingClientHttpRequestWrapper(ClientHttpRequest request) {
        this.request = request;
    }

    @Override
    @Nullable
    public HttpMethod getMethod() {
        return this.request.getMethod();
    }

    @Override
    public String getMethodValue() {
        return this.request.getMethodValue();
    }

    @Override
    public URI getURI() {
        return this.request.getURI();
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        this.request.getHeaders().putAll((Map<? extends String, ? extends List<String>>)((Object)headers));
        StreamUtils.copy((byte[])bufferedOutput, (OutputStream)this.request.getBody());
        ClientHttpResponse response = this.request.execute();
        return new BufferingClientHttpResponseWrapper(response);
    }
}

