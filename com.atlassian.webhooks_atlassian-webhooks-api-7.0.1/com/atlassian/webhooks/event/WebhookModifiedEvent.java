/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.AsynchronousPreferred
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.event;

import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.event.AbstractWebhookEvent;
import java.util.Objects;
import javax.annotation.Nonnull;

@AsynchronousPreferred
public class WebhookModifiedEvent
extends AbstractWebhookEvent {
    private final Webhook newValue;
    private final Webhook oldValue;

    public WebhookModifiedEvent(@Nonnull Object source, @Nonnull Webhook oldValue, @Nonnull Webhook newValue) {
        super(Objects.requireNonNull(source, "source"));
        this.newValue = Objects.requireNonNull(newValue, "newWebhook");
        this.oldValue = Objects.requireNonNull(oldValue, "oldWebhook");
    }

    @Nonnull
    public Webhook getOldValue() {
        return this.oldValue;
    }

    @Nonnull
    public Webhook getNewValue() {
        return this.newValue;
    }
}

