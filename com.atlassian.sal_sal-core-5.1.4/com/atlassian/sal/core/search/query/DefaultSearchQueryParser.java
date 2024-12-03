/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.search.query.SearchQuery
 *  com.atlassian.sal.api.search.query.SearchQueryParser
 */
package com.atlassian.sal.core.search.query;

import com.atlassian.sal.api.search.query.SearchQuery;
import com.atlassian.sal.api.search.query.SearchQueryParser;
import com.atlassian.sal.core.search.query.DefaultSearchQuery;

public class DefaultSearchQueryParser
implements SearchQueryParser {
    public SearchQuery parse(String query) {
        return new DefaultSearchQuery(query);
    }
}

