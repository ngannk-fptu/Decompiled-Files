/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.search.ContainerSummary
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.model.search.SearchOptions
 *  com.atlassian.confluence.api.model.search.SearchPageResponse
 *  com.atlassian.confluence.api.model.search.SearchResult
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacroAdapter
 *  com.atlassian.confluence.spaces.Spaced
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.base.Strings
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.confluence.plugins.cql.macro;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.ContainerSummary;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.model.search.SearchOptions;
import com.atlassian.confluence.api.model.search.SearchPageResponse;
import com.atlassian.confluence.api.model.search.SearchResult;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacroAdapter;
import com.atlassian.confluence.spaces.Spaced;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;

public class CQLSearchMacro
extends StreamableMacroAdapter {
    private final CQLSearchService searchService;
    private final SoyTemplateRenderer soyTemplateRenderer;
    private final PageBuilderService pageBuilderService;

    @Autowired
    public CQLSearchMacro(CQLSearchService searchService, @ComponentImport SoyTemplateRenderer soyTemplateRenderer, @ComponentImport PageBuilderService pageBuilderService) {
        this.searchService = searchService;
        this.soyTemplateRenderer = soyTemplateRenderer;
        this.pageBuilderService = pageBuilderService;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    public Streamable executeToStream(Map<String, String> parameters, Streamable noBody, ConversionContext conversionContext) throws MacroExecutionException {
        String cql = parameters.get("cql");
        if (Strings.isNullOrEmpty((String)cql)) {
            throw new MacroExecutionException("cql parameter is required when rendering CQLSearchMacro");
        }
        SearchPageResponse<SearchResult> results = this.executeCqlQuery(cql, conversionContext);
        return this.renderResults(results);
    }

    private SearchPageResponse<SearchResult> executeCqlQuery(String cql, ConversionContext conversionContext) {
        ContentEntityObject page = conversionContext.getPageContext().getEntity();
        String spaceKey = null;
        if (page instanceof Spaced) {
            spaceKey = ((Spaced)page).getSpace().getKey();
        }
        SearchContext searchContext = SearchContext.builder().contentId(page.getContentId()).spaceKey(spaceKey).build();
        return this.searchService.search(cql, SearchOptions.builder().searchContext(searchContext).build(), (PageRequest)new SimplePageRequest(0, 10), new Expansion[0]);
    }

    private Streamable renderResults(SearchPageResponse<SearchResult> results) throws MacroExecutionException {
        this.pageBuilderService.assembler().resources().requireContext("confluence-cql-plugin");
        try {
            ImmutableMap templateParams = ImmutableMap.of((Object)"searchResults", Collections.unmodifiableList(results.getResults().stream().map(result -> {
                SearchResult input = result;
                String entityType = input.getEntityType();
                String type = entityType.equals("content") ? ((Content)input.getEntity()).getType().getValue() : entityType;
                Reference container = input.getResultParentRef().exists() ? input.getResultParentRef() : input.getResultGlobalContainerRef();
                String containerName = "";
                if (container.exists()) {
                    containerName = ((ContainerSummary)container.get()).getTitle();
                }
                return ImmutableMap.builder().put((Object)"url", (Object)input.getUrl()).put((Object)"bodyTextHighlights", (Object)input.getExcerpt()).put((Object)"searchResultContainer", (Object)containerName).put((Object)"friendlyDate", (Object)"TODO").put((Object)"contentType", (Object)type).put((Object)"title", (Object)input.getTitle()).put((Object)"metadata", (Object)Maps.newHashMap()).build();
            }).collect(Collectors.toList())), (Object)"size", (Object)results.size(), (Object)"start", (Object)results.getPageRequest().getStart(), (Object)"totalSize", (Object)results.totalSize(), (Object)"queryString", (Object)results.getCqlQuery());
            String renderedText = this.soyTemplateRenderer.render("com.atlassian.querylang.confluence-cql-plugin:confluence-cql-plugin-macro-resources", "Confluence.Templates.CQLMacro.searchResults", (Map)templateParams);
            return Streamables.from((String)renderedText);
        }
        catch (SoyException e) {
            throw new MacroExecutionException(String.format("Could not render search macro with query : '%s'\nGenerated %d results.", results.getCqlQuery(), results.totalSize()), (Throwable)e);
        }
    }
}

