/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;

public interface HightlightParams {
    public String getPreTag();

    public String getPostTag();

    public String getEncoder();

    default public SearchQuery getQuery() {
        return MatchNoDocsQuery.getInstance();
    }
}

