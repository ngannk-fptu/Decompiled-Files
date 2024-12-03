/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.handler.codec.http.cookie.Cookie
 *  io.netty.handler.ssl.SslHandler
 *  org.apache.commons.logging.Log
 *  reactor.core.publisher.Flux
 *  reactor.netty.ChannelOperationsId
 *  reactor.netty.Connection
 *  reactor.netty.http.server.HttpServerRequest
 */
package org.springframework.http.server.reactive;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.net.ssl.SSLSession;
import org.apache.commons.logging.Log;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpLogging;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.DefaultSslInfo;
import org.springframework.http.server.reactive.NettyHeadersAdapter;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.netty.ChannelOperationsId;
import reactor.netty.Connection;
import reactor.netty.http.server.HttpServerRequest;

class ReactorServerHttpRequest
extends AbstractServerHttpRequest {
    static final boolean reactorNettyRequestChannelOperationsIdPresent = ClassUtils.isPresent("reactor.netty.ChannelOperationsId", ReactorServerHttpRequest.class.getClassLoader());
    private static final Log logger = HttpLogging.forLogName(ReactorServerHttpRequest.class);
    private static final AtomicLong logPrefixIndex = new AtomicLong();
    private final HttpServerRequest request;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
        super(ReactorServerHttpRequest.initUri(request), "", new NettyHeadersAdapter(request.requestHeaders()));
        Assert.notNull((Object)bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull((Object)request, "HttpServerRequest must not be null");
        return new URI(ReactorServerHttpRequest.resolveBaseUrl(request) + ReactorServerHttpRequest.resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        int port;
        String scheme = request.scheme();
        return ReactorServerHttpRequest.usePort(scheme, port = request.hostPort()) ? new URI(scheme, null, request.hostName(), port, null, null, null) : new URI(scheme, request.hostName(), null, null);
    }

    private static boolean usePort(String scheme, int port) {
        return (scheme.equals("http") || scheme.equals("ws")) && port != 80 || (scheme.equals("https") || scheme.equals("wss")) && port != 443;
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        char c;
        String uri = request.uri();
        for (int i2 = 0; i2 < uri.length() && (c = uri.charAt(i2)) != '/' && c != '?' && c != '#'; ++i2) {
            if (c != ':' || i2 + 2 >= uri.length() || uri.charAt(i2 + 1) != '/' || uri.charAt(i2 + 2) != '/') continue;
            for (int j = i2 + 3; j < uri.length(); ++j) {
                c = uri.charAt(j);
                if (c != '/' && c != '?' && c != '#') continue;
                return uri.substring(j);
            }
            return "";
        }
        return uri;
    }

    @Override
    public String getMethodValue() {
        return this.request.method().name();
    }

    @Override
    protected MultiValueMap<String, HttpCookie> initCookies() {
        LinkedMultiValueMap<String, HttpCookie> cookies = new LinkedMultiValueMap<String, HttpCookie>();
        for (CharSequence name : this.request.cookies().keySet()) {
            for (Cookie cookie : (Set)this.request.cookies().get(name)) {
                HttpCookie httpCookie = new HttpCookie(name.toString(), cookie.value());
                cookies.add(name.toString(), httpCookie);
            }
        }
        return cookies;
    }

    @Override
    @Nullable
    public InetSocketAddress getLocalAddress() {
        return this.request.hostAddress();
    }

    @Override
    @Nullable
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override
    @Nullable
    protected SslInfo initSslInfo() {
        Channel channel = ((Connection)this.request).channel();
        SslHandler sslHandler = (SslHandler)channel.pipeline().get(SslHandler.class);
        if (sslHandler == null && channel.parent() != null) {
            sslHandler = (SslHandler)channel.parent().pipeline().get(SslHandler.class);
        }
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.request.receive().retain().map(this.bufferFactory::wrap);
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.request;
    }

    @Override
    @Nullable
    protected String initId() {
        if (this.request instanceof Connection) {
            return ((Connection)this.request).channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return null;
    }

    @Override
    protected String initLogPrefix() {
        String id;
        if (reactorNettyRequestChannelOperationsIdPresent && (id = ChannelOperationsIdHelper.getId(this.request)) != null) {
            return id;
        }
        if (this.request instanceof Connection) {
            return ((Connection)this.request).channel().id().asShortText() + "-" + logPrefixIndex.incrementAndGet();
        }
        return this.getId();
    }

    private static class ChannelOperationsIdHelper {
        private ChannelOperationsIdHelper() {
        }

        @Nullable
        public static String getId(HttpServerRequest request) {
            if (request instanceof ChannelOperationsId) {
                return logger.isDebugEnabled() ? ((ChannelOperationsId)request).asLongText() : ((ChannelOperationsId)request).asShortText();
            }
            return null;
        }
    }
}

