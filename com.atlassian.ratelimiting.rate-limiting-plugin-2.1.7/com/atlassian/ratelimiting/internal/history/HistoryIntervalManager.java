/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.Nonnull
 *  javax.annotation.concurrent.ThreadSafe
 */
package com.atlassian.ratelimiting.internal.history;

import com.atlassian.ratelimiting.internal.history.HistoryInterval;
import com.atlassian.sal.api.user.UserKey;
import java.time.Clock;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public class HistoryIntervalManager {
    private Clock clock;
    private HistoryInterval interval;

    public HistoryIntervalManager() {
        this(Clock.systemDefaultZone());
    }

    public HistoryIntervalManager(Clock clock) {
        this.clock = clock;
        this.interval = HistoryInterval.create(clock);
    }

    @Nonnull
    public HistoryInterval.CompletedHistoryInterval collect() {
        HistoryInterval collectedInterval = this.swap(this.interval);
        return collectedInterval.toCompletedHistoryInterval();
    }

    public void onReject(@Nonnull UserKey user) {
        UserKey userId = Objects.requireNonNull(user, "user");
        this.interval.onRateLimit(userId);
    }

    private HistoryInterval swap(HistoryInterval original) {
        this.interval = HistoryInterval.create(this.clock);
        return original;
    }
}

