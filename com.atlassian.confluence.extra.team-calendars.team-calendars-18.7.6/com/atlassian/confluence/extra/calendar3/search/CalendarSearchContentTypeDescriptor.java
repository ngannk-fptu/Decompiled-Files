/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.CustomContentTypeQuery
 */
package com.atlassian.confluence.extra.calendar3.search;

import com.atlassian.confluence.search.plugin.ContentTypeSearchDescriptor;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.CustomContentTypeQuery;

public class CalendarSearchContentTypeDescriptor
implements ContentTypeSearchDescriptor {
    public String getIdentifier() {
        return "com.atlassian.confluence.extra.team-calendars:calendar-content-type";
    }

    public String getI18NKey() {
        return "calendar3.searchtype.name";
    }

    public boolean isIncludedInDefaultSearch() {
        return true;
    }

    public SearchQuery getQuery() {
        return new CustomContentTypeQuery(new String[]{"com.atlassian.confluence.extra.team-calendars:calendar-content-type"});
    }
}

