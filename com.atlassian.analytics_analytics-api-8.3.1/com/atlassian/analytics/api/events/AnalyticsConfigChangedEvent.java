/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.analytics.api.events;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="analytics.config.changed")
public class AnalyticsConfigChangedEvent {
    private final Key key;
    private final String oldValue;
    private final String newValue;

    public AnalyticsConfigChangedEvent(Key key, String oldValue, String newValue) {
        this.key = key;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Key getKey() {
        return this.key;
    }

    public String getOldValue() {
        return this.oldValue;
    }

    public String getNewValue() {
        return this.newValue;
    }

    public static enum Key {
        DESTINATION("destination"),
        POLICY_ACKNOWLEDGED("policy_acknowledged"),
        ANALYTICS_ENABLED("analytics_enabled"),
        ANALYTICS_DISABLED_USERNAME("analytics_disabled_username"),
        ANALYTICS_DISABLED_DATE("analytics_disabled_date"),
        LOGGED_BASE_DATA("logged_base_analytics_data");

        public static final String KEY_PREFIX = "com.atlassian.analytics.client.configuration.";
        private final String key;

        private Key(String suffix) {
            this.key = "com.atlassian.analytics.client.configuration.." + suffix;
        }

        public String getKey() {
            return this.key;
        }
    }
}

