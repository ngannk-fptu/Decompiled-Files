/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.search.v2.SearchPrimitive;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.Collections;
import java.util.List;

@SearchPrimitive
public class AllQuery
implements SearchQuery {
    public static final AllQuery instance = new AllQuery();
    public static final String KEY = "all";

    private AllQuery() {
    }

    public static AllQuery getInstance() {
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

