/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.concurrent.Immutable
 */
package com.atlassian.scheduler;

import com.atlassian.annotations.PublicApi;
import com.atlassian.scheduler.status.RunOutcome;
import com.google.common.base.Preconditions;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

@PublicApi
@Immutable
public final class JobRunnerResponse {
    private final RunOutcome runOutcome;
    private final String message;

    public static JobRunnerResponse success() {
        return JobRunnerResponse.success(null);
    }

    public static JobRunnerResponse success(@Nullable String message) {
        return new JobRunnerResponse(RunOutcome.SUCCESS, message);
    }

    public static JobRunnerResponse aborted(String message) {
        Preconditions.checkArgument((boolean)JobRunnerResponse.isNotBlank(message), (Object)"The message must be specified when reporting a job as aborted!");
        return new JobRunnerResponse(RunOutcome.ABORTED, message);
    }

    public static JobRunnerResponse failed(String message) {
        Preconditions.checkArgument((boolean)JobRunnerResponse.isNotBlank(message), (Object)"The message must be specified when reporting a job as failed!");
        return new JobRunnerResponse(RunOutcome.FAILED, message);
    }

    public static JobRunnerResponse failed(Throwable cause) {
        return new JobRunnerResponse(RunOutcome.FAILED, JobRunnerResponse.toMessage(Objects.requireNonNull(cause, "cause")));
    }

    private JobRunnerResponse(RunOutcome runOutcome, @Nullable String message) {
        this.runOutcome = runOutcome;
        this.message = message;
    }

    @Nonnull
    public RunOutcome getRunOutcome() {
        return this.runOutcome;
    }

    @Nullable
    public String getMessage() {
        return this.message;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        JobRunnerResponse other = (JobRunnerResponse)o;
        return this.runOutcome == other.runOutcome && Objects.equals(this.message, other.message);
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.runOutcome, this.message});
    }

    public String toString() {
        return "JobRunnerResponse[runOutcome=" + (Object)((Object)this.runOutcome) + ",message='" + this.message + "']";
    }

    private static boolean isNotBlank(@Nullable String message) {
        return message != null && !message.trim().isEmpty();
    }

    private static String toMessage(Throwable e) {
        StringBuilder message = new StringBuilder(255);
        JobRunnerResponse.appendShortForm(message, e);
        for (Throwable cause = e.getCause(); message.length() < 255 && cause != null; cause = cause.getCause()) {
            message.append('\n');
            JobRunnerResponse.appendShortForm(message, cause);
        }
        if (message.length() > 255) {
            message.setLength(255);
        }
        return message.toString();
    }

    private static void appendShortForm(StringBuilder sb, Throwable e) {
        sb.append(e.getClass().getSimpleName());
        String msg = e.getMessage();
        if (msg != null) {
            sb.append(": ").append(msg);
        }
    }
}

