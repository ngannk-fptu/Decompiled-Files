/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.history;

import javax.annotation.Nonnull;

public class InvocationHistoryByEventRequest {
    private final int webhookId;

    private InvocationHistoryByEventRequest(Builder builder) {
        this.webhookId = builder.webhookId;
    }

    @Nonnull
    public static Builder builder(int webhookId) {
        return new Builder(webhookId);
    }

    public int getWebhookId() {
        return this.webhookId;
    }

    public static class Builder {
        private final int webhookId;

        public Builder(int webhookId) {
            this.webhookId = webhookId;
        }

        @Nonnull
        public InvocationHistoryByEventRequest build() {
            return new InvocationHistoryByEventRequest(this);
        }
    }
}

