/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.analytics.client.base;

import com.atlassian.analytics.api.annotations.EventName;

@EventName(value="analytics.jira.base.data")
public class JiraBaseDataEvent {
    private final long numProjects;
    private final long numIssues;
    private final long numWorkflows;
    private final int numStatuses;
    private final int numIssueTypes;
    private final long numUsers;
    private final String serverKey;

    public JiraBaseDataEvent(long numProjects, long numIssues, long numWorkflows, int numStatuses, int numIssueTypes, long numUsers, String serverKey) {
        this.numProjects = numProjects;
        this.numIssues = numIssues;
        this.numWorkflows = numWorkflows;
        this.numStatuses = numStatuses;
        this.numIssueTypes = numIssueTypes;
        this.numUsers = numUsers;
        this.serverKey = serverKey;
    }

    public long getNumProjects() {
        return this.numProjects;
    }

    public long getNumIssues() {
        return this.numIssues;
    }

    public long getNumWorkflows() {
        return this.numWorkflows;
    }

    public int getNumStatuses() {
        return this.numStatuses;
    }

    public int getNumIssueTypes() {
        return this.numIssueTypes;
    }

    public long getNumUsers() {
        return this.numUsers;
    }

    public String getServerKey() {
        return this.serverKey;
    }
}

