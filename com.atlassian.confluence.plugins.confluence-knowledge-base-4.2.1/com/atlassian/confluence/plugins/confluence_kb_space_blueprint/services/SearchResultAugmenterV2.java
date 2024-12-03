/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.search.v2.SearchResult
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.fugue.Option
 */
package com.atlassian.confluence.plugins.confluence_kb_space_blueprint.services;

import com.atlassian.confluence.search.v2.SearchResult;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.fugue.Option;
import java.util.List;

public interface SearchResultAugmenterV2 {
    public List<SearchResult> addViewPermissionChecksToResults(List<SearchResult> var1, Option<ConfluenceUser> var2);
}

