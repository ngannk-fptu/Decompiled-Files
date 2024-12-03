/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.Channel
 *  io.netty.channel.pool.ChannelPool
 *  io.netty.util.concurrent.EventExecutor
 *  io.netty.util.concurrent.Future
 *  io.netty.util.concurrent.Promise
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.Channel;
import io.netty.channel.pool.ChannelPool;
import io.netty.util.concurrent.EventExecutor;
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

    public Future<Channel> acquire() {
        return this.delegatePool.acquire();
    }

    public Future<Channel> acquire(Promise<Channel> promise) {
        return this.delegatePool.acquire(promise);
    }

    public Future<Void> release(Channel channel) {
        return this.release(channel, (Promise<Void>)channel.eventLoop().newPromise());
    }

    public Future<Void> release(Channel channel, Promise<Void> promise) {
        NettyUtils.doInEventLoop((EventExecutor)channel.eventLoop(), () -> {
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

    public void close() {
        this.delegatePool.close();
    }
}

