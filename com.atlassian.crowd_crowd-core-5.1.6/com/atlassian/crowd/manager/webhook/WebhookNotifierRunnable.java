/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.manager.webhook.WebhookNotificationListener
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.webhook.WebhookNotificationListener;
import com.atlassian.crowd.manager.webhook.WebhookPinger;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.Preconditions;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebhookNotifierRunnable
implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(WebhookNotifierRunnable.class);
    private final Webhook webhook;
    private final WebhookPinger webhookPinger;
    private final WebhookNotificationListener webhookNotificationListener;

    public WebhookNotifierRunnable(Webhook webhook, WebhookPinger webhookPinger, WebhookNotificationListener webhookNotificationListener) {
        this.webhook = (Webhook)Preconditions.checkNotNull((Object)webhook);
        this.webhookPinger = (WebhookPinger)Preconditions.checkNotNull((Object)webhookPinger);
        this.webhookNotificationListener = (WebhookNotificationListener)Preconditions.checkNotNull((Object)webhookNotificationListener);
        Preconditions.checkArgument((webhook.getId() != null ? 1 : 0) != 0, (Object)"Webhook must be registered and have an ID");
    }

    @Override
    public void run() {
        try {
            if (this.pingWebhook(this.webhook)) {
                this.webhookNotificationListener.onPingSuccess(this.webhook.getId().longValue());
            } else {
                this.webhookNotificationListener.onPingFailure(this.webhook.getId().longValue());
            }
        }
        catch (WebhookNotFoundException e) {
            logger.debug("Webhook " + this.webhook.getId() + " was deleted while it was being pinged", (Throwable)e);
        }
    }

    private boolean pingWebhook(Webhook webhook) {
        try {
            this.webhookPinger.ping(webhook);
            return true;
        }
        catch (IOException e) {
            logger.debug("Failed to notify Webhook " + webhook.getId(), (Throwable)e);
            return false;
        }
    }
}

