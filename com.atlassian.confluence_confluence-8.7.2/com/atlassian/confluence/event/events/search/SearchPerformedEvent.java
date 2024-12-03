/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.atlassian.confluence.event.events.search;

import com.atlassian.confluence.event.events.search.SearchEvent;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.user.User;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.UUID;

@SuppressFBWarnings(value={"SE_NO_SERIALVERSIONID"})
public class SearchPerformedEvent
extends SearchEvent {
    private final SearchQuery searchQuery;
    private final User user;
    private final int numberOfResults;
    private final String uuid;

    public SearchPerformedEvent(Object source, SearchQuery search, User user, int numberOfResults) {
        super(source);
        this.searchQuery = search;
        this.user = user;
        this.numberOfResults = numberOfResults;
        this.uuid = UUID.randomUUID().toString();
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

    public String getUuid() {
        return this.uuid;
    }
}

