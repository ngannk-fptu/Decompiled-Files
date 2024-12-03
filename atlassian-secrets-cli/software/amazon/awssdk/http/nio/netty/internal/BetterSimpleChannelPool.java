/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.pool.ChannelPoolHandler;
import io.netty.channel.pool.SimpleChannelPool;
import java.util.concurrent.CompletableFuture;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class BetterSimpleChannelPool
extends SimpleChannelPool {
    private final CompletableFuture<Boolean> closeFuture = new CompletableFuture();

    BetterSimpleChannelPool(Bootstrap bootstrap, ChannelPoolHandler handler) {
        super(bootstrap, handler);
    }

    @Override
    public void close() {
        super.close();
        this.closeFuture.complete(true);
    }

    CompletableFuture<Boolean> closeFuture() {
        return this.closeFuture;
    }
}

