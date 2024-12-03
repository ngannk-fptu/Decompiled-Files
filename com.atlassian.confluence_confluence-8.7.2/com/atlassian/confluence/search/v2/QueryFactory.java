/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2;

import com.atlassian.confluence.search.v2.InvalidQueryException;
import com.atlassian.confluence.search.v2.SearchQuery;
import java.util.List;

public interface QueryFactory {
    public SearchQuery newQuery(String var1) throws InvalidQueryException;

    public SearchQuery newQuery(String var1, List var2) throws InvalidQueryException;
}

