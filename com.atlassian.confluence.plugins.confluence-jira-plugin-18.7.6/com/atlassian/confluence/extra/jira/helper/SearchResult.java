/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.extra.jira.helper;

import com.atlassian.confluence.extra.jira.helper.SearchIssue;
import java.util.ArrayList;
import java.util.List;

public class SearchResult {
    private List<SearchIssue> issues = new ArrayList<SearchIssue>();

    public List<SearchIssue> getIssues() {
        return this.issues;
    }

    public void setIssues(List<SearchIssue> issues) {
        this.issues = issues;
    }
}

