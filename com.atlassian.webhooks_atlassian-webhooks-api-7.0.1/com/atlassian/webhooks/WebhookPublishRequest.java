/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.validation.constraints.NotNull
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookCallback;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.util.BuilderUtil;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

public class WebhookPublishRequest {
    private final List<WebhookCallback> callbacks;
    private final WebhookEvent event;
    private final Object payload;
    private List<WebhookScope> scopes;
    private Webhook webhook;

    private WebhookPublishRequest(SearchBuilder searchBuilder) {
        this.callbacks = searchBuilder.callbacks;
        this.event = searchBuilder.event;
        this.scopes = searchBuilder.scopes;
        this.payload = searchBuilder.payload;
    }

    private WebhookPublishRequest(SingleWebhookBuilder singleWebhookBuilder) {
        this.callbacks = singleWebhookBuilder.callbacks;
        this.event = singleWebhookBuilder.event;
        this.payload = singleWebhookBuilder.payload;
        this.webhook = singleWebhookBuilder.webhook;
    }

    @Nonnull
    public static SearchBuilder builder(@Nonnull WebhookEvent event, @Nullable Object payload) {
        return new SearchBuilder(event, payload);
    }

    @Nonnull
    public static SingleWebhookBuilder builder(@Nonnull Webhook webhook, @Nonnull WebhookEvent event, @Nullable Object payload) {
        return new SingleWebhookBuilder(webhook, event, payload);
    }

    @Nonnull
    public List<WebhookCallback> getCallbacks() {
        return this.callbacks;
    }

    @NotNull
    public WebhookEvent getEvent() {
        return this.event;
    }

    @Nonnull
    public Optional<Object> getPayload() {
        return Optional.ofNullable(this.payload);
    }

    @Nonnull
    public List<WebhookScope> getScopes() {
        return this.scopes;
    }

    @Nonnull
    public Optional<Webhook> getWebhook() {
        return Optional.ofNullable(this.webhook);
    }

    public static class SingleWebhookBuilder {
        private final List<WebhookCallback> callbacks;
        private final WebhookEvent event;
        private final Object payload;
        private final Webhook webhook;

        public SingleWebhookBuilder(@Nonnull Webhook webhook, @Nonnull WebhookEvent event, @Nullable Object payload) {
            this.event = Objects.requireNonNull(event, "event");
            this.payload = payload;
            this.webhook = Objects.requireNonNull(webhook, "webhook");
            this.callbacks = new ArrayList<WebhookCallback>();
        }

        @Nonnull
        public WebhookPublishRequest build() {
            return new WebhookPublishRequest(this);
        }

        @Nonnull
        public SingleWebhookBuilder callback(@Nonnull WebhookCallback value, WebhookCallback ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.callbacks, value, values);
            return this;
        }
    }

    public static class SearchBuilder {
        private final List<WebhookCallback> callbacks = new ArrayList<WebhookCallback>();
        private final Object payload;
        private final List<WebhookScope> scopes;
        private WebhookEvent event;

        public SearchBuilder(@Nonnull WebhookEvent event, @Nullable Object payload) {
            this.event = Objects.requireNonNull(event, "event");
            this.payload = payload;
            this.scopes = new ArrayList<WebhookScope>();
        }

        @Nonnull
        public WebhookPublishRequest build() {
            return new WebhookPublishRequest(this);
        }

        @Nonnull
        public SearchBuilder callback(@Nonnull WebhookCallback value, WebhookCallback ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.callbacks, value, values);
            return this;
        }

        @Nonnull
        public SearchBuilder scopes(@Nonnull WebhookScope value, WebhookScope ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.scopes, value, values);
            return this;
        }
    }
}

