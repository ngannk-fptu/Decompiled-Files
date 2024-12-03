/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin;

import com.atlassian.confluence.search.SearchResultRenderContext;
import com.atlassian.confluence.search.v2.SearchResult;

public interface SearchResultRenderer {
    public boolean canRender(SearchResult var1);

    public String render(SearchResult var1, SearchResultRenderContext var2);
}

