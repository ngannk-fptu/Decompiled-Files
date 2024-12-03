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
public class WebhookCreatedEvent
extends AbstractWebhookEvent {
    private final Webhook webhook;

    public WebhookCreatedEvent(@Nonnull Object source, @Nonnull Webhook webhook) {
        super(Objects.requireNonNull(source, "source"));
        this.webhook = Objects.requireNonNull(webhook, "webhook");
    }

    @Nonnull
    public Webhook getWebhook() {
        return this.webhook;
    }
}

