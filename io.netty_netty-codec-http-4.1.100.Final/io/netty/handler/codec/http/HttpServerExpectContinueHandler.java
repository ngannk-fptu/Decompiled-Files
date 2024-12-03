/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.buffer.Unpooled
 *  io.netty.channel.ChannelFutureListener
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.ReferenceCountUtil
 *  io.netty.util.concurrent.GenericFutureListener
 */
package io.netty.handler.codec.http;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.GenericFutureListener;

public class HttpServerExpectContinueHandler
extends ChannelInboundHandlerAdapter {
    private static final FullHttpResponse EXPECTATION_FAILED = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.EXPECTATION_FAILED, Unpooled.EMPTY_BUFFER);
    private static final FullHttpResponse ACCEPT = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE, Unpooled.EMPTY_BUFFER);

    protected HttpResponse acceptMessage(HttpRequest request) {
        return ACCEPT.retainedDuplicate();
    }

    protected HttpResponse rejectResponse(HttpRequest request) {
        return EXPECTATION_FAILED.retainedDuplicate();
    }

    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        HttpRequest req;
        if (msg instanceof HttpRequest && HttpUtil.is100ContinueExpected(req = (HttpRequest)msg)) {
            HttpResponse accept = this.acceptMessage(req);
            if (accept == null) {
                HttpResponse rejection = this.rejectResponse(req);
                ReferenceCountUtil.release((Object)msg);
                ctx.writeAndFlush((Object)rejection).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
                return;
            }
            ctx.writeAndFlush((Object)accept).addListener((GenericFutureListener)ChannelFutureListener.CLOSE_ON_FAILURE);
            req.headers().remove((CharSequence)HttpHeaderNames.EXPECT);
        }
        super.channelRead(ctx, msg);
    }

    static {
        EXPECTATION_FAILED.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)0);
        ACCEPT.headers().set((CharSequence)HttpHeaderNames.CONTENT_LENGTH, (Object)0);
    }
}

