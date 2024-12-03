/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.confluence.plugin.SearchResultRenderer;
import com.atlassian.confluence.search.SearchResultRenderContext;
import com.atlassian.confluence.search.SearchResultRendererCache;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DelegatedSearchResultRenderer {
    private SearchResultRenderer defaultRenderer;
    private SearchResultRendererCache searchResultRendererCache;
    private static final Logger log = LoggerFactory.getLogger(DelegatedSearchResultRenderer.class);

    public String render(SearchResult searchResult, SearchResultRenderContext renderContext) {
        Iterator<SearchResultRenderer> iterator = this.searchResultRendererCache.getSearchReslultRenderers().iterator();
        String renderedHtml = null;
        while (iterator.hasNext() && StringUtils.isBlank(renderedHtml)) {
            SearchResultRenderer iSearchResultRenderer = iterator.next();
            if (!iSearchResultRenderer.canRender(searchResult)) continue;
            try {
                renderedHtml = iSearchResultRenderer.render(searchResult, renderContext);
            }
            catch (Exception e) {
                log.warn("SearchResultRenderer threw exception when rendering, please consider disabling this plugin", (Throwable)e);
            }
        }
        if (!StringUtils.isBlank(renderedHtml)) {
            return renderedHtml;
        }
        return this.defaultRenderer.render(searchResult, renderContext);
    }

    public void setDefaultRenderer(SearchResultRenderer defaultRenderer) {
        this.defaultRenderer = defaultRenderer;
    }

    public void setSearchResultRendererCache(SearchResultRendererCache cache) {
        this.searchResultRendererCache = cache;
    }
}

