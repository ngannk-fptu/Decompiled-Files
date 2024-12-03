/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.user.User;

public class SiteSearchAuditEvent {
    private final String queryString;
    private final User searchPerformer;

    public SiteSearchAuditEvent(String queryString, User searchPerformer) {
        this.queryString = queryString;
        this.searchPerformer = searchPerformer;
    }

    public String getQueryString() {
        return this.queryString;
    }

    public User getSearchPerformer() {
        return this.searchPerformer;
    }
}

