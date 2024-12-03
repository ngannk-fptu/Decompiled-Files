/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.view")
public class ViewEvent
extends BaseAnalyticEvent {
    private final String delegatedType;

    public ViewEvent(String delegatedType, String pluginVersion) {
        super(pluginVersion);
        this.delegatedType = delegatedType;
    }

    public String getDelegatedType() {
        return this.delegatedType;
    }
}

