/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 *  com.atlassian.confluence.event.events.ConfluenceEvent
 *  com.atlassian.event.api.AsynchronousPreferred
 */
package com.atlassian.confluence.plugins.metadata.jira.event;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.event.events.ConfluenceEvent;
import com.atlassian.confluence.plugins.metadata.jira.model.JiraMetadataGroup;
import com.atlassian.event.api.AsynchronousPreferred;

@AsynchronousPreferred
@EventName(value="confluence.jira.metadata.visit")
public class JiraItemVisitEvent
extends ConfluenceEvent {
    private final JiraMetadataGroup.Type type;
    private final boolean viewMore;

    public JiraItemVisitEvent(Object src, JiraMetadataGroup.Type type, boolean viewMore) {
        super(src);
        this.type = type;
        this.viewMore = viewMore;
    }

    public JiraMetadataGroup.Type getType() {
        return this.type;
    }

    public boolean isViewMore() {
        return this.viewMore;
    }
}

