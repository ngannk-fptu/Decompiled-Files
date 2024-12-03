/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.validation.Valid
 *  javax.validation.constraints.NotBlank
 *  javax.validation.constraints.NotEmpty
 *  javax.validation.constraints.NotNull
 *  javax.validation.constraints.Size
 *  org.hibernate.validator.constraints.URL
 */
package com.atlassian.webhooks;

import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.util.BuilderUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public abstract class AbstractWebhookRequest {
    private final boolean active;
    private final Map<String, String> configuration;
    private final List<WebhookEvent> events;
    private final String name;
    private final WebhookScope scope;
    private final String url;

    AbstractWebhookRequest(AbstractBuilder<?> builder) {
        this.active = ((AbstractBuilder)builder).active;
        this.configuration = Collections.unmodifiableMap(new HashMap(((AbstractBuilder)builder).configuration));
        this.events = Collections.unmodifiableList(new ArrayList(((AbstractBuilder)builder).events));
        this.name = ((AbstractBuilder)builder).name == null ? ((AbstractBuilder)builder).url : ((AbstractBuilder)builder).name;
        this.scope = ((AbstractBuilder)builder).scope;
        this.url = ((AbstractBuilder)builder).url;
    }

    @NotNull
    public Map<String, String> getConfiguration() {
        return this.configuration;
    }

    @Nonnull
    @NotEmpty(message="{webhooks.event.required}")
    @Valid
    public @NotEmpty(message="{webhooks.event.required}") @Valid List<WebhookEvent> getEvents() {
        return this.events;
    }

    @NotBlank(message="{webhooks.field.name.required}")
    @Size(max=255)
    public @NotBlank(message="{webhooks.field.name.required}") @Size(max=255) String getName() {
        return this.name;
    }

    @NotNull(message="{webhooks.field.required}")
    public @NotNull(message="{webhooks.field.required}") WebhookScope getScope() {
        return this.scope;
    }

    @NotNull(message="{webhooks.field.url.required}")
    @URL
    public @NotNull(message="{webhooks.field.url.required}") @URL String getUrl() {
        return this.url;
    }

    public boolean isActive() {
        return this.active;
    }

    static abstract class AbstractBuilder<B extends AbstractBuilder<B>> {
        private final Map<String, String> configuration = new HashMap<String, String>();
        private final List<WebhookEvent> events = new ArrayList<WebhookEvent>();
        private boolean active = true;
        private String name;
        private WebhookScope scope;
        private String url;

        AbstractBuilder() {
        }

        @Nonnull
        public B active(boolean value) {
            this.active = value;
            return this.self();
        }

        @Nonnull
        public B configuration(@Nullable Map<String, String> value) {
            if (value != null) {
                value.forEach(this::configuration);
            }
            return this.self();
        }

        @Nonnull
        public B configuration(@Nullable String key, @Nullable String value) {
            if (key != null) {
                if (value == null) {
                    this.configuration.remove(key);
                } else {
                    this.configuration.put(key, value);
                }
            }
            return this.self();
        }

        @Nonnull
        public B event(@Nullable Iterable<WebhookEvent> values) {
            BuilderUtil.addIf(Objects::nonNull, this.events, values);
            return this.self();
        }

        @Nonnull
        public B event(@Nullable WebhookEvent value, WebhookEvent ... values) {
            BuilderUtil.addIf(Objects::nonNull, this.events, value, values);
            return this.self();
        }

        @Nonnull
        public B name(@Nullable String value) {
            this.name = value;
            return this.self();
        }

        @Nonnull
        public B scope(@Nullable WebhookScope value) {
            this.scope = value;
            return this.self();
        }

        @Nonnull
        public B url(@Nullable String value) {
            this.url = value;
            return this.self();
        }

        @Nonnull
        protected abstract B self();
    }
}

