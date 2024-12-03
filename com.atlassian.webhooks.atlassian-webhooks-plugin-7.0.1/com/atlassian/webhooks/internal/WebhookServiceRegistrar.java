/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.webhooks.WebhookEventIdValidator
 *  com.atlassian.webhooks.WebhookService
 */
package com.atlassian.webhooks.internal;

import com.atlassian.webhooks.WebhookEventIdValidator;
import com.atlassian.webhooks.WebhookService;

public class WebhookServiceRegistrar
extends WebhookEventIdValidator {
    public void register(WebhookService value) {
        WebhookServiceRegistrar.setWebhookService((WebhookService)value);
    }
}

