/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.ChannelHandler$Sharable
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.ChannelInboundHandlerAdapter
 *  io.netty.util.Attribute
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.Attribute;
import java.io.IOException;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.FutureCancelledException;
import software.amazon.awssdk.http.nio.netty.internal.RequestContext;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@ChannelHandler.Sharable
@SdkInternalApi
public final class FutureCancelHandler
extends ChannelInboundHandlerAdapter {
    private static final NettyClientLogger LOG = NettyClientLogger.getLogger(FutureCancelHandler.class);
    private static final FutureCancelHandler INSTANCE = new FutureCancelHandler();

    private FutureCancelHandler() {
    }

    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        if (!(e instanceof FutureCancelledException)) {
            ctx.fireExceptionCaught(e);
            return;
        }
        FutureCancelledException cancelledException = (FutureCancelledException)e;
        Long channelExecutionId = FutureCancelHandler.executionId(ctx);
        if (channelExecutionId == null) {
            RequestContext requestContext = (RequestContext)ctx.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
            LOG.warn(ctx.channel(), () -> String.format("Received a cancellation exception on a channel that doesn't have an execution Id attached. Exception's execution ID is %d. Exception is being ignored. Closing the channel", FutureCancelHandler.executionId(ctx)));
            ctx.close();
            requestContext.channelPool().release(ctx.channel());
        } else if (FutureCancelHandler.currentRequestCancelled(channelExecutionId, cancelledException)) {
            RequestContext requestContext = (RequestContext)ctx.channel().attr(ChannelAttributeKey.REQUEST_CONTEXT_KEY).get();
            requestContext.handler().onError(e);
            ctx.fireExceptionCaught((Throwable)new IOException("Request cancelled"));
            ctx.close();
            requestContext.channelPool().release(ctx.channel());
        } else {
            LOG.debug(ctx.channel(), () -> String.format("Received a cancellation exception but it did not match the current execution ID. Exception's execution ID is %d, but the current ID on the channel is %d. Exception is being ignored.", cancelledException.getExecutionId(), FutureCancelHandler.executionId(ctx)));
        }
    }

    public static FutureCancelHandler getInstance() {
        return INSTANCE;
    }

    private static boolean currentRequestCancelled(long executionId, FutureCancelledException e) {
        return e.getExecutionId() == executionId;
    }

    private static Long executionId(ChannelHandlerContext ctx) {
        Attribute attr = ctx.channel().attr(ChannelAttributeKey.EXECUTION_ID_KEY);
        if (attr == null) {
            return null;
        }
        return (Long)attr.get();
    }
}

