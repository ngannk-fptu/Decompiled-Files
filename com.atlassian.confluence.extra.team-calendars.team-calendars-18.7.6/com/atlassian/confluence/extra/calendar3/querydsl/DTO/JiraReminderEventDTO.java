/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.calendar3.querydsl.DTO;

import com.atlassian.confluence.extra.calendar3.querydsl.DTO.EventDTO;
import java.io.Serializable;

public class JiraReminderEventDTO
extends EventDTO
implements Serializable {
    private String keyId;
    private String userId;
    private String jql;
    private String ticketId;
    private String assignee;
    private String status;
    private String eventType;
    private String jiraIssueLink;
    private String jiraIssueIconUrl;

    public JiraReminderEventDTO() {
    }

    public JiraReminderEventDTO(int eventId, String subCalendarId, long utcStart, long utcEnd, long period, String summary, String description, String storeKey, boolean isAllDay, String calendarName, String parentSubCalendarId, String keyId, String userId, String jql, String ticketId, String assignee, String status, String eventType, String jiraIssueLink, String jiraIssueIconUrl) {
        super(eventId, subCalendarId, utcStart, utcEnd, period, "", summary, description, "", "", "", 0L, 0L, 0L, 0, storeKey, isAllDay, 0L, 0L, "", "", calendarName, parentSubCalendarId, "", "", "");
        this.keyId = keyId;
        this.userId = userId;
        this.jql = jql;
        this.ticketId = ticketId;
        this.assignee = assignee;
        this.status = status;
        this.eventType = eventType;
        this.jiraIssueLink = jiraIssueLink;
        this.jiraIssueIconUrl = jiraIssueIconUrl;
    }

    public String getKeyId() {
        return this.keyId;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getJql() {
        return this.jql;
    }

    public void setJql(String jql) {
        this.jql = jql;
    }

    public String getTicketId() {
        return this.ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getAssignee() {
        return this.assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEventType() {
        return this.eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getJiraIssueLink() {
        return this.jiraIssueLink;
    }

    public void setJiraIssueLink(String jiraIssueLink) {
        this.jiraIssueLink = jiraIssueLink;
    }

    public String getJiraIssueIconUrl() {
        return this.jiraIssueIconUrl;
    }

    public void setJiraIssueIconUrl(String jiraIssueIconUrl) {
        this.jiraIssueIconUrl = jiraIssueIconUrl;
    }
}

