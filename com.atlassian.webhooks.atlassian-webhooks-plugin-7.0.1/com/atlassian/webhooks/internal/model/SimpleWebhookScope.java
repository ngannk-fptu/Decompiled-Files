/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookScope
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.webhooks.internal.model;

import com.atlassian.webhooks.WebhookScope;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SimpleWebhookScope
implements WebhookScope {
    private final String id;
    private final String type;

    public SimpleWebhookScope(@Nonnull String type, @Nullable String id) {
        this.id = id;
        this.type = Objects.requireNonNull(type, "type");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SimpleWebhookScope that = (SimpleWebhookScope)o;
        return Objects.equals(this.getType(), that.getType()) && Objects.equals(this.getId(), that.getId());
    }

    @Nonnull
    public Optional<String> getId() {
        return Optional.ofNullable(this.id);
    }

    @Nonnull
    public String getType() {
        return this.type;
    }

    public int hashCode() {
        return Objects.hash(this.getType(), this.getId());
    }
}

