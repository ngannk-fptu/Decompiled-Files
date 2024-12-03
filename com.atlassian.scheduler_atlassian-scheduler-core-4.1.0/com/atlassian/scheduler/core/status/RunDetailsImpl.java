/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.scheduler.status.RunDetails
 *  com.atlassian.scheduler.status.RunOutcome
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler.core.status;

import com.atlassian.scheduler.status.RunDetails;
import com.atlassian.scheduler.status.RunOutcome;
import java.util.Date;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@Immutable
public final class RunDetailsImpl
implements RunDetails {
    private final long startTime;
    private final RunOutcome runOutcome;
    private final long durationInMillis;
    private final String message;

    public RunDetailsImpl(Date startTime, RunOutcome runOutcome, long durationInMillis, @Nullable String message) {
        this.startTime = Objects.requireNonNull(startTime, "startTime").getTime();
        this.runOutcome = Objects.requireNonNull(runOutcome, "runOutcome");
        this.durationInMillis = durationInMillis >= 0L ? durationInMillis : 0L;
        this.message = RunDetailsImpl.truncate(message);
    }

    @Nonnull
    public Date getStartTime() {
        return new Date(this.startTime);
    }

    public long getDurationInMillis() {
        return this.durationInMillis;
    }

    @Nonnull
    public RunOutcome getRunOutcome() {
        return this.runOutcome;
    }

    @Nonnull
    public String getMessage() {
        return this.message;
    }

    public String toString() {
        return "RunDetailsImpl[startTime=" + this.startTime + ",runOutcome=" + this.runOutcome + ",durationInMillis=" + this.durationInMillis + ",message=" + this.message + ']';
    }

    private static String truncate(@Nullable String message) {
        if (message == null) {
            return "";
        }
        if (message.length() > 255) {
            return message.substring(0, 255);
        }
        return message;
    }
}

