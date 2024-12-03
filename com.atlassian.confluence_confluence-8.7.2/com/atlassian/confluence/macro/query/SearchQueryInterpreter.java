/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.query;

import com.atlassian.confluence.macro.query.SearchQueryInterpreterException;
import com.atlassian.confluence.search.v2.SearchQuery;

public interface SearchQueryInterpreter {
    public SearchQuery createSearchQuery(String var1) throws SearchQueryInterpreterException;
}

