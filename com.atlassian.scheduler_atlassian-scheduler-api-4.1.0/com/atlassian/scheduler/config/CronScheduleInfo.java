/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.config;

import com.atlassian.annotations.PublicApi;
import java.util.Objects;
import java.util.TimeZone;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
@PublicApi
public final class CronScheduleInfo {
    private final String cronExpression;
    private final TimeZone timeZone;

    CronScheduleInfo(String cronExpression, @Nullable TimeZone timeZone) {
        this.cronExpression = Objects.requireNonNull(cronExpression, "cronExpression");
        this.timeZone = timeZone;
    }

    @Nonnull
    public String getCronExpression() {
        return this.cronExpression;
    }

    @Nullable
    public TimeZone getTimeZone() {
        return this.timeZone;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CronScheduleInfo other = (CronScheduleInfo)o;
        return this.cronExpression.equals(other.cronExpression) && Objects.equals(this.timeZone, other.timeZone);
    }

    public int hashCode() {
        return Objects.hash(this.cronExpression, this.timeZone);
    }

    public String toString() {
        String timeZoneId = this.timeZone != null ? this.timeZone.getID() : null;
        return "CronScheduleInfo[cronExpression='" + this.cronExpression + "',timeZone=" + timeZoneId + ']';
    }
}

