/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  reactor.core.publisher.Flux
 *  reactor.ipc.netty.http.server.HttpServerRequest
 */
package org.springframework.http.server.reactive;

import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.cookie.Cookie;
import io.netty.handler.ssl.SslHandler;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;
import javax.net.ssl.SSLSession;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.AbstractServerHttpRequest;
import org.springframework.http.server.reactive.DefaultSslInfo;
import org.springframework.http.server.reactive.SslInfo;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import reactor.core.publisher.Flux;
import reactor.ipc.netty.http.server.HttpServerRequest;

class ReactorServerHttpRequest
extends AbstractServerHttpRequest {
    private final HttpServerRequest request;
    private final NettyDataBufferFactory bufferFactory;

    public ReactorServerHttpRequest(HttpServerRequest request, NettyDataBufferFactory bufferFactory) throws URISyntaxException {
        super(ReactorServerHttpRequest.initUri(request), "", ReactorServerHttpRequest.initHeaders(request));
        Assert.notNull((Object)bufferFactory, "DataBufferFactory must not be null");
        this.request = request;
        this.bufferFactory = bufferFactory;
    }

    private static URI initUri(HttpServerRequest request) throws URISyntaxException {
        Assert.notNull((Object)request, "HttpServerRequest must not be null");
        return new URI(ReactorServerHttpRequest.resolveBaseUrl(request).toString() + ReactorServerHttpRequest.resolveRequestUri(request));
    }

    private static URI resolveBaseUrl(HttpServerRequest request) throws URISyntaxException {
        String scheme = ReactorServerHttpRequest.getScheme(request);
        String header = request.requestHeaders().get(HttpHeaderNames.HOST);
        if (header != null) {
            int portIndex = header.startsWith("[") ? header.indexOf(58, header.indexOf(93)) : header.indexOf(58);
            if (portIndex != -1) {
                try {
                    return new URI(scheme, null, header.substring(0, portIndex), Integer.parseInt(header.substring(portIndex + 1)), null, null, null);
                }
                catch (NumberFormatException ex) {
                    throw new URISyntaxException(header, "Unable to parse port", portIndex);
                }
            }
            return new URI(scheme, header, null, null);
        }
        InetSocketAddress localAddress = (InetSocketAddress)request.context().channel().localAddress();
        return new URI(scheme, null, localAddress.getHostString(), localAddress.getPort(), null, null, null);
    }

    private static String getScheme(HttpServerRequest request) {
        ChannelPipeline pipeline = request.context().channel().pipeline();
        boolean ssl = pipeline.get(SslHandler.class) != null;
        return ssl ? "https" : "http";
    }

    private static String resolveRequestUri(HttpServerRequest request) {
        char c;
        String uri = request.uri();
        for (int i = 0; i < uri.length() && (c = uri.charAt(i)) != '/' && c != '?' && c != '#'; ++i) {
            if (c != ':' || i + 2 >= uri.length() || uri.charAt(i + 1) != '/' || uri.charAt(i + 2) != '/') continue;
            for (int j = i + 3; j < uri.length(); ++j) {
                c = uri.charAt(j);
                if (c != '/' && c != '?' && c != '#') continue;
                return uri.substring(j);
            }
            return "";
        }
        return uri;
    }

    private static HttpHeaders initHeaders(HttpServerRequest channel) {
        HttpHeaders headers = new HttpHeaders();
        for (String name : channel.requestHeaders().names()) {
            headers.put(name, channel.requestHeaders().getAll(name));
        }
        return headers;
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
    public InetSocketAddress getRemoteAddress() {
        return this.request.remoteAddress();
    }

    @Override
    @Nullable
    protected SslInfo initSslInfo() {
        SslHandler sslHandler = this.request.context().channel().pipeline().get(SslHandler.class);
        if (sslHandler != null) {
            SSLSession session = sslHandler.engine().getSession();
            return new DefaultSslInfo(session);
        }
        return null;
    }

    @Override
    public Flux<DataBuffer> getBody() {
        return this.request.receive().map(byteBuf -> {
            byte[] data = new byte[byteBuf.readableBytes()];
            byteBuf.readBytes(data);
            return this.bufferFactory.wrap(data);
        });
    }

    @Override
    public <T> T getNativeRequest() {
        return (T)this.request;
    }
}

