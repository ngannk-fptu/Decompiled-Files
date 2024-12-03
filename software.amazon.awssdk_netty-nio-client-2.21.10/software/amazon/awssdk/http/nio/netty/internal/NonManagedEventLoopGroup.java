/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.netty.channel.EventLoopGroup
 *  io.netty.util.concurrent.Future
 *  software.amazon.awssdk.annotations.SdkInternalApi
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.http.nio.netty.internal.DelegatingEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;

@SdkInternalApi
public final class NonManagedEventLoopGroup
extends DelegatingEventLoopGroup {
    public NonManagedEventLoopGroup(EventLoopGroup delegate) {
        super(delegate);
    }

    @Override
    public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
        return NettyUtils.SUCCEEDED_FUTURE;
    }
}

