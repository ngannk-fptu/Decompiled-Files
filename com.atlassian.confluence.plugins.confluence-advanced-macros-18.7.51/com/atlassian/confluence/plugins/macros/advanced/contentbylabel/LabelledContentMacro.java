/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.ContentFilteringMacro
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.params.ParameterException
 *  com.atlassian.confluence.renderer.radeox.macros.MacroUtils
 *  com.atlassian.confluence.search.v2.SearchSort
 *  com.atlassian.confluence.search.v2.SearchSort$Order
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.atlassian.plugin.webresource.WebResourceManager
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.atlassian.spring.container.ContainerManager
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.ContentFilteringMacro;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.plugins.macros.advanced.analytics.LabelledContentMacroMetrics;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.AdvancedMacrosExcerpter;
import com.atlassian.confluence.plugins.macros.advanced.xhtml.ExcerptType;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.atlassian.plugin.webresource.WebResourceManager;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.spring.container.ContainerManager;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class LabelledContentMacro
extends ContentFilteringMacro {
    private static final String TEMPLATE_NAME = "com/atlassian/confluence/plugins/macros/advanced/contentbylabel/labelledcontent.vm";
    private static final String SPACE_ALL = "@all";
    private ConfluenceActionSupport confluenceActionSupport;
    private WebResourceManager webResourceManager;
    private ContentEntityManager contentEntityManager;
    private AdvancedMacrosExcerpter advancedMacrosExcerpter;
    private EventPublisher eventPublisher;
    private CQLSearchService searchService;

    public LabelledContentMacro() {
        this.spaceKeyParam.addParameterAlias("key");
        this.spaceKeyParam.setDefaultValue(SPACE_ALL);
        this.maxResultsParam.addParameterAlias("maxResults");
        this.maxResultsParam.setDefaultValue("15");
    }

    public boolean isInline() {
        return false;
    }

    public boolean hasBody() {
        return false;
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public String execute(MacroExecutionContext ctx) throws MacroException {
        LabelledContentMacroMetrics.Builder metrics = LabelledContentMacroMetrics.builder();
        String cql = this.constructCqlQuery(ctx);
        Map<String, Object> contextMap = this.getMacroVelocityContext();
        contextMap.putAll(this.makeRenderContext(ctx, cql, metrics));
        this.webResourceManager.requireResource("confluence.macros.advanced:content-by-label-resources");
        try {
            metrics.templateRenderStart();
            String string = this.render(contextMap);
            return string;
        }
        finally {
            metrics.templateRenderFinish().publish(this.eventPublisher);
        }
    }

    @VisibleForTesting
    Map<String, Object> makeRenderContext(MacroExecutionContext ctx, String cql, LabelledContentMacroMetrics.Builder metrics) throws MacroException {
        SimplePageRequest pageRequest = this.buildPageRequest(ctx);
        SearchContext searchContext = ctx.getPageContext().toSearchContext().build();
        metrics.contentSearchStart((PageRequest)pageRequest);
        PageResponse response = this.getSearchService().searchContent(cql, searchContext, (PageRequest)pageRequest, new Expansion[0]);
        metrics.contentSearchFinish((PageResponse<Content>)response);
        metrics.fetchContentEntitiesStart();
        List<ContentEntityObject> contents = this.asContentEntityObjects((PageResponse<Content>)response);
        metrics.fetchContentEntitiesFinish();
        Map parameters = ctx.getParams();
        String title = (String)parameters.get("title");
        boolean limitLabelLinksToSpace = cql.contains("space = ");
        HashMap contextMap = Maps.newHashMap();
        contextMap.put("title", title);
        contextMap.put("contents", contents);
        contextMap.put("showLabels", this.getBooleanParameter((String)parameters.get("showLabels"), true));
        contextMap.put("showSpace", this.getBooleanParameter((String)parameters.get("showSpace"), true));
        contextMap.put("limitLabelLinksToSpace", limitLabelLinksToSpace);
        contextMap.put("excerptType", ExcerptType.fromString((String)parameters.get("excerptType")));
        contextMap.put("excerpter", this.advancedMacrosExcerpter);
        return contextMap;
    }

    private String constructCqlQuery(MacroExecutionContext ctx) throws MacroException {
        Map params = ctx.getParams();
        Object cql = (String)params.get("cql");
        if (this.getBooleanParameter((String)params.get("excludeCurrent"), false).booleanValue()) {
            cql = "(" + (String)cql + ") and content != currentContent()";
        }
        cql = (String)cql + this.buildOrderByClause(ctx);
        return cql;
    }

    private String buildOrderByClause(MacroExecutionContext ctx) throws MacroException {
        SearchSort searchSort;
        try {
            searchSort = (SearchSort)this.sortParam.findValue(ctx);
        }
        catch (ParameterException pe) {
            throw new MacroException(this.getConfluenceActionSupport().getText("contentbylabel.error.parse-reverse-or-sort-param"), (Throwable)pe);
        }
        if (searchSort == null) {
            return "";
        }
        String direction = searchSort.getOrder() == SearchSort.Order.DESCENDING ? " desc" : "";
        String key = searchSort.getKey();
        if (key.equals("modified")) {
            key = "lastModified";
        }
        return " order by " + key + direction;
    }

    private SimplePageRequest buildPageRequest(MacroExecutionContext ctx) throws MacroException {
        Integer maxResults;
        try {
            maxResults = (Integer)this.maxResultsParam.findValue(ctx);
        }
        catch (ParameterException pe) {
            throw new MacroException(this.getConfluenceActionSupport().getText("contentbylabel.error.parse-max-labels-param"), (Throwable)pe);
        }
        if (maxResults == 0) {
            maxResults = 15;
        }
        return new SimplePageRequest(0, maxResults.intValue());
    }

    private List<ContentEntityObject> asContentEntityObjects(PageResponse<Content> response) {
        List results = response.getResults();
        ArrayList ceos = Lists.newArrayList();
        for (Content result : results) {
            ceos.add(this.contentEntityManager.getById(result.getId().asLong()));
        }
        return ceos;
    }

    private CQLSearchService getSearchService() {
        if (this.searchService == null) {
            OsgiContainerManager osgiContainerManager = (OsgiContainerManager)ContainerManager.getComponent((String)"osgiContainerManager");
            this.setSearchService((CQLSearchService)osgiContainerManager.getServiceTracker(CQLSearchService.class.getName()).getService());
        }
        return this.searchService;
    }

    public void setSearchService(CQLSearchService searchService) {
        this.searchService = searchService;
    }

    protected Map<String, Object> getMacroVelocityContext() {
        return MacroUtils.defaultVelocityContext();
    }

    protected String render(Map<String, Object> contextMap) {
        return VelocityUtils.getRenderedTemplate((String)TEMPLATE_NAME, contextMap);
    }

    private Boolean getBooleanParameter(String booleanValue, boolean defaultValue) {
        if (StringUtils.isNotBlank((CharSequence)booleanValue)) {
            return Boolean.valueOf(booleanValue);
        }
        return defaultValue;
    }

    public void setContentEntityManager(ContentEntityManager contentEntityManager) {
        this.contentEntityManager = contentEntityManager;
    }

    protected ConfluenceActionSupport getConfluenceActionSupport() {
        if (null == this.confluenceActionSupport) {
            this.confluenceActionSupport = new ConfluenceActionSupport();
            ContainerManager.autowireComponent((Object)this.confluenceActionSupport);
        }
        return this.confluenceActionSupport;
    }

    public void setWebResourceManager(WebResourceManager webResourceManager) {
        this.webResourceManager = webResourceManager;
    }

    public void setAdvancedMacrosExcerpter(AdvancedMacrosExcerpter advancedMacrosExcerpter) {
        this.advancedMacrosExcerpter = advancedMacrosExcerpter;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }
}

