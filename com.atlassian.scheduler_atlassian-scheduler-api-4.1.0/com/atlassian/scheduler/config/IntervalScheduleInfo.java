/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.config;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.util.Safe;
import com.google.common.base.Preconditions;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class IntervalScheduleInfo {
    private final Date firstRunTime;
    private final long intervalInMillis;

    IntervalScheduleInfo(@Nullable Date firstRunTime, long intervalInMillis) {
        this.firstRunTime = Safe.copy(firstRunTime);
        this.intervalInMillis = intervalInMillis;
        Preconditions.checkArgument((intervalInMillis >= 0L ? 1 : 0) != 0, (Object)"intervalInMillis must not be negative");
    }

    @Nullable
    public Date getFirstRunTime() {
        return Safe.copy(this.firstRunTime);
    }

    public long getIntervalInMillis() {
        return this.intervalInMillis;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        IntervalScheduleInfo other = (IntervalScheduleInfo)o;
        return this.intervalInMillis == other.intervalInMillis && Objects.equals(this.firstRunTime, other.firstRunTime);
    }

    public int hashCode() {
        return Objects.hash(this.firstRunTime, this.intervalInMillis);
    }

    public String toString() {
        return "IntervalScheduleInfo[firstRunTime=" + this.firstRunTime + ",intervalInMillis=" + this.intervalInMillis + ']';
    }
}

