/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.webhook.Webhook
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.crowd.dao.webhook;

import com.atlassian.crowd.dao.webhook.WebhookDAO;
import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.webhook.Webhook;
import com.google.common.collect.ImmutableList;

public class NoopWebhookDAOImpl
implements WebhookDAO {
    @Override
    public Webhook findById(Long webhookId) throws WebhookNotFoundException {
        throw new WebhookNotFoundException(webhookId.longValue());
    }

    @Override
    public Webhook findByApplicationAndEndpointUrl(Application application, String endpointUrl) throws WebhookNotFoundException {
        throw new WebhookNotFoundException(application.getId().longValue(), endpointUrl);
    }

    @Override
    public Webhook add(Webhook webhook) {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public void remove(Webhook webhook) throws WebhookNotFoundException {
        throw new UnsupportedOperationException("Not implemented");
    }

    @Override
    public Iterable<Webhook> findAll() {
        return ImmutableList.of();
    }

    @Override
    public Webhook update(Webhook webhook) throws WebhookNotFoundException {
        throw new WebhookNotFoundException(webhook.getId().longValue());
    }
}

