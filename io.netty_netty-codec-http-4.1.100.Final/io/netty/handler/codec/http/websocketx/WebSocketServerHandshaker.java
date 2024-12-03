/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.Channel
 *  io.netty.channel.ChannelFuture
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandler
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.channel.ChannelOutboundInvoker
 *  io.netty.channel.ChannelPipeline
 *  io.netty.channel.ChannelPromise
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 *  io.netty.util.internal.EmptyArrays
 *  io.netty.util.internal.ObjectUtil
 *  io.netty.util.internal.logging.InternalLogger
 *  io.netty.util.internal.logging.InternalLoggerFactory
 */
package io.netty.handler.codec.http.websocketx;

import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelOutboundInvoker;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.EmptyHttpHeaders;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketDecoderConfig;
import io.netty.handler.codec.http.websocketx.WebSocketFrameDecoder;
import io.netty.handler.codec.http.websocketx.WebSocketFrameEncoder;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.nio.channels.ClosedChannelException;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WebSocketServerHandshaker {
    protected static final InternalLogger logger = InternalLoggerFactory.getInstance(WebSocketServerHandshaker.class);
    private final String uri;
    private final String[] subprotocols;
    private final WebSocketVersion version;
    private final WebSocketDecoderConfig decoderConfig;
    private String selectedSubprotocol;
    public static final String SUB_PROTOCOL_WILDCARD = "*";

    protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, int maxFramePayloadLength) {
        this(version, uri, subprotocols, WebSocketDecoderConfig.newBuilder().maxFramePayloadLength(maxFramePayloadLength).build());
    }

    protected WebSocketServerHandshaker(WebSocketVersion version, String uri, String subprotocols, WebSocketDecoderConfig decoderConfig) {
        this.version = version;
        this.uri = uri;
        if (subprotocols != null) {
            String[] subprotocolArray = subprotocols.split(",");
            for (int i = 0; i < subprotocolArray.length; ++i) {
                subprotocolArray[i] = subprotocolArray[i].trim();
            }
            this.subprotocols = subprotocolArray;
        } else {
            this.subprotocols = EmptyArrays.EMPTY_STRINGS;
        }
        this.decoderConfig = (WebSocketDecoderConfig)ObjectUtil.checkNotNull((Object)decoderConfig, (String)"decoderConfig");
    }

    public String uri() {
        return this.uri;
    }

    public Set<String> subprotocols() {
        LinkedHashSet<String> ret = new LinkedHashSet<String>();
        Collections.addAll(ret, this.subprotocols);
        return ret;
    }

    public WebSocketVersion version() {
        return this.version;
    }

    public int maxFramePayloadLength() {
        return this.decoderConfig.maxFramePayloadLength();
    }

    public WebSocketDecoderConfig decoderConfig() {
        return this.decoderConfig;
    }

    public ChannelFuture handshake(Channel channel, FullHttpRequest req) {
        return this.handshake(channel, req, null, channel.newPromise());
    }

    public final ChannelFuture handshake(Channel channel, FullHttpRequest req, HttpHeaders responseHeaders, final ChannelPromise promise) {
        String encoderName;
        ChannelHandlerContext ctx;
        if (logger.isDebugEnabled()) {
            logger.debug("{} WebSocket version {} server handshake", (Object)channel, (Object)this.version());
        }
        FullHttpResponse response = this.newHandshakeResponse(req, responseHeaders);
        ChannelPipeline p = channel.pipeline();
        if (p.get(HttpObjectAggregator.class) != null) {
            p.remove(HttpObjectAggregator.class);
        }
        if (p.get(HttpContentCompressor.class) != null) {
            p.remove(HttpContentCompressor.class);
        }
        if ((ctx = p.context(HttpRequestDecoder.class)) == null) {
            ctx = p.context(HttpServerCodec.class);
            if (ctx == null) {
                promise.setFailure((Throwable)new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
                response.release();
                return promise;
            }
            p.addBefore(ctx.name(), "wsencoder", (ChannelHandler)this.newWebSocketEncoder());
            p.addBefore(ctx.name(), "wsdecoder", (ChannelHandler)this.newWebsocketDecoder());
            encoderName = ctx.name();
        } else {
            p.replace(ctx.name(), "wsdecoder", (ChannelHandler)this.newWebsocketDecoder());
            encoderName = p.context(HttpResponseEncoder.class).name();
            p.addBefore(encoderName, "wsencoder", (ChannelHandler)this.newWebSocketEncoder());
        }
        channel.writeAndFlush((Object)response).addListener((GenericFutureListener)new ChannelFutureListener(){

            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    ChannelPipeline p = future.channel().pipeline();
                    p.remove(encoderName);
                    promise.setSuccess();
                } else {
                    promise.setFailure(future.cause());
                }
            }
        });
        return promise;
    }

    public ChannelFuture handshake(Channel channel, HttpRequest req) {
        return this.handshake(channel, req, null, channel.newPromise());
    }

    public final ChannelFuture handshake(final Channel channel, HttpRequest req, final HttpHeaders responseHeaders, final ChannelPromise promise) {
        ChannelPipeline p;
        ChannelHandlerContext ctx;
        if (req instanceof FullHttpRequest) {
            return this.handshake(channel, (FullHttpRequest)req, responseHeaders, promise);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("{} WebSocket version {} server handshake", (Object)channel, (Object)this.version());
        }
        if ((ctx = (p = channel.pipeline()).context(HttpRequestDecoder.class)) == null && (ctx = p.context(HttpServerCodec.class)) == null) {
            promise.setFailure((Throwable)new IllegalStateException("No HttpDecoder and no HttpServerCodec in the pipeline"));
            return promise;
        }
        String aggregatorCtx = ctx.name();
        if (HttpUtil.isContentLengthSet(req) || HttpUtil.isTransferEncodingChunked(req) || this.version == WebSocketVersion.V00) {
            aggregatorCtx = "httpAggregator";
            p.addAfter(ctx.name(), aggregatorCtx, (ChannelHandler)new HttpObjectAggregator(8192));
        }
        p.addAfter(aggregatorCtx, "handshaker", (ChannelHandler)new ChannelInboundHandlerAdapter(){
            private FullHttpRequest fullHttpRequest;

            public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                if (msg instanceof HttpObject) {
                    try {
                        this.handleHandshakeRequest(ctx, (HttpObject)msg);
                    }
                    finally {
                        ReferenceCountUtil.release((Object)msg);
                    }
                } else {
                    super.channelRead(ctx, msg);
                }
            }

            public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                ctx.pipeline().remove((ChannelHandler)this);
                promise.tryFailure(cause);
                ctx.fireExceptionCaught(cause);
            }

            public void channelInactive(ChannelHandlerContext ctx) throws Exception {
                try {
                    if (!promise.isDone()) {
                        promise.tryFailure((Throwable)new ClosedChannelException());
                    }
                    ctx.fireChannelInactive();
                }
                finally {
                    this.releaseFullHttpRequest();
                }
            }

            public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
                this.releaseFullHttpRequest();
            }

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            private void handleHandshakeRequest(ChannelHandlerContext ctx, HttpObject httpObject) {
                if (httpObject instanceof FullHttpRequest) {
                    ctx.pipeline().remove((ChannelHandler)this);
                    WebSocketServerHandshaker.this.handshake(channel, (FullHttpRequest)httpObject, responseHeaders, promise);
                    return;
                }
                if (httpObject instanceof LastHttpContent) {
                    assert (this.fullHttpRequest != null);
                    FullHttpRequest handshakeRequest = this.fullHttpRequest;
                    this.fullHttpRequest = null;
                    try {
                        ctx.pipeline().remove((ChannelHandler)this);
                        WebSocketServerHandshaker.this.handshake(channel, handshakeRequest, responseHeaders, promise);
                    }
                    finally {
                        handshakeRequest.release();
                    }
                    return;
                }
                if (httpObject instanceof HttpRequest) {
                    HttpRequest httpRequest = (HttpRequest)httpObject;
                    this.fullHttpRequest = new DefaultFullHttpRequest(httpRequest.protocolVersion(), httpRequest.method(), httpRequest.uri(), Unpooled.EMPTY_BUFFER, httpRequest.headers(), EmptyHttpHeaders.INSTANCE);
                    if (httpRequest.decoderResult().isFailure()) {
                        this.fullHttpRequest.setDecoderResult(httpRequest.decoderResult());
                    }
                }
            }

            private void releaseFullHttpRequest() {
                if (this.fullHttpRequest != null) {
                    this.fullHttpRequest.release();
                    this.fullHttpRequest = null;
                }
            }
        });
        try {
            ctx.fireChannelRead(ReferenceCountUtil.retain((Object)req));
        }
        catch (Throwable cause) {
            promise.setFailure(cause);
        }
        return promise;
    }

    protected abstract FullHttpResponse newHandshakeResponse(FullHttpRequest var1, HttpHeaders var2);

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull((Object)channel, (String)"channel");
        return this.close(channel, frame, channel.newPromise());
    }

    public ChannelFuture close(Channel channel, CloseWebSocketFrame frame, ChannelPromise promise) {
        return this.close0((ChannelOutboundInvoker)channel, frame, promise);
    }

    public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame) {
        ObjectUtil.checkNotNull((Object)ctx, (String)"ctx");
        return this.close(ctx, frame, ctx.newPromise());
    }

    public ChannelFuture close(ChannelHandlerContext ctx, CloseWebSocketFrame frame, ChannelPromise promise) {
        ObjectUtil.checkNotNull((Object)ctx, (String)"ctx");
        return this.close0((ChannelOutboundInvoker)ctx, frame, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }

    private ChannelFuture close0(ChannelOutboundInvoker invoker, CloseWebSocketFrame frame, ChannelPromise promise) {
        return invoker.writeAndFlush((Object)frame, promise).addListener((GenericFutureListener)ChannelFutureListener.CLOSE);
    }

    protected String selectSubprotocol(String requestedSubprotocols) {
        String[] requestedSubprotocolArray;
        if (requestedSubprotocols == null || this.subprotocols.length == 0) {
            return null;
        }
        for (String p : requestedSubprotocolArray = requestedSubprotocols.split(",")) {
            String requestedSubprotocol = p.trim();
            for (String supportedSubprotocol : this.subprotocols) {
                if (!SUB_PROTOCOL_WILDCARD.equals(supportedSubprotocol) && !requestedSubprotocol.equals(supportedSubprotocol)) continue;
                this.selectedSubprotocol = requestedSubprotocol;
                return requestedSubprotocol;
            }
        }
        return null;
    }

    public String selectedSubprotocol() {
        return this.selectedSubprotocol;
    }

    protected abstract WebSocketFrameDecoder newWebsocketDecoder();

    protected abstract WebSocketFrameEncoder newWebSocketEncoder();
}

