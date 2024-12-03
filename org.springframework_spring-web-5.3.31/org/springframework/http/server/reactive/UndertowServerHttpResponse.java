/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.server.HttpServerExchange
 *  io.undertow.server.handlers.CookieImpl
 *  org.reactivestreams.Processor
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.xnio.channels.StreamSinkChannel
 *  reactor.core.publisher.Mono
 *  reactor.core.publisher.MonoSink
 */
package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractListenerServerHttpResponse;
import org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor;
import org.springframework.http.server.reactive.AbstractListenerWriteProcessor;
import org.springframework.http.server.reactive.UndertowHeadersAdapter;
import org.springframework.http.server.reactive.UndertowServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.xnio.channels.StreamSinkChannel;
import reactor.core.publisher.Mono;
import reactor.core.publisher.MonoSink;

class UndertowServerHttpResponse
extends AbstractListenerServerHttpResponse
implements ZeroCopyHttpOutputMessage {
    private final HttpServerExchange exchange;
    private final UndertowServerHttpRequest request;
    @Nullable
    private StreamSinkChannel responseChannel;

    UndertowServerHttpResponse(HttpServerExchange exchange2, DataBufferFactory bufferFactory, UndertowServerHttpRequest request) {
        super(bufferFactory, UndertowServerHttpResponse.createHeaders(exchange2));
        this.exchange = exchange2;
        this.request = request;
    }

    private static HttpHeaders createHeaders(HttpServerExchange exchange2) {
        Assert.notNull((Object)exchange2, (String)"HttpServerExchange must not be null");
        UndertowHeadersAdapter headersMap = new UndertowHeadersAdapter(exchange2.getResponseHeaders());
        return new HttpHeaders(headersMap);
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.exchange;
    }

    @Override
    public HttpStatus getStatusCode() {
        HttpStatus status = super.getStatusCode();
        return status != null ? status : HttpStatus.resolve(this.exchange.getStatusCode());
    }

    @Override
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return status != null ? status.intValue() : this.exchange.getStatusCode();
    }

    @Override
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.exchange.setStatusCode(status.intValue());
        }
    }

    @Override
    protected void applyHeaders() {
    }

    @Override
    protected void applyCookies() {
        for (String name : this.getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List)this.getCookies().get((Object)name)) {
                CookieImpl cookie = new CookieImpl(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge(Integer.valueOf((int)httpCookie.getMaxAge().getSeconds()));
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                cookie.setSameSiteMode(httpCookie.getSameSite());
                this.exchange.getResponseCookies().putIfAbsent(name, cookie);
            }
        }
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        return this.doCommit(() -> Mono.create(sink -> {
            try {
                FileChannel source = FileChannel.open(file, StandardOpenOption.READ);
                TransferBodyListener listener = new TransferBodyListener(source, position, count, (MonoSink<Void>)sink);
                sink.onDispose(listener::closeSource);
                StreamSinkChannel destination = this.exchange.getResponseChannel();
                destination.getWriteSetter().set(listener::transfer);
                listener.transfer(destination);
            }
            catch (IOException ex) {
                sink.error((Throwable)ex);
            }
        }));
    }

    @Override
    protected Processor<? super Publisher<? extends DataBuffer>, Void> createBodyFlushProcessor() {
        return new ResponseBodyFlushProcessor();
    }

    private ResponseBodyProcessor createBodyProcessor() {
        if (this.responseChannel == null) {
            this.responseChannel = this.exchange.getResponseChannel();
        }
        return new ResponseBodyProcessor(this.responseChannel);
    }

    private static class TransferBodyListener {
        private final FileChannel source;
        private final MonoSink<Void> sink;
        private long position;
        private long count;

        public TransferBodyListener(FileChannel source, long position, long count, MonoSink<Void> sink) {
            this.source = source;
            this.sink = sink;
            this.position = position;
            this.count = count;
        }

        public void transfer(StreamSinkChannel destination) {
            try {
                while (this.count > 0L) {
                    long len = destination.transferFrom(this.source, this.position, this.count);
                    if (len != 0L) {
                        this.position += len;
                        this.count -= len;
                        continue;
                    }
                    destination.resumeWrites();
                    return;
                }
                this.sink.success();
            }
            catch (IOException ex) {
                this.sink.error((Throwable)ex);
            }
        }

        public void closeSource() {
            try {
                this.source.close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
    }

    private class ResponseBodyFlushProcessor
    extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        public ResponseBodyFlushProcessor() {
            super(UndertowServerHttpResponse.this.request.getLogPrefix());
        }

        @Override
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            return UndertowServerHttpResponse.this.createBodyProcessor();
        }

        @Override
        protected void flush() throws IOException {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                if (rsWriteFlushLogger.isTraceEnabled()) {
                    rsWriteFlushLogger.trace((Object)(this.getLogPrefix() + "flush"));
                }
                channel.flush();
            }
        }

        @Override
        protected boolean isWritePossible() {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                channel.resumeWrites();
                return true;
            }
            return false;
        }

        @Override
        protected boolean isFlushPending() {
            return false;
        }
    }

    private class ResponseBodyProcessor
    extends AbstractListenerWriteProcessor<DataBuffer> {
        private final StreamSinkChannel channel;
        @Nullable
        private volatile ByteBuffer byteBuffer;
        private volatile boolean writePossible;

        public ResponseBodyProcessor(StreamSinkChannel channel) {
            super(UndertowServerHttpResponse.this.request.getLogPrefix());
            Assert.notNull((Object)channel, (String)"StreamSinkChannel must not be null");
            this.channel = channel;
            this.channel.getWriteSetter().set(c -> {
                this.writePossible = true;
                this.onWritePossible();
            });
            this.channel.suspendWrites();
        }

        @Override
        protected boolean isWritePossible() {
            this.channel.resumeWrites();
            return this.writePossible;
        }

        @Override
        protected boolean write(DataBuffer dataBuffer) throws IOException {
            ByteBuffer buffer = this.byteBuffer;
            if (buffer == null) {
                return false;
            }
            this.writePossible = false;
            int total = buffer.remaining();
            int written = this.writeByteBuffer(buffer);
            if (rsWriteLogger.isTraceEnabled()) {
                rsWriteLogger.trace((Object)(this.getLogPrefix() + "Wrote " + written + " of " + total + " bytes"));
            }
            if (written != total) {
                return false;
            }
            this.writePossible = true;
            DataBufferUtils.release((DataBuffer)dataBuffer);
            this.byteBuffer = null;
            return true;
        }

        private int writeByteBuffer(ByteBuffer byteBuffer) throws IOException {
            int written;
            int totalWritten = 0;
            do {
                written = this.channel.write(byteBuffer);
                totalWritten += written;
            } while (byteBuffer.hasRemaining() && written > 0);
            return totalWritten;
        }

        @Override
        protected void dataReceived(DataBuffer dataBuffer) {
            super.dataReceived(dataBuffer);
            this.byteBuffer = dataBuffer.asByteBuffer();
        }

        @Override
        protected boolean isDataEmpty(DataBuffer dataBuffer) {
            return dataBuffer.readableByteCount() == 0;
        }

        @Override
        protected void writingComplete() {
            this.channel.getWriteSetter().set(null);
            this.channel.resumeWrites();
        }

        @Override
        protected void writingFailed(Throwable ex) {
            this.cancel();
            this.onError(ex);
        }

        @Override
        protected void discardData(DataBuffer dataBuffer) {
            DataBufferUtils.release((DataBuffer)dataBuffer);
        }
    }
}

