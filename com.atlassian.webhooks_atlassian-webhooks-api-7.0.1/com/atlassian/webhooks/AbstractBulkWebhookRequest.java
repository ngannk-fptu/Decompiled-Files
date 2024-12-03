/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.util.BuilderUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractBulkWebhookRequest {
    private final Boolean active;
    private final List<WebhookEvent> events;
    private final Integer id;
    private final String name;
    private final List<String> scopeTypes;
    private final List<WebhookScope> scopes;

    AbstractBulkWebhookRequest(AbstractBuilder<?> builder) {
        this.active = ((AbstractBuilder)builder).active;
        this.events = Collections.unmodifiableList(new ArrayList(((AbstractBuilder)builder).events));
        this.id = ((AbstractBuilder)builder).id;
        this.name = ((AbstractBuilder)builder).name;
        this.scopes = Collections.unmodifiableList(new ArrayList(((AbstractBuilder)builder).scopes));
        this.scopeTypes = Collections.unmodifiableList(new ArrayList(((AbstractBuilder)builder).scopeTypes));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        AbstractBulkWebhookRequest that = (AbstractBulkWebhookRequest)o;
        return Objects.equals(this.getActive(), that.getActive()) && Objects.equals(this.getEvents(), that.getEvents()) && Objects.equals(this.getId(), that.getId()) && Objects.equals(this.getName(), that.getName()) && Objects.equals(this.scopes, that.scopes) && Objects.equals(this.scopeTypes, that.scopeTypes);
    }

    @Nullable
    public Boolean getActive() {
        return this.active;
    }

    @Nonnull
    public List<WebhookEvent> getEvents() {
        return this.events;
    }

    @Nullable
    public Integer getId() {
        return this.id;
    }

    @Nullable
    public String getName() {
        return this.name;
    }

    @Nonnull
    public List<String> getScopeTypes() {
        return this.scopeTypes;
    }

    @Nonnull
    public List<WebhookScope> getScopes() {
        return this.scopes;
    }

    public int hashCode() {
        return Objects.hash(this.getActive(), this.getId(), this.getName(), this.scopes, this.scopeTypes, this.getEvents());
    }

    static abstract class AbstractBuilder<B extends AbstractBuilder<B>> {
        private final List<WebhookEvent> events = new ArrayList<WebhookEvent>();
        private final List<String> scopeTypes;
        private final List<WebhookScope> scopes = new ArrayList<WebhookScope>();
        private Boolean active;
        private Integer id;
        private String name;

        AbstractBuilder() {
            this.scopeTypes = new ArrayList<String>();
        }

        AbstractBuilder(AbstractBulkWebhookRequest request) {
            this();
            this.active = Objects.requireNonNull(request, "request").getActive();
            this.events.addAll(request.getEvents());
            this.id = request.getId();
            this.name = request.getName();
            this.scopes.addAll(request.getScopes());
            this.scopeTypes.addAll(request.getScopeTypes());
        }

        @Nonnull
        public B active(boolean value) {
            this.active = value;
            return this.self();
        }

        @Nonnull
        public B event(@Nullable Iterable<WebhookEvent> values) {
            BuilderUtil.addIf(Objects::nonNull, this.events, values);
            return this.self();
        }

        @Nonnull
        public B event(@Nonnull WebhookEvent value, WebhookEvent ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.events, value, values);
            return this.self();
        }

        @Nonnull
        public B id(int value) {
            this.id = value;
            return this.self();
        }

        @Nonnull
        public B name(@Nonnull String value) {
            this.name = Objects.requireNonNull(value, "name");
            return this.self();
        }

        @Nonnull
        public B scope(@Nullable Iterable<WebhookScope> values) {
            BuilderUtil.addIf(Objects::nonNull, this.scopes, values);
            return this.self();
        }

        @Nonnull
        public B scope(@Nonnull WebhookScope value, WebhookScope ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.scopes, value, values);
            return this.self();
        }

        @Nonnull
        public B scopeType(@Nullable Iterable<String> values) {
            BuilderUtil.addIf(Objects::nonNull, this.scopeTypes, values);
            return this.self();
        }

        @Nonnull
        public B scopeType(@Nonnull String value, String ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.scopeTypes, value, values);
            return this.self();
        }

        @Nonnull
        protected abstract B self();
    }
}

