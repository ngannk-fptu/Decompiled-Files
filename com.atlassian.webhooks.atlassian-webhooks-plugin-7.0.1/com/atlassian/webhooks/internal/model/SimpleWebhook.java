/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookScope
 *  com.atlassian.webhooks.util.BuilderUtil
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.model;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.util.BuilderUtil;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;

public class SimpleWebhook
implements Webhook {
    private final boolean active;
    private final Map<String, String> configuration;
    private final Date createdDate;
    private final Set<WebhookEvent> events;
    private final int id;
    private final String name;
    private final WebhookScope scope;
    private final Date updatedDate;
    private final String url;

    private SimpleWebhook(Builder builder) {
        this.active = builder.active;
        this.configuration = builder.configuration;
        this.createdDate = Objects.requireNonNull(builder.createdDate, "updatedDate");
        this.events = builder.events;
        this.id = builder.id;
        this.name = builder.name;
        this.scope = Objects.requireNonNull(builder.scope, "scope");
        this.updatedDate = Objects.requireNonNull(builder.updatedDate, "updatedDate");
        this.url = builder.url;
    }

    @Nonnull
    public static Builder builder() {
        return new Builder();
    }

    @Nonnull
    public Map<String, String> getConfiguration() {
        return this.configuration;
    }

    @Nonnull
    public Date getCreatedDate() {
        return this.createdDate;
    }

    @Nonnull
    public Set<WebhookEvent> getEvents() {
        return this.events;
    }

    public int getId() {
        return this.id;
    }

    @Nonnull
    public String getName() {
        return this.name;
    }

    @Nonnull
    public WebhookScope getScope() {
        return this.scope;
    }

    @Nonnull
    public Date getUpdatedDate() {
        return this.updatedDate;
    }

    @Nonnull
    public String getUrl() {
        return this.url;
    }

    public boolean isActive() {
        return this.active;
    }

    public static class Builder {
        private final Map<String, String> configuration = new HashMap<String, String>();
        private final Set<WebhookEvent> events;
        private boolean active = true;
        private Date createdDate = new Date();
        private int id;
        private String name;
        private WebhookScope scope;
        private Date updatedDate;
        private String url;

        private Builder() {
            this.events = new HashSet<WebhookEvent>();
            this.scope = WebhookScope.GLOBAL;
            this.updatedDate = this.createdDate;
        }

        @Nonnull
        public Webhook build() {
            return new SimpleWebhook(this);
        }

        @Nonnull
        public Builder configuration(@Nonnull Map<String, String> value) {
            this.configuration.putAll(Objects.requireNonNull(value, "configuration"));
            return this;
        }

        @Nonnull
        public Builder createdDate(@Nonnull Date value) {
            this.createdDate = Objects.requireNonNull(value, "createdDate");
            return this;
        }

        @Nonnull
        public Builder active(boolean value) {
            this.active = value;
            return this;
        }

        @Nonnull
        public Builder event(@Nonnull Iterable<WebhookEvent> value) {
            BuilderUtil.addIf(Objects::nonNull, this.events, value);
            return this;
        }

        @Nonnull
        public Builder event(@Nonnull WebhookEvent value, WebhookEvent ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.events, (Object)value, (Object[])values);
            return this;
        }

        @Nonnull
        public Builder id(int value) {
            this.id = value;
            return this;
        }

        @Nonnull
        public Builder name(@Nonnull String value) {
            Objects.requireNonNull(value, "value");
            this.name = value;
            return this;
        }

        @Nonnull
        public Builder scope(@Nonnull WebhookScope value) {
            this.scope = Objects.requireNonNull(value, "scope");
            return this;
        }

        @Nonnull
        public Builder url(@Nonnull String value) {
            Objects.requireNonNull(value);
            this.url = value;
            return this;
        }

        @Nonnull
        public Builder updatedDate(@Nonnull Date value) {
            this.updatedDate = Objects.requireNonNull(value, "updatedDate");
            return this;
        }
    }
}

