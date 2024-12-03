/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.persistence.Column
 *  javax.persistence.Embeddable
 *  javax.persistence.EnumType
 *  javax.persistence.Enumerated
 *  lombok.Generated
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.migration.agent.entity;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.migration.agent.entity.ExecutionStatus;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Generated;
import org.apache.commons.lang.StringUtils;

@Embeddable
public class Progress {
    private static final int MAX_MESSAGE_LENGTH = 2000;
    private static final int MAX_RESULT_LENGTH = 4000;
    private static final int MAX_STATUS_LENGTH = 2000;
    @Column(name="startTime")
    private Instant startTime;
    @Column(name="endTime")
    private Instant endTime;
    @Column(name="executionStatus")
    @Enumerated(value=EnumType.STRING)
    private ExecutionStatus status;
    @Column(name="message", length=2000)
    private String message;
    @Column(name="completionPercent", nullable=false)
    private int percent;
    @Column(name="doneResult", length=4000)
    private String result;
    @Column(name="detailedStatus", length=2000)
    private String detailedStatus;

    private Progress() {
    }

    @VisibleForTesting
    public Progress(@Nullable Instant startTime, @Nullable Instant endTime, ExecutionStatus status, @Nullable String message, int percent, @Nullable String result, @Nullable String detailedStatus) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.message = Progress.cleanStringValue(message, 2000);
        this.percent = percent;
        this.result = Progress.cleanStringValue(result, 4000);
        this.detailedStatus = Progress.cleanStringValue(detailedStatus, 2000);
    }

    public int getPercent() {
        return this.percent;
    }

    @Nonnull
    public Optional<Instant> getStartTime() {
        return Optional.ofNullable(this.startTime);
    }

    @Nonnull
    public Optional<Instant> getEndTime() {
        return Optional.ofNullable(this.endTime);
    }

    @Nonnull
    public ExecutionStatus getStatus() {
        return this.status;
    }

    @Nonnull
    public String getMessage() {
        return this.message;
    }

    @Nonnull
    public String getDetailedStatus() {
        return this.detailedStatus;
    }

    @Nonnull
    public Optional<String> getResult() {
        return Optional.ofNullable(this.result);
    }

    @Nonnull
    public Copier copy() {
        return new Copier(this);
    }

    public long getElapsed() {
        if (this.startTime != null && this.endTime != null) {
            return Duration.between(this.startTime, this.endTime).toMillis();
        }
        return 0L;
    }

    @Nonnull
    public static Progress created() {
        return new Progress(null, null, ExecutionStatus.CREATED, null, 0, null, null);
    }

    private static String cleanStringValue(@Nullable String message, int maxLength) {
        if (StringUtils.isBlank((String)message)) {
            return null;
        }
        return StringUtils.abbreviate((String)message, (int)maxLength);
    }

    @Generated
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Progress)) {
            return false;
        }
        Progress other = (Progress)o;
        if (!other.canEqual(this)) {
            return false;
        }
        Optional<Instant> this$startTime = this.getStartTime();
        Optional<Instant> other$startTime = other.getStartTime();
        if (this$startTime == null ? other$startTime != null : !((Object)this$startTime).equals(other$startTime)) {
            return false;
        }
        Optional<Instant> this$endTime = this.getEndTime();
        Optional<Instant> other$endTime = other.getEndTime();
        if (this$endTime == null ? other$endTime != null : !((Object)this$endTime).equals(other$endTime)) {
            return false;
        }
        ExecutionStatus this$status = this.getStatus();
        ExecutionStatus other$status = other.getStatus();
        if (this$status == null ? other$status != null : !((Object)((Object)this$status)).equals((Object)other$status)) {
            return false;
        }
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        if (this$message == null ? other$message != null : !this$message.equals(other$message)) {
            return false;
        }
        if (this.getPercent() != other.getPercent()) {
            return false;
        }
        Optional<String> this$result = this.getResult();
        Optional<String> other$result = other.getResult();
        if (this$result == null ? other$result != null : !((Object)this$result).equals(other$result)) {
            return false;
        }
        String this$detailedStatus = this.getDetailedStatus();
        String other$detailedStatus = other.getDetailedStatus();
        return !(this$detailedStatus == null ? other$detailedStatus != null : !this$detailedStatus.equals(other$detailedStatus));
    }

    @Generated
    protected boolean canEqual(Object other) {
        return other instanceof Progress;
    }

    @Generated
    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Optional<Instant> $startTime = this.getStartTime();
        result = result * 59 + ($startTime == null ? 43 : ((Object)$startTime).hashCode());
        Optional<Instant> $endTime = this.getEndTime();
        result = result * 59 + ($endTime == null ? 43 : ((Object)$endTime).hashCode());
        ExecutionStatus $status = this.getStatus();
        result = result * 59 + ($status == null ? 43 : ((Object)((Object)$status)).hashCode());
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        result = result * 59 + this.getPercent();
        Optional<String> $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : ((Object)$result).hashCode());
        String $detailedStatus = this.getDetailedStatus();
        result = result * 59 + ($detailedStatus == null ? 43 : $detailedStatus.hashCode());
        return result;
    }

    public static final class Copier {
        private final Progress from;

        private Copier(Progress from) {
            this.from = from;
        }

        @Nonnull
        public Progress started() {
            Copier.checkCanGo(this.from.status, ExecutionStatus.RUNNING);
            return new Progress(this.from.startTime, null, ExecutionStatus.RUNNING, null, this.from.percent, this.from.result, "Started");
        }

        @Nonnull
        public Progress started(@Nullable String message) {
            Copier.checkCanGo(this.from.status, ExecutionStatus.RUNNING);
            return new Progress(Instant.now(), null, ExecutionStatus.RUNNING, message, this.from.percent, this.from.result, null);
        }

        @Nonnull
        public Progress done() {
            return this.done(null, null);
        }

        @Nonnull
        public Progress done(@Nullable String message, @Nullable String result) {
            Copier.checkCanGo(this.from.status, ExecutionStatus.DONE);
            return new Progress(this.from.startTime, Instant.now(), ExecutionStatus.DONE, message, 100, result, null);
        }

        @Nonnull
        public Progress updatePercent(int percent) {
            return this.progress(percent, null, this.from.detailedStatus);
        }

        @Nonnull
        public Progress updateDetailedStatus(String detailedStatus) {
            return this.progress(this.from.percent, this.from.message, detailedStatus);
        }

        @Nonnull
        public Progress progress(int percent, @Nullable String message, @Nullable String detailedStatus) {
            return new Progress(this.from.startTime, this.from.endTime, this.from.status, message, percent, this.from.result, detailedStatus);
        }

        @Nonnull
        public Progress progress(int percent, @Nullable String message) {
            return new Progress(this.from.startTime, this.from.endTime, this.from.status, message, percent, this.from.result, this.from.detailedStatus);
        }

        @Nonnull
        public Progress failed(String message) {
            Objects.requireNonNull(message);
            Copier.checkCanGo(this.from.status, ExecutionStatus.FAILED);
            return new Progress(this.from.startTime, Instant.now(), ExecutionStatus.FAILED, message, this.from.percent, this.from.result, null);
        }

        @Nonnull
        public Progress incomplete(String message) {
            Objects.requireNonNull(message);
            Copier.checkCanGo(this.from.status, ExecutionStatus.INCOMPLETE);
            return new Progress(this.from.startTime, Instant.now(), ExecutionStatus.INCOMPLETE, message, this.from.percent, this.from.result, null);
        }

        @Nonnull
        public Progress stopping() {
            Copier.checkCanGo(this.from.status, ExecutionStatus.STOPPING);
            return new Progress(this.from.startTime, Instant.now(), ExecutionStatus.STOPPING, null, this.from.percent, this.from.result, null);
        }

        @Nonnull
        public Progress stopped() {
            Copier.checkCanGo(this.from.status, ExecutionStatus.STOPPED);
            return new Progress(this.from.startTime, Instant.now(), ExecutionStatus.STOPPED, null, this.from.percent, this.from.result, null);
        }

        private static void checkCanGo(ExecutionStatus from, ExecutionStatus to) {
            if (!from.canGo(to)) {
                throw new IllegalStateException(String.format("Can not make transition from %s to %s", new Object[]{from, to}));
            }
        }

        @Nonnull
        public Progress validating() {
            Copier.checkCanGo(this.from.status, ExecutionStatus.VALIDATING);
            return new Progress(Instant.now(), this.from.endTime, ExecutionStatus.VALIDATING, null, this.from.percent, this.from.result, "Checking for errors");
        }
    }
}

