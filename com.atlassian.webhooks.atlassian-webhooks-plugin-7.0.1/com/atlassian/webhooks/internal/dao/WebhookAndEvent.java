/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Objects
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.internal.dao;

import java.util.Objects;
import javax.annotation.Nonnull;

class WebhookAndEvent {
    private final String eventId;
    private final int webhookId;

    WebhookAndEvent(int webhookId, @Nonnull String eventId) {
        this.eventId = eventId;
        this.webhookId = Objects.requireNonNull(Integer.valueOf(webhookId), "webhookId");
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        WebhookAndEvent that = (WebhookAndEvent)o;
        return this.webhookId == that.webhookId && com.google.common.base.Objects.equal((Object)this.eventId, (Object)that.eventId);
    }

    public String getEventId() {
        return this.eventId;
    }

    public int getWebhookId() {
        return this.webhookId;
    }

    public int hashCode() {
        return com.google.common.base.Objects.hashCode((Object[])new Object[]{this.webhookId, this.eventId});
    }
}

