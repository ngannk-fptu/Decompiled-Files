/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.function.Consumer;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

class DefaultServerHttpRequestBuilder
implements ServerHttpRequest.Builder {
    private URI uri;
    private HttpHeaders headers;
    private String httpMethodValue;
    @Nullable
    private String uriPath;
    @Nullable
    private String contextPath;
    @Nullable
    private SslInfo sslInfo;
    @Nullable
    private InetSocketAddress remoteAddress;
    private Flux<DataBuffer> body;
    private final ServerHttpRequest originalRequest;

    public DefaultServerHttpRequestBuilder(ServerHttpRequest original) {
        Assert.notNull((Object)original, "ServerHttpRequest is required");
        this.uri = original.getURI();
        this.headers = HttpHeaders.writableHttpHeaders(original.getHeaders());
        this.httpMethodValue = original.getMethodValue();
        this.contextPath = original.getPath().contextPath().value();
        this.remoteAddress = original.getRemoteAddress();
        this.body = original.getBody();
        this.originalRequest = original;
    }

    @Override
    public ServerHttpRequest.Builder method(HttpMethod httpMethod) {
        this.httpMethodValue = httpMethod.name();
        return this;
    }

    @Override
    public ServerHttpRequest.Builder uri(URI uri) {
        this.uri = uri;
        return this;
    }

    @Override
    public ServerHttpRequest.Builder path(String path) {
        Assert.isTrue(path.startsWith("/"), () -> "The path does not have a leading slash: " + path);
        this.uriPath = path;
        return this;
    }

    @Override
    public ServerHttpRequest.Builder contextPath(String contextPath) {
        this.contextPath = contextPath;
        return this;
    }

    @Override
    public ServerHttpRequest.Builder header(String headerName, String ... headerValues) {
        this.headers.put(headerName, Arrays.asList(headerValues));
        return this;
    }

    @Override
    public ServerHttpRequest.Builder headers(Consumer<HttpHeaders> headersConsumer) {
        Assert.notNull(headersConsumer, "'headersConsumer' must not be null");
        headersConsumer.accept(this.headers);
        return this;
    }

    @Override
    public ServerHttpRequest.Builder sslInfo(SslInfo sslInfo) {
        this.sslInfo = sslInfo;
        return this;
    }

    @Override
    public ServerHttpRequest.Builder remoteAddress(InetSocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
        return this;
    }

    @Override
    public ServerHttpRequest build() {
        return new MutatedServerHttpRequest(this.getUriToUse(), this.contextPath, this.httpMethodValue, this.sslInfo, this.remoteAddress, this.headers, this.body, this.originalRequest);
    }

    private URI getUriToUse() {
        if (this.uriPath == null) {
            return this.uri;
        }
        StringBuilder uriBuilder = new StringBuilder();
        if (this.uri.getScheme() != null) {
            uriBuilder.append(this.uri.getScheme()).append(':');
        }
        if (this.uri.getRawUserInfo() != null || this.uri.getHost() != null) {
            uriBuilder.append("//");
            if (this.uri.getRawUserInfo() != null) {
                uriBuilder.append(this.uri.getRawUserInfo()).append('@');
            }
            if (this.uri.getHost() != null) {
                uriBuilder.append(this.uri.getHost());
            }
            if (this.uri.getPort() != -1) {
                uriBuilder.append(':').append(this.uri.getPort());
            }
        }
        if (StringUtils.hasLength(this.uriPath)) {
            uriBuilder.append(this.uriPath);
        }
        if (this.uri.getRawQuery() != null) {
            uriBuilder.append('?').append(this.uri.getRawQuery());
        }
        if (this.uri.getRawFragment() != null) {
            uriBuilder.append('#').append(this.uri.getRawFragment());
        }
        try {
            return new URI(uriBuilder.toString());
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URI path: \"" + this.uriPath + "\"", ex);
        }
    }

    private static class MutatedServerHttpRequest
    extends AbstractServerHttpRequest {
        private final String methodValue;
        @Nullable
        private final SslInfo sslInfo;
        @Nullable
        private InetSocketAddress remoteAddress;
        private final Flux<DataBuffer> body;
        private final ServerHttpRequest originalRequest;

        public MutatedServerHttpRequest(URI uri, @Nullable String contextPath, String methodValue, @Nullable SslInfo sslInfo, @Nullable InetSocketAddress remoteAddress, HttpHeaders headers, Flux<DataBuffer> body2, ServerHttpRequest originalRequest) {
            super(uri, contextPath, headers);
            this.methodValue = methodValue;
            this.remoteAddress = remoteAddress != null ? remoteAddress : originalRequest.getRemoteAddress();
            this.sslInfo = sslInfo != null ? sslInfo : originalRequest.getSslInfo();
            this.body = body2;
            this.originalRequest = originalRequest;
        }

        @Override
        public String getMethodValue() {
            return this.methodValue;
        }

        @Override
        protected MultiValueMap<String, HttpCookie> initCookies() {
            return this.originalRequest.getCookies();
        }

        @Override
        @Nullable
        public InetSocketAddress getLocalAddress() {
            return this.originalRequest.getLocalAddress();
        }

        @Override
        @Nullable
        public InetSocketAddress getRemoteAddress() {
            return this.remoteAddress;
        }

        @Override
        @Nullable
        protected SslInfo initSslInfo() {
            return this.sslInfo;
        }

        @Override
        public Flux<DataBuffer> getBody() {
            return this.body;
        }

        @Override
        public <T> T getNativeRequest() {
            return ServerHttpRequestDecorator.getNativeRequest(this.originalRequest);
        }

        @Override
        public String getId() {
            return this.originalRequest.getId();
        }
    }
}

