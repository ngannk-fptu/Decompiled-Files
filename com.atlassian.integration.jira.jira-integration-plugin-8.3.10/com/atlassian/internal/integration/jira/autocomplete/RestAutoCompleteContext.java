/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.map.annotate.JsonSerialize
 */
package com.atlassian.internal.integration.jira.autocomplete;

import java.util.LinkedHashMap;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class RestAutoCompleteContext
extends LinkedHashMap<String, Object> {
    private static final String ISSUE_KEY = "issueKey";
    private static final String ISSUE_TYPE = "issueType";
    private static final String PROJECT_KEY = "projectKey";
    private static final String REST_TYPE = "restType";
    private static final String SERVER_ID = "serverId";
    private static final String TERM = "term";

    public String getIssueKey() {
        return (String)this.get(ISSUE_KEY);
    }

    public String getIssueType() {
        return (String)this.get(ISSUE_TYPE);
    }

    public String getProjectKey() {
        return (String)this.get(PROJECT_KEY);
    }

    public String getRestType() {
        return (String)this.get(REST_TYPE);
    }

    public String getServerId() {
        return (String)this.get(SERVER_ID);
    }

    public String getTerm() {
        return (String)this.get(TERM);
    }

    public boolean hasTerm() {
        return this.get(TERM) != null;
    }
}

