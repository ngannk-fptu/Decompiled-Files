/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.ssl.SslHandler
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.FutureListener
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.ScheduledFuture
 *  io.netty.util.internal.ObjectUtil
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakeException;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolConfig;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.ssl.SslHandler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.ScheduledFuture;
import io.netty.util.internal.ObjectUtil;
import java.util.concurrent.TimeUnit;

class WebSocketServerProtocolHandshakeHandler
extends ChannelInboundHandlerAdapter {
    private final WebSocketServerProtocolConfig serverConfig;
    private ChannelHandlerContext ctx;
    private ChannelPromise handshakePromise;
    private boolean isWebSocketPath;

    WebSocketServerProtocolHandshakeHandler(WebSocketServerProtocolConfig serverConfig) {
        this.serverConfig = (WebSocketServerProtocolConfig)ObjectUtil.checkNotNull((Object)serverConfig, (String)"serverConfig");
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        this.ctx = ctx;
        this.handshakePromise = ctx.newPromise();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void channelRead(final ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpObject httpObject = (HttpObject)msg;
        if (httpObject instanceof HttpRequest) {
            final HttpRequest req = (HttpRequest)httpObject;
            this.isWebSocketPath = this.isWebSocketPath(req);
            if (!this.isWebSocketPath) {
                ctx.fireChannelRead(msg);
                return;
            }
            try {
                WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(WebSocketServerProtocolHandshakeHandler.getWebSocketLocation(ctx.pipeline(), req, this.serverConfig.websocketPath()), this.serverConfig.subprotocols(), this.serverConfig.decoderConfig());
                final WebSocketServerHandshaker handshaker = wsFactory.newHandshaker(req);
                final ChannelPromise localHandshakePromise = this.handshakePromise;
                if (handshaker == null) {
                    WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
                }
                WebSocketServerProtocolHandler.setHandshaker(ctx.channel(), handshaker);
                ctx.pipeline().remove((ChannelHandler)this);
                ChannelFuture handshakeFuture = handshaker.handshake(ctx.channel(), req);
                handshakeFuture.addListener((GenericFutureListener)new ChannelFutureListener(){

                    public void operationComplete(ChannelFuture future) {
                        if (!future.isSuccess()) {
                            localHandshakePromise.tryFailure(future.cause());
                            ctx.fireExceptionCaught(future.cause());
                        } else {
                            localHandshakePromise.trySuccess();
                            ctx.fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_COMPLETE);
                            ctx.fireUserEventTriggered((Object)new WebSocketServerProtocolHandler.HandshakeComplete(req.uri(), req.headers(), handshaker.selectedSubprotocol()));
                        }
                    }
                });
                this.applyHandshakeTimeout();
            }
            finally {
                ReferenceCountUtil.release((Object)req);
            }
        } else if (!this.isWebSocketPath) {
            ctx.fireChannelRead(msg);
        } else {
            ReferenceCountUtil.release((Object)msg);
        }
    }

    private boolean isWebSocketPath(HttpRequest req) {
        boolean checkNextUri;
        String websocketPath = this.serverConfig.websocketPath();
        String uri = req.uri();
        boolean checkStartUri = uri.startsWith(websocketPath);
        boolean bl = checkNextUri = "/".equals(websocketPath) || this.checkNextUri(uri, websocketPath);
        return this.serverConfig.checkStartsWith() ? checkStartUri && checkNextUri : uri.equals(websocketPath);
    }

    private boolean checkNextUri(String uri, String websocketPath) {
        int len = websocketPath.length();
        if (uri.length() > len) {
            char nextUri = uri.charAt(len);
            return nextUri == '/' || nextUri == '?';
        }
        return true;
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req, HttpResponse res) {
        ChannelFuture f = ctx.writeAndFlush((Object)res);
        if (!HttpUtil.isKeepAlive(req) || res.status().code() != 200) {
            f.addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
        }
    }

    private static String getWebSocketLocation(ChannelPipeline cp, HttpRequest req, String path) {
        String protocol = "ws";
        if (cp.get(SslHandler.class) != null) {
            protocol = "wss";
        }
        String host = req.headers().get((CharSequence)HttpHeaderNames.HOST);
        return protocol + "://" + host + path;
    }

    private void applyHandshakeTimeout() {
        final ChannelPromise localHandshakePromise = this.handshakePromise;
        long handshakeTimeoutMillis = this.serverConfig.handshakeTimeoutMillis();
        if (handshakeTimeoutMillis <= 0L || localHandshakePromise.isDone()) {
            return;
        }
        ScheduledFuture timeoutFuture = this.ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!localHandshakePromise.isDone() && localHandshakePromise.tryFailure((Throwable)new WebSocketServerHandshakeException("handshake timed out"))) {
                    WebSocketServerProtocolHandshakeHandler.this.ctx.flush().fireUserEventTriggered((Object)WebSocketServerProtocolHandler.ServerHandshakeStateEvent.HANDSHAKE_TIMEOUT).close();
                }
            }
        }, handshakeTimeoutMillis, TimeUnit.MILLISECONDS);
        localHandshakePromise.addListener((GenericFutureListener)new FutureListener<Void>((Future)timeoutFuture){
            final /* synthetic */ Future val$timeoutFuture;
            {
                this.val$timeoutFuture = future;
            }

            public void operationComplete(Future<Void> f) {
                this.val$timeoutFuture.cancel(false);
            }
        });
    }
}

