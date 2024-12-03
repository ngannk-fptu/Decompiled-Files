/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.search;

import com.atlassian.confluence.plugin.SearchResultRenderer;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.search.SearchResultRenderContext;
import com.atlassian.confluence.search.v2.SearchResult;
import java.util.Map;

public class DefaultSearchResultResultRenderer
implements SearchResultRenderer {
    private final VelocityHelperService velocityService;

    public DefaultSearchResultResultRenderer(VelocityHelperService velocityService) {
        this.velocityService = velocityService;
    }

    @Override
    public boolean canRender(SearchResult searchResult) {
        return true;
    }

    @Override
    public String render(SearchResult searchResult, SearchResultRenderContext renderContext) {
        return this.render(searchResult, renderContext, MacroUtils.defaultVelocityContext());
    }

    String render(SearchResult searchResult, SearchResultRenderContext renderContext, Map<String, Object> velocityContext) {
        return this.velocityService.getRenderedTemplate("decorators/components/default-search-result-item.vmd", this.populateVelocityContext(searchResult, renderContext, velocityContext));
    }

    private Map<String, Object> populateVelocityContext(SearchResult searchResult, SearchResultRenderContext renderContext, Map<String, Object> context) {
        context.put("searchResult", searchResult);
        context.put("showExcerpts", renderContext.getShowExcerpts());
        context.put("queryString", renderContext.getQueryString());
        return context;
    }
}

