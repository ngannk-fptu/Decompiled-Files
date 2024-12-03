/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.event.api.EventPublisher
 */
package com.atlassian.upm.core.analytics.impl;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.upm.api.util.Pair;
import com.atlassian.upm.core.analytics.AnalyticsEvent;
import com.atlassian.upm.core.analytics.AnalyticsLogger;
import com.atlassian.upm.core.analytics.AnalyticsPublisher;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AtlassianAnalyticsPublisher
implements AnalyticsPublisher {
    private final AnalyticsLogger analytics;
    private final EventPublisher eventPublisher;

    public AtlassianAnalyticsPublisher(AnalyticsLogger analytics, EventPublisher eventPublisher) {
        this.analytics = Objects.requireNonNull(analytics, "analytics");
        this.eventPublisher = Objects.requireNonNull(eventPublisher, "eventPublisher");
    }

    @Override
    public void publish(AnalyticsEvent event) throws Exception {
        if (!event.isRecordedByMarketplace()) {
            HashMap<String, String> props = new HashMap<String, String>();
            for (Pair<String, String> p : event.getMetadata()) {
                props.put(p.first(), p.second());
            }
            EventWrapper ew = new EventWrapper(event.getEventType(), props);
            this.eventPublisher.publish((Object)ew);
        }
    }

    public void afterPropertiesSet() throws Exception {
        this.analytics.register(this);
    }

    public void destroy() throws Exception {
        this.analytics.unregister(this);
    }

    @EventName(value="browser")
    public static class EventWrapper {
        private final String name;
        private final Map<String, String> properties;

        EventWrapper(String name, Map<String, String> properties) {
            this.name = Objects.requireNonNull(name);
            this.properties = Collections.unmodifiableMap(properties);
        }

        public String getName() {
            return this.name;
        }

        public Map<String, String> getProperties() {
            return this.properties;
        }
    }
}

