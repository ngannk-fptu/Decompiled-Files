/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.diagnostics.Issue
 */
package com.atlassian.diagnostics.internal.rest;

import com.atlassian.diagnostics.Issue;
import com.atlassian.diagnostics.internal.rest.RestComponent;
import com.atlassian.diagnostics.internal.rest.RestEntity;

public class RestIssue
extends RestEntity {
    public RestIssue(Issue issue) {
        this.put("id", issue.getId());
        this.put("component", new RestComponent(issue.getComponent()));
        this.put("summary", issue.getSummary());
        this.put("description", issue.getDescription());
        this.put("severity", issue.getSeverity());
    }
}

