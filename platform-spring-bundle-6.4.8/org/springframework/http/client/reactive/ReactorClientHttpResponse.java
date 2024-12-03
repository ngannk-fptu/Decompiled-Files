/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBuf
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.handler.codec.http.cookie.Cookie
 *  io.netty.handler.codec.http.cookie.DefaultCookie
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  reactor.core.publisher.Flux
 *  reactor.netty.ChannelOperationsId
 *  reactor.netty.Connection
 *  reactor.netty.NettyInbound
 *  reactor.netty.http.client.HttpClientResponse
 */
package org.springframework.http.client.reactive;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.codec.http.cookie.DefaultCookie;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.client.reactive.ClientHttpResponse;
import org.springframework.http.client.reactive.NettyHeadersAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ObjectUtils;
import reactor.core.publisher.Flux;
import reactor.netty.ChannelOperationsId;
import reactor.netty.Connection;
import reactor.netty.NettyInbound;
import reactor.netty.http.client.HttpClientResponse;

class ReactorClientHttpResponse
implements ClientHttpResponse {
    static final boolean reactorNettyRequestChannelOperationsIdPresent = ClassUtils.isPresent("reactor.netty.ChannelOperationsId", ReactorClientHttpResponse.class.getClassLoader());
    private static final Log logger = LogFactory.getLog(ReactorClientHttpResponse.class);
    private final HttpClientResponse response;
    private final HttpHeaders headers;
    private final NettyInbound inbound;
    private final NettyDataBufferFactory bufferFactory;
    private final AtomicInteger state = new AtomicInteger();

    public ReactorClientHttpResponse(HttpClientResponse response, Connection connection) {
        this.response = response;
        NettyHeadersAdapter adapter = new NettyHeadersAdapter(response.responseHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        this.inbound = connection.inbound();
        this.bufferFactory = new NettyDataBufferFactory(connection.outbound().alloc());
    }

    @Deprecated
    public ReactorClientHttpResponse(HttpClientResponse response, NettyInbound inbound, ByteBufAllocator alloc) {
        this.response = response;
        NettyHeadersAdapter adapter = new NettyHeadersAdapter(response.responseHeaders());
        this.headers = HttpHeaders.readOnlyHttpHeaders(adapter);
        this.inbound = inbound;
        this.bufferFactory = new NettyDataBufferFactory(alloc);
    }

    @Override
    public String getId() {
        String id = null;
        if (reactorNettyRequestChannelOperationsIdPresent) {
            id = ChannelOperationsIdHelper.getId(this.response);
        }
        if (id == null && this.response instanceof Connection) {
            id = ((Connection)this.response).channel().id().asShortText();
        }
        return id != null ? id : ObjectUtils.getIdentityHexString(this);
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.inbound.receive().doOnSubscribe(s -> {
            if (this.state.compareAndSet(0, 1)) {
                return;
            }
            if (this.state.get() == 2) {
                throw new IllegalStateException("The client response body has been released already due to cancellation.");
            }
        }).map(byteBuf -> {
            byteBuf.retain();
            return this.bufferFactory.wrap((ByteBuf)byteBuf);
        });
    }

    @Override
    public HttpHeaders getHeaders() {
        return this.headers;
    }

    @Override
    public HttpStatus getStatusCode() {
        return HttpStatus.valueOf(this.getRawStatusCode());
    }

    @Override
    public int getRawStatusCode() {
        return this.response.status().code();
    }

    @Override
    public MultiValueMap<String, ResponseCookie> getCookies() {
        LinkedMultiValueMap result = new LinkedMultiValueMap();
        this.response.cookies().values().stream().flatMap(Collection::stream).forEach(cookie -> result.add(cookie.name(), ResponseCookie.fromClientResponse(cookie.name(), cookie.value()).domain(cookie.domain()).path(cookie.path()).maxAge(cookie.maxAge()).secure(cookie.isSecure()).httpOnly(cookie.isHttpOnly()).sameSite(ReactorClientHttpResponse.getSameSite(cookie)).build()));
        return CollectionUtils.unmodifiableMultiValueMap(result);
    }

    @Nullable
    private static String getSameSite(Cookie cookie) {
        DefaultCookie defaultCookie;
        if (cookie instanceof DefaultCookie && (defaultCookie = (DefaultCookie)cookie).sameSite() != null) {
            return defaultCookie.sameSite().name();
        }
        return null;
    }

    void releaseAfterCancel(HttpMethod method) {
        if (this.mayHaveBody(method) && this.state.compareAndSet(0, 2)) {
            if (logger.isDebugEnabled()) {
                logger.debug((Object)("[" + this.getId() + "]Releasing body, not yet subscribed."));
            }
            this.inbound.receive().doOnNext(byteBuf -> {}).subscribe(byteBuf -> {}, ex -> {});
        }
    }

    private boolean mayHaveBody(HttpMethod method) {
        int code = this.getRawStatusCode();
        return (code < 100 || code >= 200) && code != 204 && code != 205 && !method.equals((Object)HttpMethod.HEAD) && this.getHeaders().getContentLength() != 0L;
    }

    public String toString() {
        return "ReactorClientHttpResponse{request=[" + this.response.method().name() + " " + this.response.uri() + "],status=" + this.getRawStatusCode() + '}';
    }

    private static class ChannelOperationsIdHelper {
        private ChannelOperationsIdHelper() {
        }

        @Nullable
        public static String getId(HttpClientResponse response) {
            if (response instanceof ChannelOperationsId) {
                return logger.isDebugEnabled() ? ((ChannelOperationsId)response).asLongText() : ((ChannelOperationsId)response).asShortText();
            }
            return null;
        }
    }
}

