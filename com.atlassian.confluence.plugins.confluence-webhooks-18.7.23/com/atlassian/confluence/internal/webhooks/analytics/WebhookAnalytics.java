/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.AsynchronousPreferred
 *  com.atlassian.webhooks.WebhookEvent
 */
package com.atlassian.confluence.internal.webhooks.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.AsynchronousPreferred;
import com.atlassian.webhooks.WebhookEvent;
import java.util.Collection;

public class WebhookAnalytics {

    @AsynchronousPreferred
    @EventName(value="confluence.webhook.delete-webhook")
    public static class DeleteEvent
    extends AbstractWebhookAnalyticsEvent {
        public DeleteEvent(boolean active, Collection<WebhookEvent> events) {
            super(active, events);
        }
    }

    @AsynchronousPreferred
    @EventName(value="confluence.webhook.create-webhook")
    public static class CreateEvent
    extends AbstractWebhookAnalyticsEvent {
        private final boolean connectionTested;
        private final boolean formSubmit;

        public CreateEvent(boolean active, Collection<WebhookEvent> events, boolean connectionTested, boolean formSubmit) {
            super(active, events);
            this.connectionTested = connectionTested;
            this.formSubmit = formSubmit;
        }

        public boolean getConnectionTested() {
            return this.connectionTested;
        }

        public boolean getFormSubmit() {
            return this.formSubmit;
        }
    }

    private static abstract class AbstractWebhookAnalyticsEvent {
        private final boolean active;
        private final Collection<WebhookEvent> events;

        protected AbstractWebhookAnalyticsEvent(boolean active, Collection<WebhookEvent> events) {
            this.active = active;
            this.events = events;
        }

        public Collection<WebhookEvent> getEvents() {
            return this.events;
        }

        public boolean getActive() {
            return this.active;
        }
    }
}

