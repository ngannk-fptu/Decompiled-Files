/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.http.server.reactive;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.function.Consumer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpRequest;
import org.springframework.http.ReactiveHttpInputMessage;
import org.springframework.http.server.RequestPath;
import org.springframework.http.server.reactive.DefaultServerHttpRequestBuilder;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

public interface ServerHttpRequest
extends HttpRequest,
ReactiveHttpInputMessage {
    public String getId();

    public RequestPath getPath();

    public MultiValueMap<String, String> getQueryParams();

    public MultiValueMap<String, HttpCookie> getCookies();

    @Nullable
    default public InetSocketAddress getLocalAddress() {
        return null;
    }

    @Nullable
    default public InetSocketAddress getRemoteAddress() {
        return null;
    }

    @Nullable
    default public SslInfo getSslInfo() {
        return null;
    }

    default public Builder mutate() {
        return new DefaultServerHttpRequestBuilder(this);
    }

    public static interface Builder {
        public Builder method(HttpMethod var1);

        public Builder uri(URI var1);

        public Builder path(String var1);

        public Builder contextPath(String var1);

        public Builder header(String var1, String ... var2);

        public Builder headers(Consumer<HttpHeaders> var1);

        public Builder sslInfo(SslInfo var1);

        public Builder remoteAddress(InetSocketAddress var1);

        public ServerHttpRequest build();
    }
}

