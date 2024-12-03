/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.webhooks.Webhook
 *  com.atlassian.webhooks.event.WebhookCreatedEvent
 *  com.atlassian.webhooks.event.WebhookDeletedEvent
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.internal.webhooks.analytics;

import com.atlassian.confluence.internal.webhooks.analytics.WebhookAnalytics;
import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.webhooks.Webhook;
import com.atlassian.webhooks.event.WebhookCreatedEvent;
import com.atlassian.webhooks.event.WebhookDeletedEvent;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class WebhookEventListener {
    private final EventPublisher eventPublisher;

    public WebhookEventListener(@ComponentImport EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void teardown() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onWebhookCreated(WebhookCreatedEvent event) {
        Webhook webhook = event.getWebhook();
        Map configuration = webhook.getConfiguration();
        this.eventPublisher.publish((Object)new WebhookAnalytics.CreateEvent(webhook.isActive(), webhook.getEvents(), Boolean.parseBoolean(configuration.getOrDefault("connectionTested", "false")), Boolean.parseBoolean(configuration.getOrDefault("formSubmit", "false"))));
    }

    @EventListener
    public void onWebhookDeleted(WebhookDeletedEvent event) {
        Webhook webhook = event.getWebhook();
        this.eventPublisher.publish((Object)new WebhookAnalytics.DeleteEvent(webhook.isActive(), webhook.getEvents()));
    }
}

