/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.pool.ChannelPool;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import java.io.IOException;
import java.net.URI;
import java.util.Base64;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.utils.StringUtils;

@SdkInternalApi
public final class ProxyTunnelInitHandler
extends ChannelDuplexHandler {
    public static final NettyClientLogger log = NettyClientLogger.getLogger(ProxyTunnelInitHandler.class);
    private final ChannelPool sourcePool;
    private final String username;
    private final String password;
    private final URI remoteHost;
    private final Promise<Channel> initPromise;
    private final Supplier<HttpClientCodec> httpCodecSupplier;

    public ProxyTunnelInitHandler(ChannelPool sourcePool, String proxyUsername, String proxyPassword, URI remoteHost, Promise<Channel> initPromise) {
        this(sourcePool, proxyUsername, proxyPassword, remoteHost, initPromise, HttpClientCodec::new);
    }

    public ProxyTunnelInitHandler(ChannelPool sourcePool, URI remoteHost, Promise<Channel> initPromise) {
        this(sourcePool, null, null, remoteHost, initPromise, HttpClientCodec::new);
    }

    @SdkTestInternalApi
    public ProxyTunnelInitHandler(ChannelPool sourcePool, String prosyUsername, String proxyPassword, URI remoteHost, Promise<Channel> initPromise, Supplier<HttpClientCodec> httpCodecSupplier) {
        this.sourcePool = sourcePool;
        this.remoteHost = remoteHost;
        this.initPromise = initPromise;
        this.username = prosyUsername;
        this.password = proxyPassword;
        this.httpCodecSupplier = httpCodecSupplier;
    }

    @Override
    public void handlerAdded(ChannelHandlerContext ctx) {
        ChannelPipeline pipeline = ctx.pipeline();
        pipeline.addBefore(ctx.name(), null, this.httpCodecSupplier.get());
        HttpRequest connectRequest = this.connectRequest();
        ctx.channel().writeAndFlush(connectRequest).addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)f -> {
            if (!f.isSuccess()) {
                this.handleConnectRequestFailure(ctx, f.cause());
            }
        }));
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) {
        if (ctx.pipeline().get(HttpClientCodec.class) != null) {
            ctx.pipeline().remove(HttpClientCodec.class);
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        HttpResponse response;
        if (msg instanceof HttpResponse && (response = (HttpResponse)msg).status().code() == 200) {
            ctx.pipeline().remove(this);
            this.initPromise.setSuccess(ctx.channel());
            return;
        }
        ctx.pipeline().remove(this);
        ctx.close();
        this.sourcePool.release(ctx.channel());
        this.initPromise.setFailure(new IOException("Could not connect to proxy"));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (!this.initPromise.isDone()) {
            this.handleConnectRequestFailure(ctx, null);
        } else {
            log.debug(ctx.channel(), () -> "The proxy channel (" + ctx.channel().id() + ") is inactive");
            this.closeAndRelease(ctx);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (!this.initPromise.isDone()) {
            this.handleConnectRequestFailure(ctx, cause);
        } else {
            log.debug(ctx.channel(), () -> "An exception occurred on the proxy tunnel channel (" + ctx.channel().id() + "). The channel has been closed to prevent any ongoing issues.", cause);
            this.closeAndRelease(ctx);
        }
    }

    private void handleConnectRequestFailure(ChannelHandlerContext ctx, Throwable cause) {
        this.closeAndRelease(ctx);
        String errorMsg = "Unable to send CONNECT request to proxy";
        IOException ioException = cause == null ? new IOException(errorMsg) : new IOException(errorMsg, cause);
        this.initPromise.setFailure(ioException);
    }

    private void closeAndRelease(ChannelHandlerContext ctx) {
        ctx.close();
        this.sourcePool.release(ctx.channel());
    }

    private HttpRequest connectRequest() {
        String uri = this.getUri();
        DefaultFullHttpRequest request = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.CONNECT, uri, Unpooled.EMPTY_BUFFER, false);
        request.headers().add((CharSequence)HttpHeaderNames.HOST, (Object)uri);
        if (!StringUtils.isEmpty(this.username) && !StringUtils.isEmpty(this.password)) {
            String authToken = String.format("%s:%s", this.username, this.password);
            String authB64 = Base64.getEncoder().encodeToString(authToken.getBytes(CharsetUtil.UTF_8));
            request.headers().add((CharSequence)HttpHeaderNames.PROXY_AUTHORIZATION, (Object)String.format("Basic %s", authB64));
        }
        return request;
    }

    private String getUri() {
        return this.remoteHost.getHost() + ":" + this.remoteHost.getPort();
    }
}

