/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.client.config.RequestConfig
 *  org.apache.http.client.config.RequestConfig$Builder
 *  org.apache.http.client.methods.HttpDelete
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.apache.http.client.methods.HttpGet
 *  org.apache.http.client.methods.HttpHead
 *  org.apache.http.client.methods.HttpOptions
 *  org.apache.http.client.methods.HttpPatch
 *  org.apache.http.client.methods.HttpPost
 *  org.apache.http.client.methods.HttpPut
 *  org.apache.http.client.methods.HttpRequestBase
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.http.HttpExecuteRequest
 *  software.amazon.awssdk.http.SdkHttpMethod
 *  software.amazon.awssdk.http.SdkHttpRequest
 *  software.amazon.awssdk.utils.NumericUtils
 *  software.amazon.awssdk.utils.StringUtils
 *  software.amazon.awssdk.utils.http.SdkHttpUtils
 */
package software.amazon.awssdk.http.apache.internal.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpExecuteRequest;
import software.amazon.awssdk.http.SdkHttpMethod;
import software.amazon.awssdk.http.SdkHttpRequest;
import software.amazon.awssdk.http.apache.internal.ApacheHttpRequestConfig;
import software.amazon.awssdk.http.apache.internal.RepeatableInputStreamRequestEntity;
import software.amazon.awssdk.http.apache.internal.utils.ApacheUtils;
import software.amazon.awssdk.utils.NumericUtils;
import software.amazon.awssdk.utils.StringUtils;
import software.amazon.awssdk.utils.http.SdkHttpUtils;

@SdkInternalApi
public class ApacheHttpRequestFactory {
    private static final List<String> IGNORE_HEADERS = Arrays.asList("Content-Length", "Host", "Transfer-Encoding");

    public HttpRequestBase create(HttpExecuteRequest request, ApacheHttpRequestConfig requestConfig) {
        HttpRequestBase base = this.createApacheRequest(request, this.sanitizeUri(request.httpRequest()));
        this.addHeadersToRequest(base, request.httpRequest());
        this.addRequestConfig(base, request.httpRequest(), requestConfig);
        return base;
    }

    private URI sanitizeUri(SdkHttpRequest request) {
        String path = request.encodedPath();
        if (path.contains("//")) {
            int port = request.port();
            String protocol = request.protocol();
            String newPath = StringUtils.replace((String)path, (String)"//", (String)"/%2F");
            String encodedQueryString = request.encodedQueryParameters().map(value -> "?" + value).orElse("");
            String portString = SdkHttpUtils.isUsingStandardPort((String)protocol, (Integer)port) ? "" : ":" + port;
            return URI.create(protocol + "://" + request.host() + portString + newPath + encodedQueryString);
        }
        return request.getUri();
    }

    private void addRequestConfig(HttpRequestBase base, SdkHttpRequest request, ApacheHttpRequestConfig requestConfig) {
        int connectTimeout = NumericUtils.saturatedCast((long)requestConfig.connectionTimeout().toMillis());
        int connectAcquireTimeout = NumericUtils.saturatedCast((long)requestConfig.connectionAcquireTimeout().toMillis());
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setConnectionRequestTimeout(connectAcquireTimeout).setConnectTimeout(connectTimeout).setSocketTimeout(NumericUtils.saturatedCast((long)requestConfig.socketTimeout().toMillis())).setLocalAddress(requestConfig.localAddress());
        ApacheUtils.disableNormalizeUri(requestConfigBuilder);
        if (SdkHttpMethod.PUT == request.method() && requestConfig.expectContinueEnabled()) {
            requestConfigBuilder.setExpectContinueEnabled(true);
        }
        base.setConfig(requestConfigBuilder.build());
    }

    private HttpRequestBase createApacheRequest(HttpExecuteRequest request, URI uri) {
        switch (request.httpRequest().method()) {
            case HEAD: {
                return new HttpHead(uri);
            }
            case GET: {
                return new HttpGet(uri);
            }
            case DELETE: {
                return new HttpDelete(uri);
            }
            case OPTIONS: {
                return new HttpOptions(uri);
            }
            case PATCH: {
                return this.wrapEntity(request, (HttpEntityEnclosingRequestBase)new HttpPatch(uri));
            }
            case POST: {
                return this.wrapEntity(request, (HttpEntityEnclosingRequestBase)new HttpPost(uri));
            }
            case PUT: {
                return this.wrapEntity(request, (HttpEntityEnclosingRequestBase)new HttpPut(uri));
            }
        }
        throw new RuntimeException("Unknown HTTP method name: " + request.httpRequest().method());
    }

    private HttpRequestBase wrapEntity(HttpExecuteRequest request, HttpEntityEnclosingRequestBase entityEnclosingRequest) {
        if (request.contentStreamProvider().isPresent()) {
            RepeatableInputStreamRequestEntity entity = new RepeatableInputStreamRequestEntity(request);
            if (!request.httpRequest().firstMatchingHeader("Content-Length").isPresent() && !entity.isChunked()) {
                entity = ApacheUtils.newBufferedHttpEntity((HttpEntity)entity);
            }
            entityEnclosingRequest.setEntity((HttpEntity)entity);
        }
        return entityEnclosingRequest;
    }

    private void addHeadersToRequest(HttpRequestBase httpRequest, SdkHttpRequest request) {
        httpRequest.addHeader("Host", this.getHostHeaderValue(request));
        request.forEachHeader((name, value) -> {
            if (!IGNORE_HEADERS.contains(name)) {
                for (String headerValue : value) {
                    httpRequest.addHeader(name, headerValue);
                }
            }
        });
    }

    private String getHostHeaderValue(SdkHttpRequest request) {
        return !SdkHttpUtils.isUsingStandardPort((String)request.protocol(), (Integer)request.port()) ? request.host() + ":" + request.port() : request.host();
    }
}

