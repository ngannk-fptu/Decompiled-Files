/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.history;

import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class InvocationHistoryRequest {
    private final String eventId;
    private final int webhookId;

    private InvocationHistoryRequest(Builder builder) {
        this.eventId = builder.eventId;
        this.webhookId = builder.webhookId;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public Optional<String> getEventId() {
        return Optional.ofNullable(this.eventId);
    }

    public int getWebhookId() {
        return this.webhookId;
    }

    public static class Builder {
        private String eventId;
        private int webhookId;

        @Nonnull
        public InvocationHistoryRequest build() {
            return new InvocationHistoryRequest(this);
        }

        @Nonnull
        public Builder eventId(@Nullable String value) {
            this.eventId = value;
            return this;
        }

        @Nonnull
        public Builder webhookId(int id) {
            this.webhookId = id;
            return this;
        }
    }
}

