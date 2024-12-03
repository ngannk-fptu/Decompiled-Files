/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.history;

import com.atlassian.webhooks.history.InvocationOutcome;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HistoricalInvocationRequest {
    private final String eventId;
    private final Set<InvocationOutcome> outcomes;
    private final int webhookId;

    private HistoricalInvocationRequest(Builder builder) {
        this.eventId = builder.eventId;
        this.outcomes = Collections.unmodifiableSet(new HashSet(builder.outcomes));
        this.webhookId = builder.webhookId;
    }

    @Nonnull
    public static Builder builder(int webhookId) {
        return new Builder(webhookId);
    }

    @Nonnull
    public Optional<String> getEventId() {
        return Optional.ofNullable(this.eventId);
    }

    @Nonnull
    public Collection<InvocationOutcome> getOutcomes() {
        return this.outcomes;
    }

    public int getWebhookId() {
        return this.webhookId;
    }

    public static class Builder {
        private final Set<InvocationOutcome> outcomes;
        private final int webhookId;
        private String eventId;

        public Builder(int webhookId) {
            this.webhookId = webhookId;
            this.outcomes = new HashSet<InvocationOutcome>();
        }

        @Nonnull
        public HistoricalInvocationRequest build() {
            return new HistoricalInvocationRequest(this);
        }

        @Nonnull
        public Builder eventId(@Nullable String value) {
            this.eventId = value;
            return this;
        }

        @Nonnull
        public Builder outcome(@Nullable Collection<InvocationOutcome> values) {
            if (values != null) {
                values.stream().filter(Objects::nonNull).forEach(this.outcomes::add);
            }
            return this;
        }

        @Nonnull
        public Builder outcome(@Nullable InvocationOutcome value, InvocationOutcome ... moreValues) {
            if (value != null) {
                this.outcomes.add(value);
            }
            if (moreValues != null) {
                Arrays.stream(moreValues).filter(Objects::nonNull).forEach(this.outcomes::add);
            }
            return this;
        }
    }
}

