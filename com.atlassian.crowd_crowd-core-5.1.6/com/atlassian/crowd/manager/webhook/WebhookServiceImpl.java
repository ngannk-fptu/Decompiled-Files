/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.manager.webhook.WebhookNotificationListener
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.manager.webhook.WebhookService
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.manager.webhook.KeyedExecutor;
import com.atlassian.crowd.manager.webhook.WebhookNotificationListener;
import com.atlassian.crowd.manager.webhook.WebhookNotifierRunnable;
import com.atlassian.crowd.manager.webhook.WebhookPinger;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.manager.webhook.WebhookService;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class WebhookServiceImpl
implements WebhookService {
    private static final Logger logger = LoggerFactory.getLogger(WebhookServiceImpl.class);
    private final WebhookRegistry webhookRegistry;
    private final WebhookPinger webhookPinger;
    private final KeyedExecutor<Long> executor;
    private final WebhookNotificationListener webhookNotificationListener;

    public WebhookServiceImpl(WebhookRegistry webhookRegistry, WebhookPinger webhookPinger, KeyedExecutor<Long> executor, WebhookNotificationListener webhookNotificationListener) {
        this.webhookRegistry = (WebhookRegistry)Preconditions.checkNotNull((Object)webhookRegistry);
        this.webhookPinger = (WebhookPinger)Preconditions.checkNotNull((Object)webhookPinger);
        this.executor = (KeyedExecutor)Preconditions.checkNotNull(executor);
        this.webhookNotificationListener = (WebhookNotificationListener)Preconditions.checkNotNull((Object)webhookNotificationListener);
    }

    public void notifyWebhooks() {
        logger.debug("New events are available, notifying Webhooks");
        for (Webhook webhook : this.webhookRegistry.findAll()) {
            WebhookNotifierRunnable runnable = new WebhookNotifierRunnable(webhook, this.webhookPinger, this.webhookNotificationListener);
            this.executor.execute(runnable, webhook.getId());
        }
    }
}

