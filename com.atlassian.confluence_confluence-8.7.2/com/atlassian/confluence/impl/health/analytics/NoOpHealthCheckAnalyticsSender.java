/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.johnson.event.Event
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.impl.health.analytics;

import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.confluence.internal.health.analytics.HealthCheckAnalyticsSender;
import com.atlassian.johnson.event.Event;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ParametersAreNonnullByDefault
public class NoOpHealthCheckAnalyticsSender
implements HealthCheckAnalyticsSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpHealthCheckAnalyticsSender.class);

    @Override
    public void sendHealthCheckResult(Event johnsonEvent) {
        LOGGER.debug("Not sending analytics event for {}", (Object)johnsonEvent);
    }

    @Override
    public void sendHelpLinkClickedForEvent(String eventId) {
        LOGGER.debug("Not sending help link event for event {}", (Object)eventId);
    }

    @Override
    public void sendGeneralHelpLinkClicked(String kbUrl) {
        LOGGER.debug("Not sending analytics event for KB URL = '{}'", (Object)kbUrl);
    }
}

