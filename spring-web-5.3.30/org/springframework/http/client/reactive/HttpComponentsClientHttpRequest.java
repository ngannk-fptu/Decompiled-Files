/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.hc.client5.http.cookie.Cookie
 *  org.apache.hc.client5.http.cookie.CookieStore
 *  org.apache.hc.client5.http.impl.cookie.BasicClientCookie
 *  org.apache.hc.client5.http.protocol.HttpClientContext
 *  org.apache.hc.core5.http.ContentType
 *  org.apache.hc.core5.http.HttpMessage
 *  org.apache.hc.core5.http.HttpRequest
 *  org.apache.hc.core5.http.message.BasicHttpRequest
 *  org.apache.hc.core5.http.nio.AsyncRequestProducer
 *  org.apache.hc.core5.http.nio.support.BasicRequestProducer
 *  org.apache.hc.core5.reactive.ReactiveEntityProducer
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.client.reactive;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import org.apache.hc.client5.http.cookie.Cookie;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.cookie.BasicClientCookie;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.HttpMessage;
import org.apache.hc.core5.http.HttpRequest;
import org.apache.hc.core5.http.message.BasicHttpRequest;
import org.apache.hc.core5.http.nio.AsyncRequestProducer;
import org.apache.hc.core5.http.nio.support.BasicRequestProducer;
import org.apache.hc.core5.reactive.ReactiveEntityProducer;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.reactive.AbstractClientHttpRequest;
import org.springframework.http.client.reactive.HttpComponentsHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

class HttpComponentsClientHttpRequest
extends AbstractClientHttpRequest {
    private final HttpRequest httpRequest;
    private final DataBufferFactory dataBufferFactory;
    private final HttpClientContext context;
    @Nullable
    private Flux<ByteBuffer> byteBufferFlux;
    private transient long contentLength = -1L;

    public HttpComponentsClientHttpRequest(HttpMethod method, URI uri, HttpClientContext context, DataBufferFactory dataBufferFactory) {
        this.context = context;
        this.httpRequest = new BasicHttpRequest(method.name(), uri);
        this.dataBufferFactory = dataBufferFactory;
    }

    @Override
    public HttpMethod getMethod() {
        HttpMethod method = HttpMethod.resolve(this.httpRequest.getMethod());
        Assert.state((method != null ? 1 : 0) != 0, (String)"Method must not be null");
        return method;
    }

    @Override
    public URI getURI() {
        try {
            return this.httpRequest.getUri();
        }
        catch (URISyntaxException ex) {
            throw new IllegalArgumentException("Invalid URI syntax: " + ex.getMessage());
        }
    }

    @Override
    public DataBufferFactory bufferFactory() {
        return this.dataBufferFactory;
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.httpRequest;
    }

    @Override
    public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
        return this.doCommit(() -> {
            this.byteBufferFlux = Flux.from((Publisher)body).map(DataBuffer::asByteBuffer);
            return Mono.empty();
        });
    }

    @Override
    public Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
        return this.writeWith((Publisher<? extends DataBuffer>)Flux.from(body).flatMap(Function.identity()));
    }

    @Override
    public Mono<Void> setComplete() {
        return this.doCommit();
    }

    @Override
    protected void applyHeaders() {
        HttpHeaders headers = this.getHeaders();
        headers.entrySet().stream().filter(entry -> !"Content-Length".equals(entry.getKey())).forEach(entry -> ((List)entry.getValue()).forEach(v -> this.httpRequest.addHeader((String)entry.getKey(), v)));
        if (!this.httpRequest.containsHeader("Accept")) {
            this.httpRequest.addHeader("Accept", (Object)"*/*");
        }
        this.contentLength = headers.getContentLength();
    }

    @Override
    protected void applyCookies() {
        if (this.getCookies().isEmpty()) {
            return;
        }
        CookieStore cookieStore = this.context.getCookieStore();
        this.getCookies().values().stream().flatMap(Collection::stream).forEach(cookie -> {
            BasicClientCookie clientCookie = new BasicClientCookie(cookie.getName(), cookie.getValue());
            clientCookie.setDomain(this.getURI().getHost());
            clientCookie.setPath(this.getURI().getPath());
            cookieStore.addCookie((Cookie)clientCookie);
        });
    }

    @Override
    protected HttpHeaders initReadOnlyHeaders() {
        return HttpHeaders.readOnlyHttpHeaders(new HttpComponentsHeadersAdapter((HttpMessage)this.httpRequest));
    }

    public AsyncRequestProducer toRequestProducer() {
        ReactiveEntityProducer reactiveEntityProducer = null;
        if (this.byteBufferFlux != null) {
            String contentEncoding = this.getHeaders().getFirst("Content-Encoding");
            ContentType contentType = null;
            if (this.getHeaders().getContentType() != null) {
                contentType = ContentType.parse((CharSequence)this.getHeaders().getContentType().toString());
            }
            reactiveEntityProducer = new ReactiveEntityProducer(this.byteBufferFlux, this.contentLength, contentType, contentEncoding);
        }
        return new BasicRequestProducer(this.httpRequest, reactiveEntityProducer);
    }
}

