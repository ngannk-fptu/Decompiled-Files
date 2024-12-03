/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelOutboundHandler
 *  io.netty.channel.ChannelPromise
 *  io.netty.handler.codec.MessageToMessageDecoder
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.concurrent.Promise
 *  io.netty.util.concurrent.PromiseNotifier
 *  io.netty.util.concurrent.ScheduledFuture
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandler;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketCloseStatus;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketHandshakeException;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.concurrent.ScheduledFuture;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.List;
import java.util.concurrent.TimeUnit;

abstract class WebSocketProtocolHandler
extends MessageToMessageDecoder<WebSocketFrame>
implements ChannelOutboundHandler {
    private final boolean dropPongFrames;
    private final WebSocketCloseStatus closeStatus;
    private final long forceCloseTimeoutMillis;
    private ChannelPromise closeSent;

    WebSocketProtocolHandler() {
        this(true);
    }

    WebSocketProtocolHandler(boolean dropPongFrames) {
        this(dropPongFrames, null, 0L);
    }

    WebSocketProtocolHandler(boolean dropPongFrames, WebSocketCloseStatus closeStatus, long forceCloseTimeoutMillis) {
        this.dropPongFrames = dropPongFrames;
        this.closeStatus = closeStatus;
        this.forceCloseTimeoutMillis = forceCloseTimeoutMillis;
    }

    protected void decode(ChannelHandlerContext ctx, WebSocketFrame frame, List<Object> out) throws Exception {
        if (frame instanceof PingWebSocketFrame) {
            frame.content().retain();
            ctx.writeAndFlush((Object)new PongWebSocketFrame(frame.content()));
            WebSocketProtocolHandler.readIfNeeded(ctx);
            return;
        }
        if (frame instanceof PongWebSocketFrame && this.dropPongFrames) {
            WebSocketProtocolHandler.readIfNeeded(ctx);
            return;
        }
        out.add((Object)frame.retain());
    }

    private static void readIfNeeded(ChannelHandlerContext ctx) {
        if (!ctx.channel().config().isAutoRead()) {
            ctx.read();
        }
    }

    public void close(final ChannelHandlerContext ctx, final ChannelPromise promise) throws Exception {
        if (this.closeStatus == null || !ctx.channel().isActive()) {
            ctx.close(promise);
        } else {
            if (this.closeSent == null) {
                this.write(ctx, (Object)new CloseWebSocketFrame(this.closeStatus), ctx.newPromise());
            }
            this.flush(ctx);
            this.applyCloseSentTimeout(ctx);
            this.closeSent.addListener((GenericFutureListener)new ChannelFutureListener(){

                public void operationComplete(ChannelFuture future) {
                    ctx.close(promise);
                }
            });
        }
    }

    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (this.closeSent != null) {
            ReferenceCountUtil.release((Object)msg);
            promise.setFailure((Throwable)new ClosedChannelException());
        } else if (msg instanceof CloseWebSocketFrame) {
            this.closeSent(promise.unvoid());
            ctx.write(msg).addListener((GenericFutureListener)new PromiseNotifier(false, new Promise[]{this.closeSent}));
        } else {
            ctx.write(msg, promise);
        }
    }

    void closeSent(ChannelPromise promise) {
        this.closeSent = promise;
    }

    private void applyCloseSentTimeout(ChannelHandlerContext ctx) {
        if (this.closeSent.isDone() || this.forceCloseTimeoutMillis < 0L) {
            return;
        }
        ScheduledFuture timeoutTask = ctx.executor().schedule(new Runnable(){

            @Override
            public void run() {
                if (!WebSocketProtocolHandler.this.closeSent.isDone()) {
                    WebSocketProtocolHandler.this.closeSent.tryFailure((Throwable)WebSocketProtocolHandler.this.buildHandshakeException("send close frame timed out"));
                }
            }
        }, this.forceCloseTimeoutMillis, TimeUnit.MILLISECONDS);
        this.closeSent.addListener((GenericFutureListener)new ChannelFutureListener((Future)timeoutTask){
            final /* synthetic */ Future val$timeoutTask;
            {
                this.val$timeoutTask = future;
            }

            public void operationComplete(ChannelFuture future) {
                this.val$timeoutTask.cancel(false);
            }
        });
    }

    protected WebSocketHandshakeException buildHandshakeException(String message) {
        return new WebSocketHandshakeException(message);
    }

    public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.bind(localAddress, promise);
    }

    public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) throws Exception {
        ctx.connect(remoteAddress, localAddress, promise);
    }

    public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.disconnect(promise);
    }

    public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) throws Exception {
        ctx.deregister(promise);
    }

    public void read(ChannelHandlerContext ctx) throws Exception {
        ctx.read();
    }

    public void flush(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.fireExceptionCaught(cause);
        ctx.close();
    }
}

