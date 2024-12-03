/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.handler.timeout.ReadTimeoutHandler
 *  io.netty.handler.timeout.WriteTimeoutHandler
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ListenerInvokingChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.ResponseHandler;
import software.amazon.awssdk.http.nio.netty.internal.http2.FlushOnReadHandler;
import software.amazon.awssdk.http.nio.netty.internal.nrs.HttpStreamsClientHandler;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;

@SdkInternalApi
public final class HandlerRemovingChannelPoolListener
implements ListenerInvokingChannelPool.ChannelPoolListener {
    private static final HandlerRemovingChannelPoolListener INSTANCE = new HandlerRemovingChannelPoolListener();

    private HandlerRemovingChannelPoolListener() {
    }

    public static HandlerRemovingChannelPoolListener create() {
        return INSTANCE;
    }

    @Override
    public void channelReleased(Channel channel) {
        if (channel.isOpen() || channel.isRegistered()) {
            ChannelUtils.removeIfExists(channel.pipeline(), HttpStreamsClientHandler.class, FlushOnReadHandler.class, ResponseHandler.class, ReadTimeoutHandler.class, WriteTimeoutHandler.class);
        }
    }
}

