/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultHttpContent;
import io.netty.handler.codec.http.DefaultLastHttpContent;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http2.Http2DataFrame;
import io.netty.handler.codec.http2.Http2Error;
import io.netty.handler.codec.http2.Http2Exception;
import io.netty.handler.codec.http2.Http2Frame;
import io.netty.handler.codec.http2.Http2HeadersFrame;
import io.netty.handler.codec.http2.Http2ResetFrame;
import io.netty.handler.codec.http2.HttpConversionUtil;
import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.HttpStatusFamily;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2ConnectionTerminatingException;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@SdkInternalApi
public class Http2ToHttpInboundAdapter
extends SimpleChannelInboundHandler<Http2Frame> {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http2ToHttpInboundAdapter.class);

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Http2Frame frame) throws Exception {
        if (frame instanceof Http2DataFrame) {
            this.onDataRead((Http2DataFrame)frame, ctx);
        } else if (frame instanceof Http2HeadersFrame) {
            this.onHeadersRead((Http2HeadersFrame)frame, ctx);
            ctx.channel().read();
        } else if (frame instanceof Http2ResetFrame) {
            this.onRstStreamRead((Http2ResetFrame)frame, ctx);
        } else {
            ctx.channel().parent().read();
        }
    }

    private void onHeadersRead(Http2HeadersFrame headersFrame, ChannelHandlerContext ctx) throws Http2Exception {
        HttpResponse httpResponse = HttpConversionUtil.toHttpResponse(headersFrame.stream().id(), headersFrame.headers(), true);
        ctx.fireChannelRead(httpResponse);
        if (HttpStatusFamily.of(httpResponse.status().code()) == HttpStatusFamily.SERVER_ERROR) {
            this.fireConnectionExceptionForServerError(ctx);
        }
    }

    private void fireConnectionExceptionForServerError(ChannelHandlerContext ctx) {
        if (ctx.channel().parent() != null) {
            Channel parent = ctx.channel().parent();
            log.debug(ctx.channel(), () -> "A 5xx server error occurred on an Http2 stream, notifying the connection channel " + ctx.channel());
            parent.pipeline().fireExceptionCaught(new Http2ConnectionTerminatingException("A 5xx server error occurred on an Http2 stream " + ctx.channel()));
        }
    }

    private void onDataRead(Http2DataFrame dataFrame, ChannelHandlerContext ctx) throws Http2Exception {
        ByteBuf data = dataFrame.content();
        data.retain();
        if (!dataFrame.isEndStream()) {
            ctx.fireChannelRead(new DefaultHttpContent(data));
        } else {
            ctx.fireChannelRead(new DefaultLastHttpContent(data));
        }
    }

    private void onRstStreamRead(Http2ResetFrame resetFrame, ChannelHandlerContext ctx) throws Http2Exception {
        ctx.fireExceptionCaught(new Http2ResetException(resetFrame.errorCode()));
    }

    public static final class Http2ResetException
    extends IOException {
        Http2ResetException(long errorCode) {
            super(String.format("Connection reset. Error - %s(%d)", Http2Error.valueOf(errorCode).name(), errorCode));
        }
    }
}

