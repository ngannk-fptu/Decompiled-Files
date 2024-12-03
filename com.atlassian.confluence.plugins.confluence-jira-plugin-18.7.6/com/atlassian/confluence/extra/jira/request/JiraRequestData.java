/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.request;

import com.atlassian.confluence.extra.jira.JiraIssuesMacro;

public class JiraRequestData {
    private String requestData;
    private JiraIssuesMacro.Type requestType;

    public JiraRequestData(String requestData, JiraIssuesMacro.Type requestType) {
        this.requestData = requestData;
        this.requestType = requestType;
    }

    public String getRequestData() {
        return this.requestData;
    }

    public void setRequestData(String requestData) {
        this.requestData = requestData;
    }

    public JiraIssuesMacro.Type getRequestType() {
        return this.requestType;
    }

    public void setRequestType(JiraIssuesMacro.Type requestType) {
        this.requestType = requestType;
    }
}

