/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.event.events.cluster.ClusterEvent;

public class SiteDarkFeatureEnabledEvent
extends ConfluenceEvent
implements ClusterEvent {
    private static final long serialVersionUID = -2775161611504010285L;
    private final String featureKey;

    public SiteDarkFeatureEnabledEvent(Object src, String featureKey) {
        super(src);
        this.featureKey = featureKey;
    }

    public String getFeatureKey() {
        return this.featureKey;
    }
}

