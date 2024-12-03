/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.model.application.Application
 *  com.atlassian.crowd.model.webhook.Webhook
 */
package com.atlassian.crowd.dao.webhook;

import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.webhook.Webhook;

public interface WebhookDAO {
    public Webhook findById(Long var1) throws WebhookNotFoundException;

    public Webhook findByApplicationAndEndpointUrl(Application var1, String var2) throws WebhookNotFoundException;

    public Webhook add(Webhook var1);

    public void remove(Webhook var1) throws WebhookNotFoundException;

    public Iterable<Webhook> findAll();

    public Webhook update(Webhook var1) throws WebhookNotFoundException;
}

