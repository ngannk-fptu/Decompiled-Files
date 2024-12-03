/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.johnson.JohnsonEventContainer
 *  com.atlassian.johnson.event.Event
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.impl.health.analytics.HealthCheckJohnsonEvents;
import com.atlassian.confluence.impl.health.analytics.KnowledgeBaseArticleClickedEvent;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEvent;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEventFactory;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsSender;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.johnson.JohnsonEventContainer;
import com.atlassian.johnson.event.Event;
import java.util.Objects;

@ParametersAreNonnullByDefault
public class EventPublishingHealthCheckAnalyticsSender
implements HealthCheckAnalyticsSender {
    private final EventPublisher eventPublisher;
    private final HealthCheckAnalyticsEventFactory analyticsEventFactory;
    private final JohnsonEventContainer johnsonEventContainer;

    public EventPublishingHealthCheckAnalyticsSender(HealthCheckAnalyticsEventFactory analyticsEventFactory, EventPublisher eventPublisher, JohnsonEventContainer johnsonEventContainer) {
        this.analyticsEventFactory = Objects.requireNonNull(analyticsEventFactory);
        this.eventPublisher = Objects.requireNonNull(eventPublisher);
        this.johnsonEventContainer = Objects.requireNonNull(johnsonEventContainer);
    }

    @Override
    public void sendHealthCheckResult(Event johnsonEvent) {
        HealthCheckAnalyticsEvent analyticsEvent = this.analyticsEventFactory.forHealthCheckResult(johnsonEvent);
        this.eventPublisher.publish((Object)analyticsEvent);
    }

    @Override
    public void sendHelpLinkClickedForEvent(String eventId) {
        HealthCheckJohnsonEvents.findEventById(this.johnsonEventContainer, eventId).ifPresent(event -> {
            HealthCheckAnalyticsEvent analyticsEvent = this.analyticsEventFactory.forJohnsonHelpLinkClicked((Event)event);
            this.eventPublisher.publish((Object)analyticsEvent);
        });
    }

    @Override
    public void sendGeneralHelpLinkClicked(String kbURL) {
        this.eventPublisher.publish((Object)new KnowledgeBaseArticleClickedEvent(kbURL));
    }
}

