/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.connector.ByteBufferPool
 *  io.undertow.connector.PooledByteBuffer
 *  io.undertow.server.HttpServerExchange
 *  io.undertow.server.handlers.Cookie
 *  org.reactivestreams.Publisher
 *  org.xnio.channels.StreamSourceChannel
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.server.reactive;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.AbstractListenerReadPublisher;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.DefaultSslInfo;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.http.server.reactive.UndertowHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.xnio.channels.StreamSourceChannel;
import reactor.core.publisher.Flux;

class UndertowServerHttpRequest
extends AbstractServerHttpRequest {
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerExchange exchange;
    private final RequestBodyPublisher body;

    public UndertowServerHttpRequest(HttpServerExchange exchange2, DataBufferFactory bufferFactory) throws URISyntaxException {
        super(UndertowServerHttpRequest.initUri(exchange2), "", new UndertowHeadersAdapter(exchange2.getRequestHeaders()));
        this.exchange = exchange2;
        this.body = new RequestBodyPublisher(exchange2, bufferFactory);
        this.body.registerListeners(exchange2);
    }

    private static URI initUri(HttpServerExchange exchange2) throws URISyntaxException {
        Assert.notNull((Object)exchange2, "HttpServerExchange is required");
        String requestURL = exchange2.getRequestURL();
        String query = exchange2.getQueryString();
        String requestUriAndQuery = StringUtils.hasLength(query) ? requestURL + "?" + query : requestURL;
        return new URI(requestUriAndQuery);
    }

    @Override
    public String getMethodValue() {
        return this.exchange.getRequestMethod().toString();
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        LinkedMultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<String, HttpCookie>();
        for (String name : this.exchange.getRequestCookies().keySet()) {
            Cookie cookie = (Cookie)this.exchange.getRequestCookies().get(name);
            HttpCookie httpCookie = new HttpCookie(name, cookie.getValue());
            cookies.add(name, httpCookie);
        }
        return cookies;
    }

    @Override
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.exchange.getDestinationAddress();
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.exchange.getSourceAddress();
    }

    @Override
    @Nullable
    protected SslInfo initSslInfo() {
        SSLSession session = this.exchange.getConnection().getSslSession();
        if (session != null) {
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return Flux.from((Publisher)this.body);
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.exchange;
    }

    @Override
    protected String initId() {
        return ObjectUtils.getIdentityHexString(this.exchange.getConnection()) + "-" + logPrefixIndex.incrementAndGet();
    }

    private class RequestBodyPublisher
    extends AbstractListenerReadPublisher<DataBuffer> {
        private final StreamSourceChannel channel;
        private final DataBufferFactory bufferFactory;
        private final ByteBufferPool byteBufferPool;

        public RequestBodyPublisher(HttpServerExchange exchange2, DataBufferFactory bufferFactory) {
            super(UndertowServerHttpRequest.this.getLogPrefix());
            this.channel = exchange2.getRequestChannel();
            this.bufferFactory = bufferFactory;
            this.byteBufferPool = exchange2.getConnection().getByteBufferPool();
        }

        private void registerListeners(HttpServerExchange exchange2) {
            exchange2.addExchangeCompleteListener((ex, next) -> {
                this.onAllDataRead();
                next.proceed();
            });
            this.channel.getReadSetter().set(c -> this.onDataAvailable());
            this.channel.getCloseSetter().set(c -> this.onAllDataRead());
            this.channel.resumeReads();
        }

        @Override
        protected void checkOnDataAvailable() {
            this.channel.resumeReads();
            this.onDataAvailable();
        }

        @Override
        protected void readingPaused() {
            this.channel.suspendReads();
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        protected DataBuffer read() throws IOException {
            try (PooledByteBuffer pooledByteBuffer = this.byteBufferPool.allocate();){
                ByteBuffer byteBuffer = pooledByteBuffer.getBuffer();
                int read = this.channel.read(byteBuffer);
                if (rsReadLogger.isTraceEnabled()) {
                    rsReadLogger.trace((Object)(this.getLogPrefix() + "Read " + read + (read != -1 ? " bytes" : "")));
                }
                if (read > 0) {
                    byteBuffer.flip();
                    DataBuffer dataBuffer = this.bufferFactory.allocateBuffer(read);
                    dataBuffer.write(byteBuffer);
                    DataBuffer dataBuffer2 = dataBuffer;
                    return dataBuffer2;
                }
                if (read == -1) {
                    this.onAllDataRead();
                }
                DataBuffer dataBuffer = null;
                return dataBuffer;
            }
        }

        @Override
        protected void discardData() {
        }
    }
}

