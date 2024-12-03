/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.AllQuery;
import java.util.Collections;
import java.util.List;

@SearchPrimitive
public class MatchNoDocsQuery
implements SearchQuery {
    public static final String KEY = "none";
    private static final MatchNoDocsQuery instance = new MatchNoDocsQuery();

    private MatchNoDocsQuery() {
    }

    public static MatchNoDocsQuery getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return Collections.EMPTY_LIST;
    }

    public int hashCode() {
        return AllQuery.class.hashCode();
    }

    public boolean equals(Object o) {
        return o != null && o.getClass().equals(this.getClass());
    }
}

