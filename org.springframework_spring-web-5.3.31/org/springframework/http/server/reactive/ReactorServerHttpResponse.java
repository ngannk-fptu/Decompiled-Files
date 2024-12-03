/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.channel.ChannelId
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.reactivestreams.Publisher
 *  org.springframework.core.io.buffer.DataBuffer
 *  org.springframework.core.io.buffer.DataBufferFactory
 *  org.springframework.core.io.buffer.DataBufferUtils
 *  org.springframework.core.io.buffer.NettyDataBufferFactory
 *  org.springframework.util.Assert
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.netty.ChannelOperationsId
 *  reactor.netty.http.server.HttpServerResponse
 */
package org.springframework.http.server.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelId;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.NettyHeadersAdapter;
import org.springframework.http.server.reactive.ReactorServerHttpRequest;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.ChannelOperationsId;
import reactor.netty.http.server.HttpServerResponse;

class ReactorServerHttpResponse
extends AbstractServerHttpResponse
implements ZeroCopyHttpOutputMessage {
    private static final Log logger = LogFactory.getLog(ReactorServerHttpResponse.class);
    private final HttpServerResponse response;

    public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(bufferFactory, new HttpHeaders(new NettyHeadersAdapter(response.responseHeaders())));
        Assert.notNull((Object)response, (String)"HttpServerResponse must not be null");
        this.response = response;
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.response;
    }

    @Override
    public HttpStatus getStatusCode() {
        HttpStatus status = super.getStatusCode();
        return status != null ? status : HttpStatus.resolve(this.response.status().code());
    }

    @Override
    public Integer getRawStatusCode() {
        Integer status = super.getRawStatusCode();
        return status != null ? status.intValue() : this.response.status().code();
    }

    @Override
    protected void applyStatusCode() {
        Integer status = super.getRawStatusCode();
        if (status != null) {
            this.response.status(status.intValue());
        }
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        return this.response.send(this.toByteBufs(publisher)).then();
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
        return this.response.sendGroups((Publisher)Flux.from(publisher).map(this::toByteBufs)).then();
    }

    @Override
    protected void applyHeaders() {
    }

    @Override
    protected void applyCookies() {
        for (List cookies : this.getCookies().values()) {
            for (ResponseCookie cookie : cookies) {
                this.response.addHeader((CharSequence)"Set-Cookie", (CharSequence)cookie.toString());
            }
        }
    }

    @Override
    public Mono<Void> writeWith(Path file, long position, long count) {
        return this.doCommit(() -> this.response.sendFile(file, position, count).then());
    }

    private Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return dataBuffers instanceof Mono ? Mono.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf) : Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }

    @Override
    protected void touchDataBuffer(DataBuffer buffer) {
        if (logger.isDebugEnabled()) {
            if (ReactorServerHttpRequest.reactorNettyRequestChannelOperationsIdPresent && ChannelOperationsIdHelper.touch(buffer, this.response)) {
                return;
            }
            this.response.withConnection(connection -> {
                ChannelId id = connection.channel().id();
                DataBufferUtils.touch((DataBuffer)buffer, (Object)("Channel id: " + id.asShortText()));
            });
        }
    }

    private static class ChannelOperationsIdHelper {
        private ChannelOperationsIdHelper() {
        }

        public static boolean touch(DataBuffer dataBuffer, HttpServerResponse response) {
            if (response instanceof ChannelOperationsId) {
                String id = ((ChannelOperationsId)response).asLongText();
                DataBufferUtils.touch((DataBuffer)dataBuffer, (Object)("Channel id: " + id));
                return true;
            }
            return false;
        }
    }
}

