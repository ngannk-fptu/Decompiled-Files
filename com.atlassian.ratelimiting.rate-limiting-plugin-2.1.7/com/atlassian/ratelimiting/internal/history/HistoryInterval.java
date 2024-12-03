/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.ratelimiting.internal.history;

import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.ImmutableMap;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class HistoryInterval {
    private final Map<UserKey, AtomicLong> counters;
    private final Instant start;

    private HistoryInterval(Clock clock) {
        this.start = Instant.now(clock);
        this.counters = new ConcurrentHashMap<UserKey, AtomicLong>();
    }

    @Nonnull
    static HistoryInterval create(Clock clock) {
        return new HistoryInterval(clock);
    }

    @Nonnull
    public LocalDateTime getStart() {
        return LocalDateTime.ofInstant(this.start, ZoneOffset.UTC);
    }

    @Nonnull
    HistoryInterval onRateLimit(UserKey userId) {
        this.counters.computeIfAbsent(userId, id -> new AtomicLong(0L)).incrementAndGet();
        return this;
    }

    public CompletedHistoryInterval toCompletedHistoryInterval() {
        return new CompletedHistoryInterval(this);
    }

    public static class CompletedHistoryInterval {
        private final Map<UserKey, Long> counters;
        private final LocalDateTime start;

        public CompletedHistoryInterval(HistoryInterval historyInterval) {
            this.start = historyInterval.getStart();
            ImmutableMap.Builder builder = new ImmutableMap.Builder();
            historyInterval.counters.forEach((k, v) -> builder.put(k, (Object)v.get()));
            this.counters = builder.build();
        }

        public Map<UserKey, Long> getCounters() {
            return this.counters;
        }

        public LocalDateTime getStart() {
            return this.start;
        }
    }
}

