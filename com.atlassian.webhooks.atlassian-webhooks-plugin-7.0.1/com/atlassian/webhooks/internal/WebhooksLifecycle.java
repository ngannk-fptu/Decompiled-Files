/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.lifecycle.LifecycleAware
 *  com.atlassian.webhooks.WebhookService
 *  com.atlassian.webhooks.WebhooksConfiguration
 */
package com.atlassian.webhooks.internal;

import com.atlassian.sal.api.lifecycle.LifecycleAware;
import com.atlassian.webhooks.WebhookService;
import com.atlassian.webhooks.WebhooksConfiguration;
import com.atlassian.webhooks.internal.WebhookHostAccessor;
import com.atlassian.webhooks.internal.WebhookServiceRegistrar;
import com.atlassian.webhooks.internal.WebhooksLifecycleAware;
import java.util.List;

public class WebhooksLifecycle
implements LifecycleAware {
    private final WebhookHostAccessor hostAccessor;
    private final WebhookServiceRegistrar registrar;
    private final List<WebhooksLifecycleAware> services;
    private final WebhookService webhookService;

    public WebhooksLifecycle(WebhookHostAccessor hostAccessor, List<WebhooksLifecycleAware> services, WebhookService webhookService) {
        this.hostAccessor = hostAccessor;
        this.services = services;
        this.webhookService = webhookService;
        this.registrar = new WebhookServiceRegistrar();
    }

    public void onStart() {
        this.registrar.register(this.webhookService);
        WebhooksConfiguration configuration = this.hostAccessor.getConfiguration().orElse(WebhooksConfiguration.DEFAULT);
        this.services.forEach(service -> service.onStart(configuration));
    }

    public void onStop() {
        this.registrar.register(null);
        this.services.forEach(WebhooksLifecycleAware::onStop);
    }
}

