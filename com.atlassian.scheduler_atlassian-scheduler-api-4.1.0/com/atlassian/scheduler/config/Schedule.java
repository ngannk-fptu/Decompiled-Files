/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  com.google.common.base.Predicate
 *  com.google.common.base.Predicates
 *  com.google.common.collect.Iterables
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.config;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.config.CronScheduleInfo;
import com.atlassian.scheduler.config.IntervalScheduleInfo;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.TimeZone;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class Schedule {
    private final Type type;
    private final IntervalScheduleInfo intervalScheduleInfo;
    private final CronScheduleInfo cronScheduleInfo;

    public static Schedule forCronExpression(String cronExpression) {
        return Schedule.forCronExpression(cronExpression, null);
    }

    public static Schedule forCronExpression(String cronExpression, @Nullable TimeZone timeZone) {
        return new Schedule(Type.CRON_EXPRESSION, null, new CronScheduleInfo(cronExpression, timeZone));
    }

    public static Schedule runOnce(@Nullable Date runTime) {
        return Schedule.forInterval(0L, runTime);
    }

    public static Schedule forInterval(long intervalInMillis, @Nullable Date firstRunTime) {
        return new Schedule(Type.INTERVAL, new IntervalScheduleInfo(firstRunTime, intervalInMillis), null);
    }

    private Schedule(Type type, @Nullable IntervalScheduleInfo intervalScheduleInfo, @Nullable CronScheduleInfo cronScheduleInfo) {
        this.type = Objects.requireNonNull(type, "type");
        this.intervalScheduleInfo = intervalScheduleInfo;
        this.cronScheduleInfo = cronScheduleInfo;
        Preconditions.checkArgument((Schedule.countNulls(intervalScheduleInfo, cronScheduleInfo) == 1 ? 1 : 0) != 0, (Object)"Exactly one of the schedule formats must be non-null");
    }

    private static int countNulls(Object ... schedules) {
        return Iterables.size((Iterable)Iterables.filter(Arrays.asList(schedules), (Predicate)Predicates.notNull()));
    }

    public IntervalScheduleInfo getIntervalScheduleInfo() {
        return this.intervalScheduleInfo;
    }

    public CronScheduleInfo getCronScheduleInfo() {
        return this.cronScheduleInfo;
    }

    public Type getType() {
        return this.type;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Schedule other = (Schedule)o;
        return this.type == other.type && Objects.equals(this.intervalScheduleInfo, other.intervalScheduleInfo) && Objects.equals(this.cronScheduleInfo, other.cronScheduleInfo);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.type, this.intervalScheduleInfo, this.cronScheduleInfo});
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128).append("Schedule[type=").append((Object)this.type);
        switch (this.type) {
            case CRON_EXPRESSION: {
                sb.append(",cronScheduleInfo=").append(this.cronScheduleInfo);
                break;
            }
            case INTERVAL: {
                sb.append(",intervalScheduleInfo=").append(this.intervalScheduleInfo);
            }
        }
        return sb.append(']').toString();
    }

    public static enum Type {
        CRON_EXPRESSION,
        INTERVAL;

    }
}

