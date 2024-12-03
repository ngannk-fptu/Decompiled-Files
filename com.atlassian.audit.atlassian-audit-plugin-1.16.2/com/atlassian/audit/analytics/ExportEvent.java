/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.export")
public class ExportEvent
extends BaseAnalyticEvent {
    private final boolean selective;
    private final String delegatedType;

    public ExportEvent(boolean selective, String delegatedType, String pluginVersion) {
        super(pluginVersion);
        this.delegatedType = delegatedType;
        this.selective = selective;
    }

    public boolean isSelective() {
        return this.selective;
    }

    public String getDelegatedType() {
        return this.delegatedType;
    }
}

