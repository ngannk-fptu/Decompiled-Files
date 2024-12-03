/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.ChannelDiagnostics;
import software.amazon.awssdk.http.nio.netty.internal.ListenerInvokingChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.ChannelUtils;

@SdkInternalApi
public final class InUseTrackingChannelPoolListener
implements ListenerInvokingChannelPool.ChannelPoolListener {
    private static final InUseTrackingChannelPoolListener INSTANCE = new InUseTrackingChannelPoolListener();

    private InUseTrackingChannelPoolListener() {
    }

    public static InUseTrackingChannelPoolListener create() {
        return INSTANCE;
    }

    @Override
    public void channelAcquired(Channel channel) {
        channel.attr(ChannelAttributeKey.IN_USE).set((Object)true);
        ChannelUtils.getAttribute(channel, ChannelAttributeKey.CHANNEL_DIAGNOSTICS).ifPresent(ChannelDiagnostics::stopIdleTimer);
    }

    @Override
    public void channelReleased(Channel channel) {
        channel.attr(ChannelAttributeKey.IN_USE).set((Object)false);
        ChannelUtils.getAttribute(channel, ChannelAttributeKey.CHANNEL_DIAGNOSTICS).ifPresent(ChannelDiagnostics::startIdleTimer);
    }
}

