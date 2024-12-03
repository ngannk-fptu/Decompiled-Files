/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.ByteBufAllocator
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.EventLoop
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.channel.pool.ChannelPoolHandler
 *  io.netty.handler.ssl.SslContext
 *  io.netty.handler.ssl.SslHandler
 *  io.netty.util.AttributeKey
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 *  software.amazon.awssdk.utils.StringUtils
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.EventLoop;
import io.netty.channel.pool.ChannelPool;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import java.net.URI;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.NettyConfiguration;
import software.amazon.awssdk.http.nio.netty.internal.ProxyTunnelInitHandler;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public class Http1TunnelConnectionPool
implements ChannelPool {
    static final AttributeKey<Boolean> TUNNEL_ESTABLISHED_KEY = NettyUtils.getOrCreateAttributeKey("aws.http.nio.netty.async.Http1TunnelConnectionPool.tunnelEstablished");
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http1TunnelConnectionPool.class);
    private final EventLoop eventLoop;
    private final ChannelPool delegate;
    private final SslContext sslContext;
    private final URI proxyAddress;
    private final String proxyUser;
    private final String proxyPassword;
    private final URI remoteAddress;
    private final ChannelPoolHandler handler;
    private final InitHandlerSupplier initHandlerSupplier;
    private final NettyConfiguration nettyConfiguration;

    public Http1TunnelConnectionPool(EventLoop eventLoop, ChannelPool delegate, SslContext sslContext, URI proxyAddress, String proxyUsername, String proxyPassword, URI remoteAddress, ChannelPoolHandler handler, NettyConfiguration nettyConfiguration) {
        this(eventLoop, delegate, sslContext, proxyAddress, proxyUsername, proxyPassword, remoteAddress, handler, ProxyTunnelInitHandler::new, nettyConfiguration);
    }

    public Http1TunnelConnectionPool(EventLoop eventLoop, ChannelPool delegate, SslContext sslContext, URI proxyAddress, URI remoteAddress, ChannelPoolHandler handler, NettyConfiguration nettyConfiguration) {
        this(eventLoop, delegate, sslContext, proxyAddress, null, null, remoteAddress, handler, ProxyTunnelInitHandler::new, nettyConfiguration);
    }

    @SdkTestInternalApi
    Http1TunnelConnectionPool(EventLoop eventLoop, ChannelPool delegate, SslContext sslContext, URI proxyAddress, String proxyUser, String proxyPassword, URI remoteAddress, ChannelPoolHandler handler, InitHandlerSupplier initHandlerSupplier, NettyConfiguration nettyConfiguration) {
        this.eventLoop = eventLoop;
        this.delegate = delegate;
        this.sslContext = sslContext;
        this.proxyAddress = proxyAddress;
        this.proxyUser = proxyUser;
        this.proxyPassword = proxyPassword;
        this.remoteAddress = remoteAddress;
        this.handler = handler;
        this.initHandlerSupplier = initHandlerSupplier;
        this.nettyConfiguration = nettyConfiguration;
    }

    public Future<Channel> acquire() {
        return this.acquire((Promise<Channel>)this.eventLoop.newPromise());
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        this.delegate.acquire(this.eventLoop.newPromise()).addListener(f -> {
            if (f.isSuccess()) {
                this.setupChannel((Channel)f.getNow(), promise);
            } else {
                promise.setFailure(f.cause());
            }
        });
        return promise;
    }

    public Future<Void> release(Channel channel) {
        return this.release(channel, (Promise<Void>)this.eventLoop.newPromise());
    }

    public Future<Void> release(Channel channel, Promise<Void> promise) {
        return this.delegate.release(channel, promise);
    }

    public void close() {
        this.delegate.close();
    }

    private void setupChannel(Channel ch, Promise<Channel> acquirePromise) {
        if (Http1TunnelConnectionPool.isTunnelEstablished(ch)) {
            log.debug(ch, () -> String.format("Tunnel already established for %s", ch.id().asShortText()));
            acquirePromise.setSuccess((Object)ch);
            return;
        }
        log.debug(ch, () -> String.format("Tunnel not yet established for channel %s. Establishing tunnel now.", ch.id().asShortText()));
        Promise tunnelEstablishedPromise = this.eventLoop.newPromise();
        SslHandler sslHandler = this.createSslHandlerIfNeeded(ch.alloc());
        if (sslHandler != null) {
            ch.pipeline().addLast(new ChannelHandler[]{sslHandler});
        }
        ch.pipeline().addLast(new ChannelHandler[]{this.initHandlerSupplier.newInitHandler(this.delegate, this.proxyUser, this.proxyPassword, this.remoteAddress, (Promise<Channel>)tunnelEstablishedPromise)});
        tunnelEstablishedPromise.addListener(f -> {
            if (f.isSuccess()) {
                Channel tunnel = (Channel)f.getNow();
                this.handler.channelCreated(tunnel);
                tunnel.attr(TUNNEL_ESTABLISHED_KEY).set((Object)true);
                acquirePromise.setSuccess((Object)tunnel);
            } else {
                ch.close();
                this.delegate.release(ch);
                Throwable cause = f.cause();
                log.error(ch, () -> String.format("Unable to establish tunnel for channel %s", ch.id().asShortText()), cause);
                acquirePromise.setFailure(cause);
            }
        });
    }

    private SslHandler createSslHandlerIfNeeded(ByteBufAllocator alloc) {
        if (this.sslContext == null) {
            return null;
        }
        String scheme = this.proxyAddress.getScheme();
        if (!"https".equals(StringUtils.lowerCase((String)scheme))) {
            return null;
        }
        return NettyUtils.newSslHandler(this.sslContext, alloc, this.proxyAddress.getHost(), this.proxyAddress.getPort(), this.nettyConfiguration.tlsHandshakeTimeout());
    }

    private static boolean isTunnelEstablished(Channel ch) {
        Boolean established = (Boolean)ch.attr(TUNNEL_ESTABLISHED_KEY).get();
        return Boolean.TRUE.equals(established);
    }

    @FunctionalInterface
    @SdkTestInternalApi
    static interface InitHandlerSupplier {
        public ChannelHandler newInitHandler(ChannelPool var1, String var2, String var3, URI var4, Promise<Channel> var5);
    }
}

