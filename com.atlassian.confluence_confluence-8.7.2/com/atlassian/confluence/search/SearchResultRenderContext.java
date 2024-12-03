/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search;

import java.util.HashMap;
import java.util.Map;

public class SearchResultRenderContext {
    private Map<String, String> context = new HashMap<String, String>();
    private static final String QUERY_STRING_KEY = "query.string";
    private static final String SHOW_EXCERPTS = "show.excerpts";

    public SearchResultRenderContext(String queryString, String showExcerpts) {
        this();
        this.context.put(QUERY_STRING_KEY, queryString);
        this.context.put(SHOW_EXCERPTS, showExcerpts);
    }

    public SearchResultRenderContext() {
    }

    public String getQueryString() {
        if (this.context.containsKey(QUERY_STRING_KEY)) {
            return this.context.get(QUERY_STRING_KEY);
        }
        return "";
    }

    public String getShowExcerpts() {
        if (this.context.containsKey(SHOW_EXCERPTS)) {
            return this.context.get(SHOW_EXCERPTS);
        }
        return "";
    }
}

