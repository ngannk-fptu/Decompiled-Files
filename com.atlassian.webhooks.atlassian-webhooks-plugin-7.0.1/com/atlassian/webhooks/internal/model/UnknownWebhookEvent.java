/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEvent
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.model;

import com.atlassian.webhooks.WebhookEvent;
import java.util.Objects;
import javax.annotation.Nonnull;

public class UnknownWebhookEvent
implements WebhookEvent {
    private final String id;

    public UnknownWebhookEvent(String id) {
        this.id = Objects.requireNonNull(id, "id");
    }

    @Nonnull
    public String getId() {
        return this.id;
    }

    @Nonnull
    public String getI18nKey() {
        return this.id;
    }
}

