/*
 * Decompiled with CFR 0.152.
 */
package software.amazon.awssdk.utils.cache;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.Semaphore;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.ThreadFactoryBuilder;
import software.amazon.awssdk.utils.cache.CachedSupplier;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkProtectedApi
public class NonBlocking
implements CachedSupplier.PrefetchStrategy {
    private static final Logger log = Logger.loggerFor(NonBlocking.class);
    private static final int MAX_CONCURRENT_REFRESHES = 100;
    private static final Semaphore CONCURRENT_REFRESH_LEASES = new Semaphore(100);
    private static final ScheduledThreadPoolExecutor SCHEDULER = new ScheduledThreadPoolExecutor(1, new ThreadFactoryBuilder().threadNamePrefix("sdk-cache-scheduler").daemonThreads(true).build());
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(1, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new ThreadFactoryBuilder().threadNamePrefix("sdk-cache").daemonThreads(true).build());
    private static final AtomicLong INSTANCE_NUMBER = new AtomicLong(0L);
    private final AtomicBoolean currentlyPrefetching = new AtomicBoolean(false);
    private final String asyncThreadName;
    private final AtomicReference<ScheduledFuture<?>> refreshTask = new AtomicReference();
    private volatile boolean shutdown = false;
    private volatile CachedSupplier<?> cachedSupplier;

    public NonBlocking(String asyncThreadName) {
        this.asyncThreadName = asyncThreadName + "-" + INSTANCE_NUMBER.getAndIncrement();
    }

    @SdkTestInternalApi
    static ThreadPoolExecutor executor() {
        return EXECUTOR;
    }

    @Override
    public void initializeCachedSupplier(CachedSupplier<?> cachedSupplier) {
        this.cachedSupplier = cachedSupplier;
    }

    @Override
    public void prefetch(Runnable valueUpdater) {
        if (this.currentlyPrefetching.compareAndSet(false, true)) {
            this.tryRunBackgroundTask(valueUpdater, () -> this.currentlyPrefetching.set(false));
        }
    }

    @Override
    public <T> RefreshResult<T> fetch(Supplier<RefreshResult<T>> supplier) {
        RefreshResult<T> result = supplier.get();
        this.schedulePrefetch(result);
        return result;
    }

    private void schedulePrefetch(RefreshResult<?> result) {
        if (this.shutdown || result.staleTime() == null || result.prefetchTime() == null) {
            return;
        }
        Duration timeUntilPrefetch = Duration.between(Instant.now(), result.prefetchTime());
        if (timeUntilPrefetch.isNegative() || timeUntilPrefetch.toDays() > 7L) {
            log.debug(() -> "Skipping background refresh because the prefetch time is in the past or too far in the future: " + result.prefetchTime());
            return;
        }
        Instant backgroundRefreshTime = result.prefetchTime().plusSeconds(1L);
        Duration timeUntilBackgroundRefresh = timeUntilPrefetch.plusSeconds(1L);
        log.debug(() -> "Scheduling refresh attempt for " + backgroundRefreshTime + " (in " + timeUntilBackgroundRefresh.toMillis() + " ms)");
        ScheduledFuture<?> scheduledTask = SCHEDULER.schedule(() -> this.runWithInstanceThreadName(() -> {
            log.debug(() -> "Executing refresh attempt scheduled for " + backgroundRefreshTime);
            this.tryRunBackgroundTask(this.cachedSupplier::get);
        }), timeUntilBackgroundRefresh.toMillis(), TimeUnit.MILLISECONDS);
        this.updateTask(scheduledTask);
        if (this.shutdown) {
            this.updateTask(null);
        }
    }

    @Override
    public void close() {
        this.shutdown = true;
        this.updateTask(null);
    }

    public void updateTask(ScheduledFuture<?> newTask) {
        ScheduledFuture<?> currentTask;
        do {
            if ((currentTask = this.refreshTask.get()) == null || currentTask.isDone()) continue;
            currentTask.cancel(false);
        } while (!this.refreshTask.compareAndSet(currentTask, newTask));
    }

    public void tryRunBackgroundTask(Runnable runnable) {
        this.tryRunBackgroundTask(runnable, () -> {});
    }

    public void tryRunBackgroundTask(Runnable runnable, Runnable runOnCompletion) {
        if (!CONCURRENT_REFRESH_LEASES.tryAcquire()) {
            log.warn(() -> "Skipping a background refresh task because there are too many other tasks running.");
            runOnCompletion.run();
            return;
        }
        try {
            EXECUTOR.submit(() -> this.runWithInstanceThreadName(() -> {
                try {
                    runnable.run();
                }
                catch (Throwable t) {
                    log.warn(() -> "Exception occurred in AWS SDK background task.", t);
                }
                finally {
                    CONCURRENT_REFRESH_LEASES.release();
                    runOnCompletion.run();
                }
            }));
        }
        catch (Throwable t) {
            log.warn(() -> "Exception occurred when submitting AWS SDK background task.", t);
            CONCURRENT_REFRESH_LEASES.release();
            runOnCompletion.run();
        }
    }

    public void runWithInstanceThreadName(Runnable runnable) {
        String baseThreadName = Thread.currentThread().getName();
        try {
            Thread.currentThread().setName(baseThreadName + "-" + this.asyncThreadName);
            runnable.run();
        }
        finally {
            Thread.currentThread().setName(baseThreadName);
        }
    }

    static {
        SCHEDULER.setRemoveOnCancelPolicy(true);
    }
}

