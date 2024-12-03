/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.client.support;

import java.net.URI;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class HttpRequestWrapper
implements HttpRequest {
    private final HttpRequest request;

    public HttpRequestWrapper(HttpRequest request) {
        Assert.notNull((Object)request, "HttpRequest must not be null");
        this.request = request;
    }

    public HttpRequest getRequest() {
        return this.request;
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
    public HttpHeaders getHeaders() {
        return this.request.getHeaders();
    }
}

