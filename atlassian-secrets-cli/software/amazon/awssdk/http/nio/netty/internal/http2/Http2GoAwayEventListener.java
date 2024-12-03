/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.handler.codec.http2.Http2ConnectionAdapter;
import java.nio.charset.StandardCharsets;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.http2.GoAwayException;
import software.amazon.awssdk.http.nio.netty.internal.http2.Http2MultiplexedChannelPool;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;

@SdkInternalApi
public final class Http2GoAwayEventListener
extends Http2ConnectionAdapter {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(Http2GoAwayEventListener.class);
    private final Channel parentChannel;

    public Http2GoAwayEventListener(Channel parentChannel) {
        this.parentChannel = parentChannel;
    }

    @Override
    public void onGoAwayReceived(int lastStreamId, long errorCode, ByteBuf debugData) {
        Http2MultiplexedChannelPool channelPool = this.parentChannel.attr(ChannelAttributeKey.HTTP2_MULTIPLEXED_CHANNEL_POOL).get();
        GoAwayException exception = new GoAwayException(errorCode, debugData.toString(StandardCharsets.UTF_8));
        if (channelPool != null) {
            channelPool.handleGoAway(this.parentChannel, lastStreamId, exception);
        } else {
            log.warn(this.parentChannel, () -> "GOAWAY received on a connection (" + this.parentChannel + ") not associated with any multiplexed channel pool.");
            this.parentChannel.pipeline().fireExceptionCaught(exception);
        }
    }
}

