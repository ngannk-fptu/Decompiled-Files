/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.confluence.user.ConfluenceUser
 */
package com.atlassian.confluence.extra.calendar3.events;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.confluence.extra.calendar3.events.CalendarEvent;
import com.atlassian.confluence.user.ConfluenceUser;

@EventName(value="teamcalendars.jira.applink.request")
public class JiraApplinkRequest
extends CalendarEvent {
    private String applicationLinkId;
    private String operation;

    public JiraApplinkRequest(Object eventSource, ConfluenceUser trigger, ApplicationLink applicationLink, String operation) {
        super(eventSource, trigger);
        this.setApplicationLinkId(applicationLink.getId().get());
        this.setOperation(operation);
    }

    public void setApplicationLinkId(String applicationLinkId) {
        this.applicationLinkId = applicationLinkId;
    }

    public String getApplicationLinkId() {
        return this.applicationLinkId;
    }

    public String getOperation() {
        return this.operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}

