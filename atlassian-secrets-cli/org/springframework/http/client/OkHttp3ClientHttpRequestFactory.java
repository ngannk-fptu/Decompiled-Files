/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  okhttp3.Cache
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.Request$Builder
 *  okhttp3.RequestBody
 *  okhttp3.internal.http.HttpMethod
 */
package org.springframework.http.client;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import okhttp3.Cache;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.OkHttp3AsyncClientHttpRequest;
import org.springframework.http.client.OkHttp3ClientHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

public class OkHttp3ClientHttpRequestFactory
implements ClientHttpRequestFactory,
AsyncClientHttpRequestFactory,
DisposableBean {
    private OkHttpClient client;
    private final boolean defaultClient;

    public OkHttp3ClientHttpRequestFactory() {
        this.client = new OkHttpClient();
        this.defaultClient = true;
    }

    public OkHttp3ClientHttpRequestFactory(OkHttpClient client) {
        Assert.notNull((Object)client, "OkHttpClient must not be null");
        this.client = client;
        this.defaultClient = false;
    }

    public void setReadTimeout(int readTimeout) {
        this.client = this.client.newBuilder().readTimeout((long)readTimeout, TimeUnit.MILLISECONDS).build();
    }

    public void setWriteTimeout(int writeTimeout) {
        this.client = this.client.newBuilder().writeTimeout((long)writeTimeout, TimeUnit.MILLISECONDS).build();
    }

    public void setConnectTimeout(int connectTimeout) {
        this.client = this.client.newBuilder().connectTimeout((long)connectTimeout, TimeUnit.MILLISECONDS).build();
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) {
        return new OkHttp3ClientHttpRequest(this.client, uri, httpMethod);
    }

    @Override
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) {
        return new OkHttp3AsyncClientHttpRequest(this.client, uri, httpMethod);
    }

    @Override
    public void destroy() throws IOException {
        if (this.defaultClient) {
            Cache cache = this.client.cache();
            if (cache != null) {
                cache.close();
            }
            this.client.dispatcher().executorService().shutdown();
        }
    }

    static Request buildRequest(HttpHeaders headers, byte[] content, URI uri, HttpMethod method) throws MalformedURLException {
        MediaType contentType = OkHttp3ClientHttpRequestFactory.getContentType(headers);
        RequestBody body = content.length > 0 || okhttp3.internal.http.HttpMethod.requiresRequestBody((String)method.name()) ? RequestBody.create((MediaType)contentType, (byte[])content) : null;
        Request.Builder builder = new Request.Builder().url(uri.toURL()).method(method.name(), body);
        headers.forEach((headerName, headerValues) -> {
            for (String headerValue : headerValues) {
                builder.addHeader(headerName, headerValue);
            }
        });
        return builder.build();
    }

    @Nullable
    private static MediaType getContentType(HttpHeaders headers) {
        String rawContentType = headers.getFirst("Content-Type");
        return StringUtils.hasText(rawContentType) ? MediaType.parse((String)rawContentType) : null;
    }
}

