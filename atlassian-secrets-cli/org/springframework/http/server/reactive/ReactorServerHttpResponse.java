/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  reactor.ipc.netty.http.server.HttpServerResponse
 */
package org.springframework.http.server.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.io.File;
import java.util.List;
import org.reactivestreams.Publisher;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ZeroCopyHttpOutputMessage;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.util.Assert;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.ipc.netty.http.server.HttpServerResponse;

class ReactorServerHttpResponse
extends AbstractServerHttpResponse
implements ZeroCopyHttpOutputMessage {
    private final HttpServerResponse response;

    public ReactorServerHttpResponse(HttpServerResponse response, DataBufferFactory bufferFactory) {
        super(bufferFactory);
        Assert.notNull((Object)response, "HttpServerResponse must not be null");
        this.response = response;
    }

    @Override
    public <T> T getNativeResponse() {
        return (T)this.response;
    }

    @Override
    protected void applyStatusCode() {
        Integer statusCode = this.getStatusCodeValue();
        if (statusCode != null) {
            this.response.status(HttpResponseStatus.valueOf(statusCode));
        }
    }

    @Override
    protected Mono<Void> writeWithInternal(Publisher<? extends DataBuffer> publisher) {
        Publisher<ByteBuf> body = ReactorServerHttpResponse.toByteBufs(publisher);
        return this.response.send(body).then();
    }

    @Override
    protected Mono<Void> writeAndFlushWithInternal(Publisher<? extends Publisher<? extends DataBuffer>> publisher) {
        Flux body = Flux.from(publisher).map(ReactorServerHttpResponse::toByteBufs);
        return this.response.sendGroups((Publisher)body).then();
    }

    @Override
    protected void applyHeaders() {
        this.getHeaders().forEach((headerName, headerValues) -> {
            for (String value : headerValues) {
                this.response.responseHeaders().add((String)headerName, (Object)value);
            }
        });
    }

    @Override
    protected void applyCookies() {
        for (String name : this.getCookies().keySet()) {
            for (ResponseCookie httpCookie : (List)this.getCookies().get(name)) {
                DefaultCookie cookie = new DefaultCookie(name, httpCookie.getValue());
                if (!httpCookie.getMaxAge().isNegative()) {
                    cookie.setMaxAge(httpCookie.getMaxAge().getSeconds());
                }
                if (httpCookie.getDomain() != null) {
                    cookie.setDomain(httpCookie.getDomain());
                }
                if (httpCookie.getPath() != null) {
                    cookie.setPath(httpCookie.getPath());
                }
                cookie.setSecure(httpCookie.isSecure());
                cookie.setHttpOnly(httpCookie.isHttpOnly());
                this.response.addCookie((Cookie)cookie);
            }
        }
    }

    @Override
    public Mono<Void> writeWith(File file, long position, long count) {
        return this.doCommit(() -> this.response.sendFile(file.toPath(), position, count).then());
    }

    private static Publisher<ByteBuf> toByteBufs(Publisher<? extends DataBuffer> dataBuffers) {
        return Flux.from(dataBuffers).map(NettyDataBufferFactory::toByteBuf);
    }
}

