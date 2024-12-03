/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.diagnostics;

import com.atlassian.diagnostics.AlertTrigger;
import com.atlassian.diagnostics.Issue;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AlertRequest {
    private final Supplier<Object> detailsSupplier;
    private final Issue issue;
    private final Instant timestamp;
    private final AlertTrigger trigger;

    private AlertRequest(Builder builder) {
        this.issue = builder.issue;
        this.detailsSupplier = builder.detailsSupplier;
        this.timestamp = builder.timestamp;
        this.trigger = builder.trigger;
    }

    @Nonnull
    public static Builder builder(@Nonnull Issue issue) {
        return new Builder(issue);
    }

    @Nonnull
    public Optional<Supplier<Object>> getDetailsSupplier() {
        return Optional.ofNullable(this.detailsSupplier);
    }

    @Nonnull
    public Issue getIssue() {
        return this.issue;
    }

    @Nonnull
    public Instant getTimestamp() {
        return this.timestamp;
    }

    @Nonnull
    public Optional<AlertTrigger> getTrigger() {
        return Optional.ofNullable(this.trigger);
    }

    public static class Builder {
        private final Issue issue;
        private Supplier<Object> detailsSupplier;
        private Instant timestamp;
        private AlertTrigger trigger;

        public Builder(@Nonnull Issue issue) {
            this.issue = Objects.requireNonNull(issue, "issue");
            this.timestamp = Instant.now();
        }

        @Nonnull
        public AlertRequest build() {
            return new AlertRequest(this);
        }

        @Nonnull
        public Builder details(@Nullable Supplier<Object> value) {
            this.detailsSupplier = value;
            return this;
        }

        @Nonnull
        public Builder timestamp(@Nullable Instant timestamp) {
            if (timestamp != null) {
                this.timestamp = timestamp;
            }
            return this;
        }

        @Nonnull
        public Builder trigger(@Nullable AlertTrigger value) {
            this.trigger = value;
            return this;
        }
    }
}

