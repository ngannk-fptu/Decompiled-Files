/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.validation.constraints.NotNull
 *  org.hibernate.validator.constraints.URL
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookScope;
import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

public class PingRequest {
    private final String url;
    private final WebhookScope scope;

    private PingRequest(Builder builder) {
        this.url = builder.url;
        this.scope = builder.scope;
    }

    @Nonnull
    public static Builder builder(@Nonnull String url) {
        return new Builder(url);
    }

    @Nonnull
    public static Builder builder(@Nonnull Webhook webhook) {
        return new Builder(webhook.getUrl()).scope(webhook.getScope());
    }

    @NotNull
    @URL
    public String getUrl() {
        return this.url;
    }

    @NotNull
    public WebhookScope getScope() {
        return this.scope;
    }

    public static class Builder {
        private final String url;
        private WebhookScope scope;

        public Builder(@Nonnull String url) {
            this.url = url;
            this.scope = WebhookScope.GLOBAL;
        }

        @Nonnull
        public Builder scope(@Nonnull WebhookScope scope) {
            this.scope = scope;
            return this;
        }

        @Nonnull
        public PingRequest build() {
            return new PingRequest(this);
        }
    }
}

