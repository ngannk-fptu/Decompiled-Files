/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  software.amazon.awssdk.annotations.SdkProtectedApi
 *  software.amazon.awssdk.annotations.SdkTestInternalApi
 */
package software.amazon.awssdk.utils.cache;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;
import software.amazon.awssdk.annotations.SdkProtectedApi;
import software.amazon.awssdk.annotations.SdkTestInternalApi;
import software.amazon.awssdk.utils.ComparableUtils;
import software.amazon.awssdk.utils.Logger;
import software.amazon.awssdk.utils.SdkAutoCloseable;
import software.amazon.awssdk.utils.Validate;
import software.amazon.awssdk.utils.cache.OneCallerBlocks;
import software.amazon.awssdk.utils.cache.RefreshResult;

@SdkProtectedApi
public class CachedSupplier<T>
implements Supplier<T>,
SdkAutoCloseable {
    private static final Logger log = Logger.loggerFor(CachedSupplier.class);
    private static final Duration BLOCKING_REFRESH_MAX_WAIT = Duration.ofSeconds(5L);
    private final Lock refreshLock = new ReentrantLock();
    private final PrefetchStrategy prefetchStrategy;
    private final AtomicBoolean prefetchStrategyInitialized = new AtomicBoolean(false);
    private final StaleValueBehavior staleValueBehavior;
    private final Clock clock;
    private final AtomicInteger consecutiveStaleRetrievalFailures = new AtomicInteger(0);
    private final String cachedValueName;
    private volatile RefreshResult<T> cachedValue;
    private final Supplier<RefreshResult<T>> valueSupplier;
    private final Random jitterRandom = new Random();

    private CachedSupplier(Builder<T> builder) {
        Validate.notNull(((Builder)builder).supplier, "builder.supplier", new Object[0]);
        Validate.notNull(((Builder)builder).jitterEnabled, "builder.jitterEnabled", new Object[0]);
        this.valueSupplier = this.jitteredPrefetchValueSupplier(((Builder)builder).supplier, ((Builder)builder).jitterEnabled);
        this.prefetchStrategy = Validate.notNull(((Builder)builder).prefetchStrategy, "builder.prefetchStrategy", new Object[0]);
        this.staleValueBehavior = Validate.notNull(((Builder)builder).staleValueBehavior, "builder.staleValueBehavior", new Object[0]);
        this.clock = Validate.notNull(((Builder)builder).clock, "builder.clock", new Object[0]);
        this.cachedValueName = Validate.notNull(((Builder)builder).cachedValueName, "builder.cachedValueName", new Object[0]);
    }

    public static <T> Builder<T> builder(Supplier<RefreshResult<T>> valueSupplier) {
        return new Builder(valueSupplier);
    }

    @Override
    public T get() {
        if (this.cacheIsStale()) {
            log.debug(() -> "(" + this.cachedValueName + ") Cached value is stale and will be refreshed.");
            this.refreshCache();
        } else if (this.shouldInitiateCachePrefetch()) {
            log.debug(() -> "(" + this.cachedValueName + ") Cached value has reached prefetch time and will be refreshed.");
            this.prefetchCache();
        }
        return this.cachedValue.value();
    }

    private boolean cacheIsStale() {
        RefreshResult<T> currentCachedValue = this.cachedValue;
        if (currentCachedValue == null) {
            return true;
        }
        if (currentCachedValue.staleTime() == null) {
            return false;
        }
        Instant now = this.clock.instant();
        return !now.isBefore(currentCachedValue.staleTime());
    }

    private boolean shouldInitiateCachePrefetch() {
        RefreshResult<T> currentCachedValue = this.cachedValue;
        if (currentCachedValue == null) {
            return false;
        }
        if (currentCachedValue.prefetchTime() == null) {
            return false;
        }
        return !this.clock.instant().isBefore(currentCachedValue.prefetchTime());
    }

    private void prefetchCache() {
        this.prefetchStrategy.prefetch(this::refreshCache);
    }

    private void refreshCache() {
        try {
            boolean lockAcquired = this.refreshLock.tryLock(BLOCKING_REFRESH_MAX_WAIT.getSeconds(), TimeUnit.SECONDS);
            try {
                if (this.cacheIsStale() || this.shouldInitiateCachePrefetch()) {
                    log.debug(() -> "(" + this.cachedValueName + ") Refreshing cached value.");
                    if (this.prefetchStrategyInitialized.compareAndSet(false, true)) {
                        this.prefetchStrategy.initializeCachedSupplier(this);
                    }
                    try {
                        RefreshResult<T> cachedValue = this.handleFetchedSuccess(this.prefetchStrategy.fetch(this.valueSupplier));
                        this.cachedValue = cachedValue;
                        log.debug(() -> "(" + this.cachedValueName + ") Successfully refreshed cached value. Next Prefetch Time: " + cachedValue.prefetchTime() + ". Next Stale Time: " + cachedValue.staleTime());
                    }
                    catch (RuntimeException t) {
                        this.cachedValue = this.handleFetchFailure(t);
                    }
                }
            }
            finally {
                if (lockAcquired) {
                    this.refreshLock.unlock();
                }
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted waiting to refresh a cached value.", e);
        }
    }

    private RefreshResult<T> handleFetchedSuccess(RefreshResult<T> fetch) {
        this.consecutiveStaleRetrievalFailures.set(0);
        Instant now = this.clock.instant();
        if (now.isBefore(fetch.staleTime())) {
            return fetch;
        }
        switch (this.staleValueBehavior) {
            case STRICT: {
                Instant newStale = now.plusSeconds(1L);
                log.warn(() -> "(" + this.cachedValueName + ") Retrieved value expiration is in the past (" + fetch.staleTime() + "). Using expiration of " + newStale);
                return ((RefreshResult.Builder)fetch.toBuilder()).staleTime(newStale).build();
            }
            case ALLOW: {
                Instant newStaleTime = this.jitterTime(now, Duration.ofMinutes(1L), Duration.ofMinutes(10L));
                log.warn(() -> "(" + this.cachedValueName + ") Cached value expiration has been extended to " + newStaleTime + " because the downstream service returned a time in the past: " + fetch.staleTime());
                return ((RefreshResult.Builder)fetch.toBuilder()).staleTime(newStaleTime).build();
            }
        }
        throw new IllegalStateException("Unknown stale-value-behavior: " + (Object)((Object)this.staleValueBehavior));
    }

    private RefreshResult<T> handleFetchFailure(RuntimeException e) {
        log.debug(() -> "(" + this.cachedValueName + ") Failed to refresh cached value.", e);
        RefreshResult<T> currentCachedValue = this.cachedValue;
        if (currentCachedValue == null) {
            throw e;
        }
        Instant now = this.clock.instant();
        if (!now.isBefore(currentCachedValue.staleTime())) {
            int numFailures = this.consecutiveStaleRetrievalFailures.incrementAndGet();
            switch (this.staleValueBehavior) {
                case STRICT: {
                    throw e;
                }
                case ALLOW: {
                    Instant newStaleTime = this.jitterTime(now, Duration.ofMillis(1L), this.maxStaleFailureJitter(numFailures));
                    log.warn(() -> "(" + this.cachedValueName + ") Cached value expiration has been extended to " + newStaleTime + " because calling the downstream service failed (consecutive failures: " + numFailures + ").", e);
                    return ((RefreshResult.Builder)currentCachedValue.toBuilder()).staleTime(newStaleTime).build();
                }
            }
            throw new IllegalStateException("Unknown stale-value-behavior: " + (Object)((Object)this.staleValueBehavior));
        }
        return currentCachedValue;
    }

    private Supplier<RefreshResult<T>> jitteredPrefetchValueSupplier(Supplier<RefreshResult<T>> supplier, boolean prefetchJitterEnabled) {
        return () -> {
            RefreshResult result = (RefreshResult)supplier.get();
            if (!prefetchJitterEnabled || result.prefetchTime() == null) {
                return result;
            }
            Duration maxJitter = this.maxPrefetchJitter(result);
            if (maxJitter.isZero()) {
                return result;
            }
            Instant newPrefetchTime = this.jitterTime(result.prefetchTime(), Duration.ZERO, maxJitter);
            return ((RefreshResult.Builder)result.toBuilder()).prefetchTime(newPrefetchTime).build();
        };
    }

    private Duration maxPrefetchJitter(RefreshResult<T> result) {
        Instant staleTime = result.staleTime() != null ? result.staleTime() : Instant.MAX;
        Instant oneMinuteBeforeStale = staleTime.minus(1L, ChronoUnit.MINUTES);
        if (!result.prefetchTime().isBefore(oneMinuteBeforeStale)) {
            return Duration.ZERO;
        }
        Duration timeBetweenPrefetchAndStale = Duration.between(result.prefetchTime(), oneMinuteBeforeStale);
        if (timeBetweenPrefetchAndStale.toDays() > 365L) {
            return Duration.ofMinutes(5L);
        }
        return timeBetweenPrefetchAndStale;
    }

    private Duration maxStaleFailureJitter(int numFailures) {
        long exponentialBackoffMillis = (1L << numFailures - 1) * 100L;
        return (Duration)ComparableUtils.minimum((Comparable[])new Duration[]{Duration.ofMillis(exponentialBackoffMillis), Duration.ofSeconds(10L)});
    }

    private Instant jitterTime(Instant time, Duration jitterStart, Duration jitterEnd) {
        long jitterRange = jitterEnd.minus(jitterStart).toMillis();
        long jitterAmount = Math.abs(this.jitterRandom.nextLong() % jitterRange);
        return time.plus(jitterStart).plusMillis(jitterAmount);
    }

    @Override
    public void close() {
        this.prefetchStrategy.close();
    }

    public static enum StaleValueBehavior {
        STRICT,
        ALLOW;

    }

    @FunctionalInterface
    public static interface PrefetchStrategy
    extends SdkAutoCloseable {
        public void prefetch(Runnable var1);

        default public <T> RefreshResult<T> fetch(Supplier<RefreshResult<T>> supplier) {
            return supplier.get();
        }

        default public void initializeCachedSupplier(CachedSupplier<?> cachedSupplier) {
        }

        @Override
        default public void close() {
        }
    }

    public static final class Builder<T> {
        private final Supplier<RefreshResult<T>> supplier;
        private PrefetchStrategy prefetchStrategy = new OneCallerBlocks();
        private Boolean jitterEnabled = true;
        private StaleValueBehavior staleValueBehavior = StaleValueBehavior.STRICT;
        private Clock clock = Clock.systemUTC();
        private String cachedValueName = "unknown";

        private Builder(Supplier<RefreshResult<T>> supplier) {
            this.supplier = supplier;
        }

        public Builder<T> prefetchStrategy(PrefetchStrategy prefetchStrategy) {
            this.prefetchStrategy = prefetchStrategy;
            return this;
        }

        public Builder<T> staleValueBehavior(StaleValueBehavior staleValueBehavior) {
            this.staleValueBehavior = staleValueBehavior;
            return this;
        }

        public Builder<T> cachedValueName(String cachedValueName) {
            this.cachedValueName = cachedValueName;
            return this;
        }

        @SdkTestInternalApi
        public Builder<T> clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        @SdkTestInternalApi
        Builder<T> jitterEnabled(Boolean jitterEnabled) {
            this.jitterEnabled = jitterEnabled;
            return this;
        }

        public CachedSupplier<T> build() {
            return new CachedSupplier(this);
        }
    }
}

