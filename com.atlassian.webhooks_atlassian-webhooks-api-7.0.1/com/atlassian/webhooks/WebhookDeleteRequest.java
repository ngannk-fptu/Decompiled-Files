/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.AbstractBulkWebhookRequest;
import javax.annotation.Nonnull;

public class WebhookDeleteRequest
extends AbstractBulkWebhookRequest {
    WebhookDeleteRequest(Builder builder) {
        super(builder);
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(AbstractBulkWebhookRequest request) {
        return new Builder(request);
    }

    public static class Builder
    extends AbstractBulkWebhookRequest.AbstractBuilder<Builder> {
        private Builder() {
        }

        private Builder(@Nonnull AbstractBulkWebhookRequest request) {
            super(request);
        }

        @Nonnull
        public WebhookDeleteRequest build() {
            return new WebhookDeleteRequest(this);
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

