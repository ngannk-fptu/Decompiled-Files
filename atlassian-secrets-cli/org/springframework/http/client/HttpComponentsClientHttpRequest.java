/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpUriRequest
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.URI;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.AbstractBufferingClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpResponse;
import org.springframework.util.StringUtils;

final class HttpComponentsClientHttpRequest
extends AbstractBufferingClientHttpRequest {
    private final HttpClient httpClient;
    private final HttpUriRequest httpRequest;
    private final HttpContext httpContext;

    HttpComponentsClientHttpRequest(HttpClient client, HttpUriRequest request, HttpContext context) {
        this.httpClient = client;
        this.httpRequest = request;
        this.httpContext = context;
    }

    @Override
    public String getMethodValue() {
        return this.httpRequest.getMethod();
    }

    @Override
    public URI getURI() {
        return this.httpRequest.getURI();
    }

    HttpContext getHttpContext() {
        return this.httpContext;
    }

    @Override
    protected ClientHttpResponse executeInternal(HttpHeaders headers, byte[] bufferedOutput) throws IOException {
        HttpComponentsClientHttpRequest.addHeaders(this.httpRequest, headers);
        if (this.httpRequest instanceof HttpEntityEnclosingRequest) {
            HttpEntityEnclosingRequest entityEnclosingRequest = (HttpEntityEnclosingRequest)this.httpRequest;
            ByteArrayEntity requestEntity = new ByteArrayEntity(bufferedOutput);
            entityEnclosingRequest.setEntity(requestEntity);
        }
        HttpResponse httpResponse = this.httpClient.execute(this.httpRequest, this.httpContext);
        return new HttpComponentsClientHttpResponse(httpResponse);
    }

    static void addHeaders(HttpUriRequest httpRequest, HttpHeaders headers) {
        headers.forEach((headerName, headerValues) -> {
            if ("Cookie".equalsIgnoreCase((String)headerName)) {
                String headerValue = StringUtils.collectionToDelimitedString(headerValues, "; ");
                httpRequest.addHeader(headerName, headerValue);
            } else if (!"Content-Length".equalsIgnoreCase((String)headerName) && !"Transfer-Encoding".equalsIgnoreCase((String)headerName)) {
                for (String headerValue : headerValues) {
                    httpRequest.addHeader(headerName, headerValue);
                }
            }
        });
    }
}

