/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.audit.analytics;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.audit.analytics.BaseAnalyticEvent;

@EventName(value="audit.level.updated")
public class LevelUpdatedEvent
extends BaseAnalyticEvent {
    private final String area;
    private final String oldLevel;
    private final String newLevel;

    public LevelUpdatedEvent(String area, String oldLevel, String newLevel, String pluginVersion) {
        super(pluginVersion);
        this.area = area;
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public String getArea() {
        return this.area;
    }

    public String getOldLevel() {
        return this.oldLevel;
    }

    public String getNewLevel() {
        return this.newLevel;
    }
}

