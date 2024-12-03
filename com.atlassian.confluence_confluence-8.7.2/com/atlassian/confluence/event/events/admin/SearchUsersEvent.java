/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.analytics.api.annotations.EventName
 */
package com.atlassian.confluence.event.events.admin;

import com.atlassian.analytics.api.annotations.EventName;
import com.atlassian.confluence.util.SearchTermType;
import java.util.Objects;

@EventName(value="confluence.admin.search.users")
public class SearchUsersEvent {
    private final boolean licensedUsersOnly;
    private final SearchTermType searchTermType;

    @Deprecated(forRemoval=true, since="8.4.2")
    public SearchUsersEvent(boolean licensedUsersOnly, String searchTermType) {
        this.licensedUsersOnly = licensedUsersOnly;
        this.searchTermType = SearchTermType.valueOf(searchTermType);
    }

    public SearchUsersEvent(boolean licensedUsersOnly, SearchTermType searchTermType) {
        this.licensedUsersOnly = licensedUsersOnly;
        this.searchTermType = searchTermType;
    }

    public boolean isLicensedUsersOnly() {
        return this.licensedUsersOnly;
    }

    public SearchTermType getType() {
        return this.searchTermType;
    }

    @Deprecated(forRemoval=true, since="8.4.2")
    public String getSearchTermType() {
        return this.searchTermType.toString();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        SearchUsersEvent that = (SearchUsersEvent)o;
        return this.licensedUsersOnly == that.licensedUsersOnly && this.searchTermType == that.searchTermType;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.licensedUsersOnly, this.searchTermType});
    }
}

