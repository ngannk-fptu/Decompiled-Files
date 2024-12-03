/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.fugue.Either
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.google.common.collect.Maps
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Context
 *  javax.ws.rs.core.UriInfo
 */
package com.atlassian.confluence.contributors.rest;

import com.atlassian.confluence.contributors.analytics.ContributorsMacroMetricsEvent;
import com.atlassian.confluence.contributors.macro.ContributorsMacroHelper;
import com.atlassian.confluence.contributors.macro.MacroParameterModel;
import com.atlassian.confluence.contributors.search.PageSearcher;
import com.atlassian.confluence.contributors.util.PageProcessor;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.fugue.Either;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.google.common.collect.Maps;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path(value="contributors")
public class ContributorsMacroResource {
    private final ContributorsMacroHelper macroHelper;
    private final PageManager pageManager;
    private final EventPublisher eventPublisher;

    public ContributorsMacroResource(PageSearcher pageSearcher, PageProcessor pageProcessor, @ComponentImport PageManager pageManager, @ComponentImport EventPublisher eventPublisher) {
        this.pageManager = pageManager;
        this.eventPublisher = eventPublisher;
        this.macroHelper = new ContributorsMacroHelper(pageProcessor, pageSearcher);
    }

    @GET
    @Produces(value={"application/json"})
    public Map<String, Object> getContributors(@Context UriInfo uriInfo, @QueryParam(value="contextEntityId") Long contextEntityId) {
        ContributorsMacroMetricsEvent.Builder dummyMetrics = ContributorsMacroMetricsEvent.builder();
        Either<String, Map<String, Object>> results = this.macroHelper.getAuthorRankingsModel(dummyMetrics, this.parameterModel(uriInfo, contextEntityId));
        this.eventPublisher.publish((Object)dummyMetrics.build());
        if (results.isLeft()) {
            return Collections.singletonMap("errorMessage", results.left().get());
        }
        return (Map)results.right().get();
    }

    private MacroParameterModel parameterModel(@Context UriInfo uriInfo, @QueryParam(value="contextEntityId") Long contextEntityId) {
        Map parameterMap = Maps.transformValues((Map)uriInfo.getQueryParameters(), input -> (String)input.get(0));
        SpaceContentEntityObject contextEntity = (SpaceContentEntityObject)Objects.requireNonNull(this.pageManager.getAbstractPage(contextEntityId.longValue()), "No page found by ID " + contextEntityId);
        return new MacroParameterModel(parameterMap, contextEntity);
    }
}

