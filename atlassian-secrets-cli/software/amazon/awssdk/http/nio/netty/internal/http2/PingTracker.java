/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.http.nio.netty.internal.http2;

import io.netty.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;

@SdkInternalApi
public final class PingTracker {
    private final Supplier<ScheduledFuture<?>> timerFutureSupplier;
    private ScheduledFuture<?> pingTimerFuture;

    PingTracker(Supplier<ScheduledFuture<?>> timerFutureSupplier) {
        this.timerFutureSupplier = timerFutureSupplier;
    }

    public void start() {
        this.pingTimerFuture = this.timerFutureSupplier.get();
    }

    public void cancel() {
        if (this.pingTimerFuture != null) {
            this.pingTimerFuture.cancel(false);
        }
    }
}

