/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.event.events.search.SearchPerformedEvent
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.events;

import com.atlassian.confluence.event.events.search.SearchPerformedEvent;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.user.User;

public class KbSearchPerformedEvent
extends SearchPerformedEvent {
    public KbSearchPerformedEvent(Object source, SearchQuery search, User user, int numberOfResults) {
        super(source, search, user, numberOfResults);
    }
}

