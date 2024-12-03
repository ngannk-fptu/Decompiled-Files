/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.jira.event.type.EventType
 */
package com.atlassian.analytics.client.extractor;

import com.atlassian.jira.event.type.EventType;

public enum SystemIssueEventType {
    ISSUE_CREATED(EventType.ISSUE_CREATED_ID, "Issue Created"),
    ISSUE_UPDATED(EventType.ISSUE_UPDATED_ID, "Issue Updated"),
    ISSUE_ASSIGNED(EventType.ISSUE_ASSIGNED_ID, "Issue Assigned"),
    ISSUE_RESOLVED(EventType.ISSUE_RESOLVED_ID, "Issue Resolved"),
    ISSUE_CLOSED(EventType.ISSUE_CLOSED_ID, "Issue Closed"),
    ISSUE_COMMENTED(EventType.ISSUE_COMMENTED_ID, "Issue Commented"),
    ISSUE_REOPENED(EventType.ISSUE_REOPENED_ID, "Issue Reopened"),
    ISSUE_DELETED(EventType.ISSUE_DELETED_ID, "Issue Deleted"),
    ISSUE_MOVED(EventType.ISSUE_MOVED_ID, "Issue Moved"),
    ISSUE_WORKLOGGED(EventType.ISSUE_WORKLOGGED_ID, "Work Logged On Issue"),
    ISSUE_WORKSTARTED(EventType.ISSUE_WORKSTARTED_ID, "Work Started On Issue"),
    ISSUE_WORKSTOPPED(EventType.ISSUE_WORKSTOPPED_ID, "Work Stopped On Issue"),
    ISSUE_GENERICEVENT(EventType.ISSUE_GENERICEVENT_ID, "Generic Event"),
    ISSUE_COMMENT_EDITED(EventType.ISSUE_COMMENT_EDITED_ID, "Issue Comment Edited"),
    ISSUE_WORKLOG_UPDATED(EventType.ISSUE_WORKLOG_UPDATED_ID, "Issue Worklog Updated"),
    ISSUE_WORKLOG_DELETED(EventType.ISSUE_WORKLOG_DELETED_ID, "Issue Worklog Deleted"),
    ISSUE_COMMENT_DELETED(EventType.ISSUE_COMMENT_DELETED_ID, "Issue Comment Deleted");

    private final long eventType;
    private final String eventTypeName;

    private SystemIssueEventType(long eventType, String eventTypeName) {
        this.eventType = eventType;
        this.eventTypeName = eventTypeName;
    }

    public long getEventType() {
        return this.eventType;
    }

    public String getEventTypeName() {
        return this.eventTypeName;
    }
}

