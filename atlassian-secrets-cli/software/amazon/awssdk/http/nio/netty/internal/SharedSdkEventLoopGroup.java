/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal;

import io.netty.channel.EventLoopGroup;
import io.netty.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.http.nio.netty.SdkEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.DelegatingEventLoopGroup;
import software.amazon.awssdk.http.nio.netty.internal.utils.NettyUtils;

@SdkInternalApi
public final class SharedSdkEventLoopGroup {
    private static SdkEventLoopGroup sharedSdkEventLoopGroup;
    private static int referenceCount;

    private SharedSdkEventLoopGroup() {
    }

    @SdkInternalApi
    public static synchronized SdkEventLoopGroup get() {
        if (sharedSdkEventLoopGroup == null) {
            sharedSdkEventLoopGroup = SdkEventLoopGroup.builder().build();
        }
        ++referenceCount;
        return SdkEventLoopGroup.create(new ReferenceCountingEventLoopGroup(sharedSdkEventLoopGroup.eventLoopGroup()), sharedSdkEventLoopGroup.channelFactory());
    }

    private static synchronized Future<?> decrementReference(long quietPeriod, long timeout, TimeUnit unit) {
        if (--referenceCount == 0) {
            Future<?> shutdownGracefully = sharedSdkEventLoopGroup.eventLoopGroup().shutdownGracefully(quietPeriod, timeout, unit);
            sharedSdkEventLoopGroup = null;
            return shutdownGracefully;
        }
        return NettyUtils.SUCCEEDED_FUTURE;
    }

    @SdkTestInternalApi
    static synchronized int referenceCount() {
        return referenceCount;
    }

    static {
        referenceCount = 0;
    }

    private static class ReferenceCountingEventLoopGroup
    extends DelegatingEventLoopGroup {
        private final AtomicBoolean hasBeenClosed = new AtomicBoolean(false);

        private ReferenceCountingEventLoopGroup(EventLoopGroup delegate) {
            super(delegate);
        }

        @Override
        public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
            if (this.hasBeenClosed.compareAndSet(false, true)) {
                return SharedSdkEventLoopGroup.decrementReference(quietPeriod, timeout, unit);
            }
            return NettyUtils.SUCCEEDED_FUTURE;
        }
    }
}

