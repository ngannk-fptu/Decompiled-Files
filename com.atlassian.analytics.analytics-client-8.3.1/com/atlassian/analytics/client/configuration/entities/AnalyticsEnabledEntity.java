/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlElement
 */
package com.atlassian.analytics.client.configuration.entities;

import javax.xml.bind.annotation.XmlElement;

public class AnalyticsEnabledEntity {
    @XmlElement
    private boolean analyticsEnabled;

    public boolean isAnalyticsEnabled() {
        return this.analyticsEnabled;
    }

    public void setAnalyticsEnabled(boolean analyticsEnabled) {
        this.analyticsEnabled = analyticsEnabled;
    }
}

