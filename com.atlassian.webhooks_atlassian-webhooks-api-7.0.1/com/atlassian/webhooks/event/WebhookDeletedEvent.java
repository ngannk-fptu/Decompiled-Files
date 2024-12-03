/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webhooks.event;

import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.event.AbstractWebhookEvent;
import java.util.Objects;
import javax.annotation.Nonnull;

public class WebhookDeletedEvent
extends AbstractWebhookEvent {
    private final Webhook webhook;

    public WebhookDeletedEvent(@Nonnull Object source, @Nonnull Webhook webhook) {
        super(Objects.requireNonNull(source, "source"));
        this.webhook = Objects.requireNonNull(webhook, "webhook");
    }

    @Nonnull
    public Webhook getWebhook() {
        return this.webhook;
    }
}

