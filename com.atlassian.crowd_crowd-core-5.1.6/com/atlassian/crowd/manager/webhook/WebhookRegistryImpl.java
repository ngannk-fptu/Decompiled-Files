/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.dao.webhook.WebhookDAO
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.manager.webhook.WebhookRegistry
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.base.Preconditions
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.dao.webhook.WebhookDAO;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.manager.webhook.WebhookRegistry;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.base.Preconditions;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class WebhookRegistryImpl
implements WebhookRegistry {
    private final WebhookDAO webhookDAO;

    public WebhookRegistryImpl(WebhookDAO webhookDAO) {
        this.webhookDAO = webhookDAO;
    }

    public Webhook add(Webhook webhook) {
        try {
            return this.webhookDAO.findByApplicationAndEndpointUrl(webhook.getApplication(), webhook.getEndpointUrl());
        }
        catch (WebhookNotFoundException e) {
            return this.webhookDAO.add(webhook);
        }
    }

    public void remove(Webhook webhook) throws WebhookNotFoundException {
        this.webhookDAO.remove(webhook);
    }

    public Webhook findById(long webhookId) throws WebhookNotFoundException {
        return this.webhookDAO.findById(Long.valueOf(webhookId));
    }

    public Iterable<Webhook> findAll() {
        return this.webhookDAO.findAll();
    }

    public Webhook update(Webhook webhook) throws WebhookNotFoundException {
        Preconditions.checkNotNull((Object)webhook.getId());
        return this.webhookDAO.update(webhook);
    }
}

