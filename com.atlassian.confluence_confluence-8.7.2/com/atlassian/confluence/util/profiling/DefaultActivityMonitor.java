/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.util.concurrent.Supplier
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.base.Preconditions
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.util.profiling;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.util.profiling.Activity;
import com.atlassian.confluence.util.profiling.ActivityMonitor;
import com.atlassian.confluence.util.profiling.ActivitySnapshot;
import com.atlassian.util.concurrent.Supplier;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
@Internal
public class DefaultActivityMonitor
implements ActivityMonitor {
    private static final Logger log = LoggerFactory.getLogger(DefaultActivityMonitor.class);
    private static final long DEFAULT_THRESHOLD_MS = 60000L;
    private final ConcurrentMap<Long, ActivitySnapshot> currentActivity = new ConcurrentHashMap<Long, ActivitySnapshot>();
    private final Supplier<Long> keyGenerator;
    private final long thresholdMs;

    public DefaultActivityMonitor() {
        this(60000L, (Supplier<Long>)((Supplier)() -> Thread.currentThread().getId()));
    }

    @VisibleForTesting
    DefaultActivityMonitor(long thresholdMs, Supplier<Long> keyGenerator) {
        Preconditions.checkArgument((thresholdMs > 0L ? 1 : 0) != 0, (Object)("thresholdMs must be greater than zero, passed " + thresholdMs));
        this.thresholdMs = thresholdMs;
        this.keyGenerator = (Supplier)Preconditions.checkNotNull(keyGenerator);
    }

    @Override
    public @NonNull Activity registerStart(String userId, String type, String summary) {
        ActivitySnapshot snapshot = new ActivitySnapshot(System.currentTimeMillis(), Thread.currentThread().getId(), Thread.currentThread().getName(), userId, type, summary);
        Long key = (Long)Preconditions.checkNotNull((Object)((Long)this.keyGenerator.get()));
        ActivitySnapshot previous = this.currentActivity.put(key, snapshot);
        if (previous != null) {
            log.warn("Overriding a previous entry {} with {}.", (Object)previous, (Object)snapshot);
        }
        return new MyActivity(key);
    }

    @Override
    public @NonNull Collection<ActivitySnapshot> snapshotCurrent() {
        return Collections.unmodifiableCollection(this.currentActivity.values());
    }

    private class MyActivity
    implements Activity {
        private final Long key;

        private MyActivity(Long key) {
            this.key = (Long)Preconditions.checkNotNull((Object)key);
        }

        @Override
        public void close() {
            ActivitySnapshot previous = (ActivitySnapshot)DefaultActivityMonitor.this.currentActivity.remove(this.key);
            if (previous == null) {
                log.warn("Unable to register a finish for thread {}", (Object)this.key);
            } else if (System.currentTimeMillis() - previous.getStartTime() > DefaultActivityMonitor.this.thresholdMs) {
                log.warn("Exceeded the threshold of {} ms: {}", (Object)DefaultActivityMonitor.this.thresholdMs, (Object)previous);
            }
        }
    }
}

