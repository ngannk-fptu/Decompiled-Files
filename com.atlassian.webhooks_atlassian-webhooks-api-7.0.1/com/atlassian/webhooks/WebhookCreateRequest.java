/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.AbstractWebhookRequest;
import com.atlassian.webhooks.WebhookScope;
import javax.annotation.Nonnull;

public class WebhookCreateRequest
extends AbstractWebhookRequest {
    private WebhookCreateRequest(Builder builder) {
        super(builder);
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder
    extends AbstractWebhookRequest.AbstractBuilder<Builder> {
        private Builder() {
            this.scope(WebhookScope.GLOBAL);
        }

        @Nonnull
        public WebhookCreateRequest build() {
            return new WebhookCreateRequest(this);
        }

        @Override
        @Nonnull
        protected Builder self() {
            return this;
        }
    }
}

