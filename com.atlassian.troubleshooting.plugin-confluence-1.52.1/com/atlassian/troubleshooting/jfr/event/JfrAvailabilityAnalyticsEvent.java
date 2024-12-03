/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.troubleshooting.jfr.event;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="atst.jfr.availability.in.jvm.checked")
public class JfrAvailabilityAnalyticsEvent {
    private final boolean featureAvailableInJvm;

    public JfrAvailabilityAnalyticsEvent(boolean featureAvailableInJvm) {
        this.featureAvailableInJvm = featureAvailableInJvm;
    }

    public boolean getFeatureAvailableInJvm() {
        return this.featureAvailableInJvm;
    }

    public String toString() {
        return "JfrAvailabilityAnalyticsEvent{featureAvailableInJvm=" + this.featureAvailableInJvm + '}';
    }
}

