/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ComparisonChain
 */
package com.atlassian.failurecache.failures;

import com.atlassian.failurecache.Cacheable;
import com.atlassian.failurecache.failures.FailureCache;
import com.atlassian.failurecache.failures.FailureEntry;
import com.atlassian.failurecache.util.date.Clock;
import com.atlassian.failurecache.util.date.SystemClock;
import com.google.common.base.Preconditions;
import com.google.common.collect.ComparisonChain;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public final class ExponentialBackOffFailureCache<K>
implements FailureCache<K>,
Cacheable {
    private static final long DEFAULT_INITIAL_EXPIRY_MS = Long.getLong("navlink.failurecache.initialexpiryMs", TimeUnit.SECONDS.toMillis(6L));
    private static final long DEFAULT_MAX_EXPIRY_MS = Long.getLong("navlink.failurecache.maxExpiryMs", TimeUnit.HOURS.toMillis(24L));
    private static final double DEFAULT_BACK_OFF_RATE = Integer.getInteger("navlink.failurecache.backoff", 10).intValue();
    private static final int DEFAULT_MAX_ENTRIES = Integer.getInteger("navlink.failurecache.maxEntries", 1000);
    private final Clock clock;
    private final long initialExpiryMs;
    private final long maxExpiryMs;
    private final double backOffRate;
    private final int maxEntries;
    private final ConcurrentMap<K, FailureEntry> cache;
    private final AtomicInteger checkSizeCount;

    @Deprecated
    public ExponentialBackOffFailureCache(Clock clock) {
        this(new Builder().clock(clock));
    }

    @Deprecated
    public ExponentialBackOffFailureCache(int initialExpiryMillis, Clock clock) {
        this(new Builder().clock(clock).initialExpiry(initialExpiryMillis, TimeUnit.MILLISECONDS));
    }

    private ExponentialBackOffFailureCache(Builder builder) {
        this.clock = builder.clock;
        this.initialExpiryMs = builder.initialExpiryUnit.toMillis(builder.initialExpiry);
        this.maxExpiryMs = builder.maxExpiryUnit.toMillis(builder.maxExpiry);
        this.backOffRate = builder.backOffRate;
        this.maxEntries = builder.maxEntries;
        this.cache = new ConcurrentHashMap<K, FailureEntry>();
        this.checkSizeCount = new AtomicInteger(0);
    }

    @Override
    public boolean isFailing(K key) {
        Preconditions.checkNotNull(key, (Object)"key");
        FailureEntry entry = (FailureEntry)this.cache.get(key);
        return entry != null && entry.isFailingNow(this.clock);
    }

    @Override
    public void registerSuccess(K key) {
        Preconditions.checkNotNull(key, (Object)"key");
        this.cache.remove(key);
    }

    @Override
    public void registerFailure(K key) {
        Preconditions.checkNotNull(key, (Object)"key");
        FailureEntry currentEntry = null;
        FailureEntry newEntry = null;
        do {
            if ((currentEntry = this.cache.putIfAbsent(key, FailureEntry.NULL_ENTRY)) == null) {
                currentEntry = FailureEntry.NULL_ENTRY;
            }
            if (!currentEntry.isFailingNow(this.clock)) continue;
            return;
        } while (!this.cache.replace(key, currentEntry, newEntry = this.calcNextFailureEntry(currentEntry)));
        this.cache.remove(key, FailureEntry.NULL_ENTRY);
        this.checkMaxSize();
    }

    @Override
    public int getCachePriority() {
        return 200;
    }

    @Override
    public void clearCache() {
        this.cache.clear();
        this.checkSizeCount.set(0);
    }

    private FailureEntry calcNextFailureEntry(FailureEntry currentEntry) {
        int failureCount = currentEntry.getFailureCount();
        long expiryOffset = (long)((double)this.initialExpiryMs * Math.pow(this.backOffRate, failureCount));
        expiryOffset = Math.min(expiryOffset, this.maxExpiryMs);
        return new FailureEntry(new Date(this.clock.getCurrentDate().getTime() + expiryOffset), ++failureCount);
    }

    private void checkMaxSize() {
        int count = this.checkSizeCount.incrementAndGet();
        if (count % (this.maxEntries / 10) != 0) {
            return;
        }
        this.checkSizeCount.set(0);
        if (this.cache.size() >= this.maxEntries) {
            ArrayList entries = new ArrayList(this.cache.entrySet());
            if (entries.size() < this.maxEntries) {
                return;
            }
            Collections.sort(entries, new Comparator<Map.Entry<K, FailureEntry>>(){

                @Override
                public int compare(Map.Entry<K, FailureEntry> o1, Map.Entry<K, FailureEntry> o2) {
                    return ComparisonChain.start().compare((Comparable)o2.getValue(), (Comparable)o1.getValue()).result();
                }
            });
            for (Map.Entry entry : entries.subList(this.maxEntries / 2, entries.size())) {
                this.cache.remove(entry.getKey());
            }
        }
    }

    static /* synthetic */ long access$700() {
        return DEFAULT_INITIAL_EXPIRY_MS;
    }

    static /* synthetic */ long access$800() {
        return DEFAULT_MAX_EXPIRY_MS;
    }

    static /* synthetic */ double access$900() {
        return DEFAULT_BACK_OFF_RATE;
    }

    static /* synthetic */ int access$1000() {
        return DEFAULT_MAX_ENTRIES;
    }

    public static class Builder {
        private Clock clock = SystemClock.getInstance();
        private long initialExpiry = ExponentialBackOffFailureCache.access$700();
        private TimeUnit initialExpiryUnit = TimeUnit.MILLISECONDS;
        private long maxExpiry = ExponentialBackOffFailureCache.access$800();
        private TimeUnit maxExpiryUnit = TimeUnit.MILLISECONDS;
        private double backOffRate = ExponentialBackOffFailureCache.access$900();
        private int maxEntries = ExponentialBackOffFailureCache.access$1000();

        public <K> ExponentialBackOffFailureCache<K> build() {
            return new ExponentialBackOffFailureCache(this);
        }

        public Builder clock(Clock clock) {
            this.clock = clock;
            return this;
        }

        public Builder initialExpiry(long initialExpiry, TimeUnit unit) {
            this.initialExpiry = initialExpiry;
            this.initialExpiryUnit = unit;
            return this;
        }

        public Builder maxExpiry(long maxExpiry, TimeUnit unit) {
            this.maxExpiry = maxExpiry;
            this.maxExpiryUnit = unit;
            return this;
        }

        public Builder backOffRate(double backOffRate) {
            this.backOffRate = backOffRate;
            return this;
        }

        public Builder maxEntries(int maxEntries) {
            this.maxEntries = maxEntries;
            return this;
        }
    }
}

