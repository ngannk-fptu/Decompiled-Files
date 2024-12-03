/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.connector.ByteBufferPool
 *  io.undertow.connector.PooledByteBuffer
 *  io.undertow.server.HttpServerExchange
 *  io.undertow.server.handlers.Cookie
 *  io.undertow.util.HeaderValues
 *  org.xnio.channels.StreamSourceChannel
 *  reactor.core.publisher.Flux
 */
package org.springframework.http.server.reactive;

import io.undertow.connector.ByteBufferPool;
import io.undertow.connector.PooledByteBuffer;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.Cookie;
import io.undertow.util.HeaderValues;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.function.IntPredicate;
import javax.net.ssl.SSLSession;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.PooledDataBuffer;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractListenerReadPublisher;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.DefaultSslInfo;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.xnio.channels.StreamSourceChannel;
import reactor.core.publisher.Flux;

class UndertowServerHttpRequest
extends AbstractServerHttpRequest {
    private final HttpServerExchange exchange;
    private final RequestBodyPublisher body;

    public UndertowServerHttpRequest(HttpServerExchange exchange2, DataBufferFactory bufferFactory) throws URISyntaxException {
        super(UndertowServerHttpRequest.initUri(exchange2), "", UndertowServerHttpRequest.initHeaders(exchange2));
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

    private static HttpHeaders initHeaders(HttpServerExchange exchange2) {
        HttpHeaders headers = new HttpHeaders();
        for (HeaderValues values : exchange2.getRequestHeaders()) {
            headers.put(values.getHeaderName().toString(), (List<String>)values);
        }
        return headers;
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

    private static class UndertowDataBuffer
    implements PooledDataBuffer {
        private final DataBuffer dataBuffer;
        private final PooledByteBuffer pooledByteBuffer;

        public UndertowDataBuffer(DataBuffer dataBuffer, PooledByteBuffer pooledByteBuffer) {
            this.dataBuffer = dataBuffer;
            this.pooledByteBuffer = pooledByteBuffer;
        }

        @Override
        public PooledDataBuffer retain() {
            return this;
        }

        @Override
        public boolean release() {
            boolean result;
            try {
                result = DataBufferUtils.release(this.dataBuffer);
            }
            finally {
                this.pooledByteBuffer.close();
            }
            return result && this.pooledByteBuffer.isOpen();
        }

        @Override
        public DataBufferFactory factory() {
            return this.dataBuffer.factory();
        }

        @Override
        public int indexOf(IntPredicate predicate, int fromIndex) {
            return this.dataBuffer.indexOf(predicate, fromIndex);
        }

        @Override
        public int lastIndexOf(IntPredicate predicate, int fromIndex) {
            return this.dataBuffer.lastIndexOf(predicate, fromIndex);
        }

        @Override
        public int readableByteCount() {
            return this.dataBuffer.readableByteCount();
        }

        @Override
        public int writableByteCount() {
            return this.dataBuffer.writableByteCount();
        }

        @Override
        public int readPosition() {
            return this.dataBuffer.readPosition();
        }

        @Override
        public DataBuffer readPosition(int readPosition) {
            return this.dataBuffer.readPosition(readPosition);
        }

        @Override
        public int writePosition() {
            return this.dataBuffer.writePosition();
        }

        @Override
        public DataBuffer writePosition(int writePosition) {
            return this.dataBuffer.writePosition(writePosition);
        }

        @Override
        public int capacity() {
            return this.dataBuffer.capacity();
        }

        @Override
        public DataBuffer capacity(int newCapacity) {
            return this.dataBuffer.capacity(newCapacity);
        }

        @Override
        public byte getByte(int index) {
            return this.dataBuffer.getByte(index);
        }

        @Override
        public byte read() {
            return this.dataBuffer.read();
        }

        @Override
        public DataBuffer read(byte[] destination) {
            return this.dataBuffer.read(destination);
        }

        @Override
        public DataBuffer read(byte[] destination, int offset, int length) {
            return this.dataBuffer.read(destination, offset, length);
        }

        @Override
        public DataBuffer write(byte b) {
            return this.dataBuffer.write(b);
        }

        @Override
        public DataBuffer write(byte[] source) {
            return this.dataBuffer.write(source);
        }

        @Override
        public DataBuffer write(byte[] source, int offset, int length) {
            return this.dataBuffer.write(source, offset, length);
        }

        @Override
        public DataBuffer write(DataBuffer ... buffers) {
            return this.dataBuffer.write(buffers);
        }

        @Override
        public DataBuffer write(ByteBuffer ... byteBuffers) {
            return this.dataBuffer.write(byteBuffers);
        }

        @Override
        public DataBuffer slice(int index, int length) {
            return this.dataBuffer.slice(index, length);
        }

        @Override
        public ByteBuffer asByteBuffer() {
            return this.dataBuffer.asByteBuffer();
        }

        @Override
        public ByteBuffer asByteBuffer(int index, int length) {
            return this.dataBuffer.asByteBuffer(index, length);
        }

        @Override
        public InputStream asInputStream() {
            return this.dataBuffer.asInputStream();
        }

        @Override
        public InputStream asInputStream(boolean releaseOnClose) {
            return this.dataBuffer.asInputStream(releaseOnClose);
        }

        @Override
        public OutputStream asOutputStream() {
            return this.dataBuffer.asOutputStream();
        }
    }

    private static class RequestBodyPublisher
    extends AbstractListenerReadPublisher<DataBuffer> {
        private final StreamSourceChannel channel;
        private final DataBufferFactory bufferFactory;
        private final ByteBufferPool byteBufferPool;

        public RequestBodyPublisher(HttpServerExchange exchange2, DataBufferFactory bufferFactory) {
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
            PooledByteBuffer pooledByteBuffer = this.byteBufferPool.allocate();
            boolean release = true;
            try {
                ByteBuffer byteBuffer = pooledByteBuffer.getBuffer();
                int read = this.channel.read(byteBuffer);
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("Channel read returned " + read + (read != -1 ? " bytes" : ""));
                }
                if (read > 0) {
                    byteBuffer.flip();
                    DataBuffer dataBuffer = this.bufferFactory.wrap(byteBuffer);
                    release = false;
                    UndertowDataBuffer undertowDataBuffer = new UndertowDataBuffer(dataBuffer, pooledByteBuffer);
                    return undertowDataBuffer;
                }
                if (read == -1) {
                    this.onAllDataRead();
                }
                DataBuffer dataBuffer = null;
                return dataBuffer;
            }
            finally {
                if (release && pooledByteBuffer.isOpen()) {
                    pooledByteBuffer.close();
                }
            }
        }

        @Override
        protected void discardData() {
        }
    }
}

