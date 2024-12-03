/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.retention.updated")
public class RetentionUpdatedEvent
extends BaseAnalyticEvent {
    private final String oldRetention;
    private final String oldRetentionUnit;
    private final String newRetention;
    private final String newRetentionUnit;

    public RetentionUpdatedEvent(String oldRetention, String oldRetentionUnit, String newRetention, String newRetentionUnit, String pluginVersion) {
        super(pluginVersion);
        this.oldRetention = oldRetention;
        this.oldRetentionUnit = oldRetentionUnit;
        this.newRetention = newRetention;
        this.newRetentionUnit = newRetentionUnit;
    }

    public String getOldRetention() {
        return this.oldRetention;
    }

    public String getOldRetentionUnit() {
        return this.oldRetentionUnit;
    }

    public String getNewRetention() {
        return this.newRetention;
    }

    public String getNewRetentionUnit() {
        return this.newRetentionUnit;
    }
}

