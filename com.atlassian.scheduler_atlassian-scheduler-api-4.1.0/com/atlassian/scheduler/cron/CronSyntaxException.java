/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.MoreObjects
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.scheduler.cron;

import com.atlassian.scheduler.SchedulerServiceException;
import com.atlassian.scheduler.cron.ErrorCode;
import com.google.common.base.MoreObjects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CronSyntaxException
extends SchedulerServiceException {
    private static final long serialVersionUID = 5594187147397941674L;
    private final ErrorCode errorCode;
    private final String cronExpression;
    private final String value;
    private final int errorOffset;

    CronSyntaxException(Builder builder) {
        super(builder.toMessage());
        this.errorCode = (ErrorCode)((Object)MoreObjects.firstNonNull((Object)((Object)builder.errorCode), (Object)((Object)ErrorCode.INTERNAL_PARSER_FAILURE)));
        this.cronExpression = (String)MoreObjects.firstNonNull((Object)builder.cronExpression, (Object)"");
        this.value = builder.value;
        this.errorOffset = builder.errorOffset;
        if (builder.cause != null) {
            this.initCause(builder.cause);
        }
    }

    @Nonnull
    public ErrorCode getErrorCode() {
        return this.errorCode;
    }

    @Nonnull
    public String getCronExpression() {
        return this.cronExpression;
    }

    @Nullable
    public String getValue() {
        return this.value;
    }

    public int getErrorOffset() {
        return this.errorOffset;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        String cronExpression;
        String value;
        ErrorCode errorCode;
        int errorOffset = -1;
        Throwable cause;

        Builder() {
        }

        public Builder cronExpression(@Nullable String cronExpression) {
            this.cronExpression = cronExpression;
            return this;
        }

        public Builder value(@Nullable String value) {
            this.value = value;
            return this;
        }

        public Builder value(char value) {
            this.value = String.valueOf(value);
            return this;
        }

        public Builder errorCode(@Nullable ErrorCode errorCode) {
            this.errorCode = errorCode;
            return this;
        }

        public Builder errorOffset(int errorOffset) {
            this.errorOffset = errorOffset >= 0 ? errorOffset : -1;
            return this;
        }

        public Builder cause(@Nullable Throwable cause) {
            this.cause = cause;
            return this;
        }

        public CronSyntaxException build() {
            return new CronSyntaxException(this);
        }

        @Nonnull
        String toMessage() {
            return this.errorCode.toMessage(this.value);
        }
    }
}

