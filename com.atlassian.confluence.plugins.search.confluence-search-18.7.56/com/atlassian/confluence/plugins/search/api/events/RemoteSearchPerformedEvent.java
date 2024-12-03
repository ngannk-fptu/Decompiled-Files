/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.search.SearchEvent
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.search.api.events;

import com.atlassian.confluence.event.events.search.SearchEvent;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.user.User;

public class RemoteSearchPerformedEvent
extends SearchEvent {
    private final SearchQuery searchQuery;
    private final User user;
    private final int numberOfResults;

    public RemoteSearchPerformedEvent(Object source, SearchQuery search, User user, int numberOfResults) {
        super(source);
        this.searchQuery = search;
        this.user = user;
        this.numberOfResults = numberOfResults;
    }

    public SearchQuery getSearchQuery() {
        return this.searchQuery;
    }

    public User getUser() {
        return this.user;
    }

    public int getNumberOfResults() {
        return this.numberOfResults;
    }
}

