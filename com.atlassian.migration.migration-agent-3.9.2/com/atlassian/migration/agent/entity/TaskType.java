/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 */
package com.atlassian.migration.agent.entity;

import lombok.Generated;

public enum TaskType {
    SPACE("confluence-space"),
    ATTACHMENTS("space-attachments"),
    USERS("users-and-groups"),
    GLOBAL_ENTITIES("global-entities"),
    APPS("apps");

    private final String analyticsEvent;

    @Generated
    public String getAnalyticsEvent() {
        return this.analyticsEvent;
    }

    @Generated
    private TaskType(String analyticsEvent) {
        this.analyticsEvent = analyticsEvent;
    }
}

