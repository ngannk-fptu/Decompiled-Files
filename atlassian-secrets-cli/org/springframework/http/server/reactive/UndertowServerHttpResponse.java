/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.undertow.server.HttpServerExchange
 *  io.undertow.server.handlers.CookieImpl
 *  io.undertow.util.HttpString
 *  org.xnio.channels.Channels
 *  org.xnio.channels.StreamSinkChannel
 *  reactor.core.publisher.Mono
 */
package org.springframework.http.server.reactive;

import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.util.HttpString;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;
import java.util.Collection;
import java.util.List;
import org.reactivestreams.Processor;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractListenerServerHttpResponse;
import org.springframework.http.server.reactive.AbstractListenerWriteFlushProcessor;
import org.springframework.http.server.reactive.AbstractListenerWriteProcessor;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.xnio.channels.Channels;
import org.xnio.channels.StreamSinkChannel;
import reactor.core.publisher.Mono;

class UndertowServerHttpResponse
extends AbstractListenerServerHttpResponse
implements ZeroCopyHttpOutputMessage {
    private final HttpServerExchange exchange;
    @Nullable
    private StreamSinkChannel responseChannel;

    public UndertowServerHttpResponse(HttpServerExchange exchange2, DataBufferFactory bufferFactory) {
        super(bufferFactory);
        Assert.notNull((Object)exchange2, "HttpServerExchange must not be null");
        this.exchange = exchange2;
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.exchange;
    }

    @Override
    protected void applyStatusCode() {
        Integer statusCode = this.getStatusCodeValue();
        if (statusCode != null) {
            this.exchange.setStatusCode(statusCode.intValue());
        }
    }

    @Override
    protected void applyHeaders() {
        this.getHeaders().forEach((headerName, headerValues) -> this.exchange.getResponseHeaders().addAll(HttpString.tryFromString((String)headerName), (Collection)headerValues));
    }

    @Override
    protected void applyCookies() {
        for (String name : this.getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List)this.getCookies().get(name)) {
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
                this.exchange.getResponseCookies().putIfAbsent(name, cookie);
            }
        }
    }

    @Override
    public Mono<Void> writeWith(File file, long position, long count) {
        return this.doCommit(() -> Mono.defer(() -> {
            try (FileChannel source = FileChannel.open(file.toPath(), StandardOpenOption.READ);){
                StreamSinkChannel destination = this.exchange.getResponseChannel();
                Channels.transferBlocking((StreamSinkChannel)destination, (FileChannel)source, (long)position, (long)count);
                Mono mono = Mono.empty();
                return mono;
            }
            catch (IOException ex) {
                return Mono.error((Throwable)ex);
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

    private class ResponseBodyFlushProcessor
    extends AbstractListenerWriteFlushProcessor<DataBuffer> {
        private ResponseBodyFlushProcessor() {
        }

        @Override
        protected Processor<? super DataBuffer, Void> createWriteProcessor() {
            return UndertowServerHttpResponse.this.createBodyProcessor();
        }

        @Override
        protected void flush() throws IOException {
            StreamSinkChannel channel = UndertowServerHttpResponse.this.responseChannel;
            if (channel != null) {
                if (this.logger.isTraceEnabled()) {
                    this.logger.trace("flush");
                }
                channel.flush();
            }
        }

        @Override
        protected void flushingFailed(Throwable t) {
            this.cancel();
            this.onError(t);
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
            Assert.notNull((Object)channel, "StreamSinkChannel must not be null");
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
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("write: " + dataBuffer);
            }
            this.writePossible = false;
            int total = buffer.remaining();
            int written = this.writeByteBuffer(buffer);
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("written: " + written + " total: " + total);
            }
            if (written != total) {
                return false;
            }
            this.writePossible = true;
            if (this.logger.isTraceEnabled()) {
                this.logger.trace("releaseData: " + dataBuffer);
            }
            DataBufferUtils.release(dataBuffer);
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
            DataBufferUtils.release(dataBuffer);
        }
    }
}

