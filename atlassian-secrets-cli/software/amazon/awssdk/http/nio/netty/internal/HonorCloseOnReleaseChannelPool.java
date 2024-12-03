/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.ChannelAttributeKey;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyClientLogger;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;

@SdkInternalApi
public class HonorCloseOnReleaseChannelPool
implements ChannelPool {
    private static final NettyClientLogger log = NettyClientLogger.getLogger(HonorCloseOnReleaseChannelPool.class);
    private final ChannelPool delegatePool;

    public HonorCloseOnReleaseChannelPool(ChannelPool delegatePool) {
        this.delegatePool = delegatePool;
    }

    @Override
    public Future<Channel> acquire() {
        return this.delegatePool.acquire();
    }

    @Override
    public Future<Channel> acquire(Promise<Channel> promise) {
        return this.delegatePool.acquire(promise);
    }

    @Override
    public Future<Void> release(Channel channel) {
        return this.release(channel, channel.eventLoop().newPromise());
    }

    @Override
    public Future<Void> release(Channel channel, Promise<Void> promise) {
        NettyUtils.doInEventLoop(channel.eventLoop(), () -> {
            boolean shouldCloseOnRelease = Boolean.TRUE.equals(channel.attr(ChannelAttributeKey.CLOSE_ON_RELEASE).get());
            if (shouldCloseOnRelease && channel.isOpen() && !channel.eventLoop().isShuttingDown()) {
                log.debug(channel, () -> "Closing connection (" + channel.id() + "), instead of releasing it.");
                channel.close();
            }
            this.delegatePool.release(channel, promise);
        }).addListener(f -> {
            if (!f.isSuccess()) {
                promise.tryFailure(f.cause());
            }
        });
        return promise;
    }

    @Override
    public void close() {
        this.delegatePool.close();
    }
}

