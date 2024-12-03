/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.exception.WebhookNotFoundException
 *  com.atlassian.crowd.model.webhook.Webhook
 */
package com.atlassian.crowd.manager.webhook;

import com.atlassian.crowd.exception.WebhookNotFoundException;
import com.atlassian.crowd.model.webhook.Webhook;

public interface WebhookRegistry {
    public Webhook add(Webhook var1);

    public void remove(Webhook var1) throws WebhookNotFoundException;

    public Webhook findById(long var1) throws WebhookNotFoundException;

    public Iterable<Webhook> findAll();

    public Webhook update(Webhook var1) throws WebhookNotFoundException;
}

