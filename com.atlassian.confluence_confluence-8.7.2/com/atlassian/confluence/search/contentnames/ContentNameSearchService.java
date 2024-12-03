/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.contentnames;

import com.atlassian.confluence.search.actions.json.ContentNameSearchResult;
import com.atlassian.confluence.search.contentnames.ContentNameSearchContext;

public interface ContentNameSearchService {
    public static final int DEFAULT_MAX_RESULTS_PER_CATEGORY = -1;
    public static final int DEFAULT_TOTAL_MAX_RESULTS = -1;

    public ContentNameSearchResult search(String var1, ContentNameSearchContext var2);
}

