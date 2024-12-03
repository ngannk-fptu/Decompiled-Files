/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.http.Header
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpEntityEnclosingRequest
 *  org.apache.http.HttpResponse
 *  org.apache.http.client.HttpClient
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpOptions
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpTrace
 *  org.apache.http.client.methods.HttpUriRequest
 *  org.apache.http.entity.InputStreamEntity
 *  org.apache.shindig.gadgets.http.HttpRequest
 *  org.apache.shindig.gadgets.http.HttpResponse
 *  org.apache.shindig.gadgets.http.HttpResponseBuilder
 */
package com.atlassian.gadgets.renderer.internal.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.shindig.gadgets.http.HttpRequest;
import org.apache.shindig.gadgets.http.HttpResponseBuilder;

public class ShindigApacheClientAdapter {
    private final HttpClient httpClient;

    public ShindigApacheClientAdapter(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public org.apache.shindig.gadgets.http.HttpResponse execute(HttpRequest request) throws IOException {
        HttpUriRequest mappedRequest = ShindigApacheClientAdapter.mapRequest(request);
        if (request.getPostBodyLength() > 0) {
            ((HttpEntityEnclosingRequest)mappedRequest).setEntity((HttpEntity)new InputStreamEntity(request.getPostBody(), (long)request.getPostBodyLength()));
        }
        return ShindigApacheClientAdapter.mapResponse(this.httpClient.execute(mappedRequest));
    }

    private static HttpUriRequest mapRequest(HttpRequest request) throws IOException {
        URI uri = request.getUri().toJavaUri();
        HttpUriRequest httpUriRequest = HttpMethod.valueOf(request.getMethod()).newMessage(uri);
        for (Map.Entry entry : request.getHeaders().entrySet()) {
            httpUriRequest.addHeader((String)entry.getKey(), StringUtils.join((Iterable)((Iterable)entry.getValue()), (char)','));
        }
        return httpUriRequest;
    }

    private static org.apache.shindig.gadgets.http.HttpResponse mapResponse(HttpResponse hcResponse) throws IOException {
        HttpResponseBuilder builder = new HttpResponseBuilder();
        for (Header header : hcResponse.getAllHeaders()) {
            builder.addHeader(header.getName(), header.getValue());
        }
        return builder.setHttpStatusCode(hcResponse.getStatusLine().getStatusCode()).setResponse(IOUtils.toByteArray((InputStream)hcResponse.getEntity().getContent())).create();
    }

    private static enum HttpMethod {
        GET,
        POST,
        DELETE,
        PUT,
        HEAD,
        OPTIONS,
        TRACE;


        public HttpUriRequest newMessage(URI uri) {
            switch (this) {
                case GET: {
                    return new HttpGet(uri);
                }
                case POST: {
                    return new HttpPost(uri);
                }
                case DELETE: {
                    return new HttpDelete(uri);
                }
                case PUT: {
                    return new HttpPut(uri);
                }
                case HEAD: {
                    return new HttpHead(uri);
                }
                case OPTIONS: {
                    return new HttpOptions(uri);
                }
                case TRACE: {
                    return new HttpTrace(uri);
                }
            }
            throw new IllegalStateException("Just not possible");
        }
    }
}

