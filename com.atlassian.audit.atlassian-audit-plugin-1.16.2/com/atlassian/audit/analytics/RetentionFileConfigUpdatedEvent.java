/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.retention.file.updated")
public class RetentionFileConfigUpdatedEvent
extends BaseAnalyticEvent {
    private final String oldMaxFileCount;
    private final String newMaxFileCount;

    public RetentionFileConfigUpdatedEvent(int oldMaxFileCount, int newMaxFileCount, String pluginVersion) {
        super(pluginVersion);
        this.oldMaxFileCount = Integer.toString(oldMaxFileCount);
        this.newMaxFileCount = Integer.toString(newMaxFileCount);
    }

    public String getOldMaxFileCount() {
        return this.oldMaxFileCount;
    }

    public String getNewMaxFileCount() {
        return this.newMaxFileCount;
    }
}

