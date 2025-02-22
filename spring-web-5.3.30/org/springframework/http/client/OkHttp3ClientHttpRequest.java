/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AbstractBufferingClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3ClientHttpResponse;

class OkHttp3ClientHttpRequest
extends AbstractBufferingClientHttpRequest {
    private final OkHttpClient client;
    private final URI uri;
    private final HttpMethod method;

    public OkHttp3ClientHttpRequest(OkHttpClient client, URI uri, HttpMethod method) {
        this.client = client;
        this.uri = uri;
        this.method = method;
    }

    @Override
    public HttpMethod getMethod() {
        return this.method;
    }

    @Override
    public String getMethodValue() {
        return this.method.name();
    }

    @Override
    public URI getURI() {
        return this.uri;
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] content) throws IOException {
        Request request = OkHttp3ClientHttpRequestFactory.buildRequest(headers, content, this.uri, this.method);
        return new OkHttp3ClientHttpResponse(this.client.newCall(request).execute());
    }
}

