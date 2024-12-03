/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.bootstrap.Bootstrap
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelInitializer
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.EventLoopGroup
 *  io.netty.channel.nio.NioEventLoopGroup
 *  io.netty.channel.socket.SocketChannel
 *  io.netty.channel.socket.SocketChannelConfig
 *  io.netty.channel.socket.nio.NioSocketChannel
 *  io.netty.handler.codec.http.HttpClientCodec
 *  io.netty.handler.codec.http.HttpObjectAggregator
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslContextBuilder
 *  io.netty.handler.timeout.ReadTimeoutHandler
 */
package org.springframework.http.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.SocketChannelConfig;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.timeout.ReadTimeoutHandler;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import javax.net.ssl.SSLException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.AsyncClientHttpRequest;
import org.springframework.http.client.AsyncClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.Netty4ClientHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

@Deprecated
public class Netty4ClientHttpRequestFactory
implements ClientHttpRequestFactory,
AsyncClientHttpRequestFactory,
InitializingBean,
DisposableBean {
    public static final int DEFAULT_MAX_RESPONSE_SIZE = 0xA00000;
    private final EventLoopGroup eventLoopGroup;
    private final boolean defaultEventLoopGroup;
    private int maxResponseSize = 0xA00000;
    @Nullable
    private SslContext sslContext;
    private int connectTimeout = -1;
    private int readTimeout = -1;
    @Nullable
    private volatile Bootstrap bootstrap;

    public Netty4ClientHttpRequestFactory() {
        int ioWorkerCount = Runtime.getRuntime().availableProcessors() * 2;
        this.eventLoopGroup = new NioEventLoopGroup(ioWorkerCount);
        this.defaultEventLoopGroup = true;
    }

    public Netty4ClientHttpRequestFactory(EventLoopGroup eventLoopGroup) {
        Assert.notNull((Object)eventLoopGroup, "EventLoopGroup must not be null");
        this.eventLoopGroup = eventLoopGroup;
        this.defaultEventLoopGroup = false;
    }

    public void setMaxResponseSize(int maxResponseSize) {
        this.maxResponseSize = maxResponseSize;
    }

    public void setSslContext(SslContext sslContext) {
        this.sslContext = sslContext;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.sslContext == null) {
            this.sslContext = this.getDefaultClientSslContext();
        }
    }

    private SslContext getDefaultClientSslContext() {
        try {
            return SslContextBuilder.forClient().build();
        }
        catch (SSLException ex) {
            throw new IllegalStateException("Could not create default client SslContext", ex);
        }
    }

    @Override
    public ClientHttpRequest createRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return this.createRequestInternal(uri, httpMethod);
    }

    @Override
    public AsyncClientHttpRequest createAsyncRequest(URI uri, HttpMethod httpMethod) throws IOException {
        return this.createRequestInternal(uri, httpMethod);
    }

    private Netty4ClientHttpRequest createRequestInternal(URI uri, HttpMethod httpMethod) {
        return new Netty4ClientHttpRequest(this.getBootstrap(uri), uri, httpMethod);
    }

    private Bootstrap getBootstrap(URI uri) {
        boolean isSecure;
        boolean bl = isSecure = uri.getPort() == 443 || "https".equalsIgnoreCase(uri.getScheme());
        if (isSecure) {
            return this.buildBootstrap(uri, true);
        }
        Bootstrap bootstrap = this.bootstrap;
        if (bootstrap == null) {
            this.bootstrap = bootstrap = this.buildBootstrap(uri, false);
        }
        return bootstrap;
    }

    private Bootstrap buildBootstrap(final URI uri, final boolean isSecure) {
        Bootstrap bootstrap = new Bootstrap();
        ((Bootstrap)((Bootstrap)bootstrap.group(this.eventLoopGroup)).channel(NioSocketChannel.class)).handler((ChannelHandler)new ChannelInitializer<SocketChannel>(){

            protected void initChannel(SocketChannel channel) throws Exception {
                Netty4ClientHttpRequestFactory.this.configureChannel(channel.config());
                ChannelPipeline pipeline = channel.pipeline();
                if (isSecure) {
                    Assert.notNull((Object)Netty4ClientHttpRequestFactory.this.sslContext, "sslContext should not be null");
                    pipeline.addLast(new ChannelHandler[]{Netty4ClientHttpRequestFactory.this.sslContext.newHandler(channel.alloc(), uri.getHost(), uri.getPort())});
                }
                pipeline.addLast(new ChannelHandler[]{new HttpClientCodec()});
                pipeline.addLast(new ChannelHandler[]{new HttpObjectAggregator(Netty4ClientHttpRequestFactory.this.maxResponseSize)});
                if (Netty4ClientHttpRequestFactory.this.readTimeout > 0) {
                    pipeline.addLast(new ChannelHandler[]{new ReadTimeoutHandler((long)Netty4ClientHttpRequestFactory.this.readTimeout, TimeUnit.MILLISECONDS)});
                }
            }
        });
        return bootstrap;
    }

    protected void configureChannel(SocketChannelConfig config) {
        if (this.connectTimeout >= 0) {
            config.setConnectTimeoutMillis(this.connectTimeout);
        }
    }

    @Override
    public void destroy() throws InterruptedException {
        if (this.defaultEventLoopGroup) {
            this.eventLoopGroup.shutdownGracefully().sync();
        }
    }
}

