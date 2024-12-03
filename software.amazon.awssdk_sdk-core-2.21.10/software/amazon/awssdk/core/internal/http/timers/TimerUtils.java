/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkInternalApi
 *  software.amazon.awssdk.utils.OptionalUtils
 */
package software.amazon.awssdk.core.internal.http.timers;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkInternalApi;
import software.amazon.awssdk.core.exception.SdkClientException;
import software.amazon.awssdk.core.internal.http.timers.ApiCallTimeoutTracker;
import software.amazon.awssdk.core.internal.http.timers.AsyncTimeoutTask;
import software.amazon.awssdk.core.internal.http.timers.NoOpTimeoutTracker;
import software.amazon.awssdk.core.internal.http.timers.SyncTimeoutTask;
import software.amazon.awssdk.core.internal.http.timers.TimeoutTracker;
import software.amazon.awssdk.utils.OptionalUtils;

@SdkInternalApi
public final class TimerUtils {
    private TimerUtils() {
    }

    public static <T> TimeoutTracker timeAsyncTaskIfNeeded(CompletableFuture<T> completableFuture, ScheduledExecutorService timeoutExecutor, Supplier<SdkClientException> exceptionSupplier, long timeoutInMills) {
        if (timeoutInMills <= 0L) {
            return NoOpTimeoutTracker.INSTANCE;
        }
        AsyncTimeoutTask timeoutTask = new AsyncTimeoutTask(completableFuture, exceptionSupplier);
        ScheduledFuture<?> scheduledFuture = timeoutExecutor.schedule(timeoutTask, timeoutInMills, TimeUnit.MILLISECONDS);
        ApiCallTimeoutTracker timeoutTracker = new ApiCallTimeoutTracker(timeoutTask, scheduledFuture);
        completableFuture.whenComplete((o, t) -> timeoutTracker.cancel());
        return timeoutTracker;
    }

    public static TimeoutTracker timeSyncTaskIfNeeded(ScheduledExecutorService timeoutExecutor, long timeoutInMills, Thread threadToInterrupt) {
        if (timeoutInMills <= 0L) {
            return NoOpTimeoutTracker.INSTANCE;
        }
        SyncTimeoutTask timeoutTask = new SyncTimeoutTask(threadToInterrupt);
        ScheduledFuture<?> scheduledFuture = timeoutExecutor.schedule(timeoutTask, timeoutInMills, TimeUnit.MILLISECONDS);
        return new ApiCallTimeoutTracker(timeoutTask, scheduledFuture);
    }

    public static long resolveTimeoutInMillis(Supplier<Optional<Duration>> supplier, Duration fallback) {
        return OptionalUtils.firstPresent(supplier.get(), () -> fallback).map(Duration::toMillis).orElse(0L);
    }
}

