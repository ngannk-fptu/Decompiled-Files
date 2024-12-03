/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEvent;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsEventFactory;
import com.atlassian.johnson.event.Event;
import java.net.URL;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ParametersAreNonnullByDefault
public class DefaultHealthCheckAnalyticsEventFactory
implements HealthCheckAnalyticsEventFactory {
    @Override
    public @NonNull HealthCheckAnalyticsEvent forHealthCheckResult(Event johnsonEvent) {
        return this.createEvent(johnsonEvent, null);
    }

    @Override
    public @NonNull HealthCheckAnalyticsEvent forJohnsonHelpLinkClicked(Event johnsonEvent) {
        URL kbURL = (URL)johnsonEvent.getAttribute((Object)"helpUrl");
        return this.createEvent(johnsonEvent, kbURL);
    }

    private HealthCheckAnalyticsEvent createEvent(Event johnsonEvent, @Nullable URL kbUrl) {
        String startupMode = this.getStartupMode();
        String cause = (String)johnsonEvent.getAttribute((Object)"causeKey");
        String checkId = (String)johnsonEvent.getAttribute((Object)"idKey");
        String eventId = (String)johnsonEvent.getAttribute((Object)"eventKey");
        String eventLevel = johnsonEvent.getLevel().getLevel();
        return new HealthCheckAnalyticsEvent(checkId, startupMode, eventId, eventLevel, cause, kbUrl);
    }

    private String getStartupMode() {
        return "unknown";
    }
}

