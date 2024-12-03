/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.AbstractWebhookRequest;
import com.atlassian.webhooks.Webhook;
import java.util.Objects;
import javax.annotation.Nonnull;

public class WebhookUpdateRequest
extends AbstractWebhookRequest {
    private WebhookUpdateRequest(Builder builder) {
        super(builder);
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public static Builder builder(@Nonnull Webhook webhook) {
        return new Builder(webhook);
    }

    public static class Builder
    extends AbstractWebhookRequest.AbstractBuilder<Builder> {
        private Builder() {
        }

        private Builder(@Nonnull Webhook webhook) {
            ((Builder)((Builder)((Builder)((Builder)((Builder)this.active(Objects.requireNonNull(webhook, "webhook").isActive())).configuration(webhook.getConfiguration())).event(webhook.getEvents())).name(webhook.getName())).scope(webhook.getScope())).url(webhook.getUrl());
        }

        @Nonnull
        public WebhookUpdateRequest build() {
            return new WebhookUpdateRequest(this);
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

