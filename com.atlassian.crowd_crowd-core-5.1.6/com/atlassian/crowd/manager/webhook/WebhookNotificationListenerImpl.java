/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.manager.webhook.WebhookNotificationListener
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Preconditions
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.webhook.WebhookHealthStrategy;
import com.atlassian.crowd.manager.webhook.WebhookNotificationListener;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.Preconditions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class WebhookNotificationListenerImpl
implements WebhookNotificationListener {
    private static final Logger logger = LoggerFactory.getLogger(WebhookNotificationListenerImpl.class);
    private final WebhookRegistry webhookRegistry;
    private final WebhookHealthStrategy webhookHealthStrategy;

    public WebhookNotificationListenerImpl(WebhookRegistry webhookRegistry, WebhookHealthStrategy webhookHealthStrategy) {
        this.webhookRegistry = (WebhookRegistry)Preconditions.checkNotNull((Object)webhookRegistry);
        this.webhookHealthStrategy = (WebhookHealthStrategy)Preconditions.checkNotNull((Object)webhookHealthStrategy);
    }

    public void onPingSuccess(long webhookId) throws WebhookNotFoundException {
        Webhook webhookTemplate = this.webhookHealthStrategy.registerSuccess(this.webhookRegistry.findById(webhookId));
        if (this.webhookHealthStrategy.isInGoodStanding(webhookTemplate)) {
            this.webhookRegistry.update(webhookTemplate);
        } else {
            logger.info("Webhook {} is in bad standing and will be removed", (Object)webhookTemplate);
            this.webhookRegistry.remove(webhookTemplate);
        }
    }

    public void onPingFailure(long webhookId) throws WebhookNotFoundException {
        Webhook webhookTemplate = this.webhookHealthStrategy.registerFailure(this.webhookRegistry.findById(webhookId));
        logger.info("Webhook {} at URL {} has failed {} consecutive times, first failure was on {}", new Object[]{webhookId, webhookTemplate.getEndpointUrl(), webhookTemplate.getFailuresSinceLastSuccess(), webhookTemplate.getOldestFailureDate()});
        if (this.webhookHealthStrategy.isInGoodStanding(webhookTemplate)) {
            this.webhookRegistry.update(webhookTemplate);
        } else {
            logger.info("Webhook {} is in bad standing and will be removed", (Object)webhookTemplate);
            this.webhookRegistry.remove(webhookTemplate);
        }
    }
}

