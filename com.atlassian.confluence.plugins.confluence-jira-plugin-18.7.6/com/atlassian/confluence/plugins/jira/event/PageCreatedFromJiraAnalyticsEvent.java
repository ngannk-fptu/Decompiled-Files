/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 */
package com.atlassian.confluence.plugins.jira.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;

public class PageCreatedFromJiraAnalyticsEvent
extends ConfluenceEvent {
    protected final String eventName;
    protected final String blueprintModuleKey;

    public PageCreatedFromJiraAnalyticsEvent(Object src, EventType eventType, String blueprintModuleKey) {
        super(src);
        this.eventName = eventType.getAnalyticsEventName();
        this.blueprintModuleKey = blueprintModuleKey;
    }

    @EventName
    public String retrieveEventName() {
        return this.eventName;
    }

    public String getBlueprintModuleKey() {
        return this.blueprintModuleKey;
    }

    public static enum EventType {
        EPIC_FROM_PLAN_MODE("confluence.jira.content.created.epic.plan"),
        SPRINT_FROM_PLAN_MODE("confluence.jira.content.created.sprint.plan"),
        SPRINT_FROM_REPORT_MODE("confluence.jira.content.created.sprint.report");

        private final String analyticsEventName;

        private EventType(String analyticsEventName) {
            this.analyticsEventName = analyticsEventName;
        }

        public String getAnalyticsEventName() {
            return this.analyticsEventName;
        }
    }
}

