/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.service;

import com.atlassian.confluence.search.service.RecentUpdateQueryParameters;
import com.atlassian.confluence.search.service.SearchQueryParameters;
import com.atlassian.confluence.search.service.UserSearchQueryParameters;
import com.atlassian.confluence.search.v2.ISearch;

public interface PredefinedSearchBuilder {
    public ISearch buildSiteSearch(SearchQueryParameters var1, int var2, int var3);

    public ISearch buildUsersSearch(String var1, int var2);

    public ISearch buildUsersSearch(UserSearchQueryParameters var1, int var2, int var3);

    public ISearch buildRecentUpdateSearch(RecentUpdateQueryParameters var1, int var2, int var3);
}

