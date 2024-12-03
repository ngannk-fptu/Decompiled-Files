/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.atlassian.renderer.RenderContext
 *  javax.ws.rs.DefaultValue
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response$Status
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.extra.masterdetail.rest;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.ContentRetriever;
import com.atlassian.confluence.extra.masterdetail.ContentRetrieverResult;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryBuilder;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryParameters;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.entities.PaginatedDetailLines;
import com.atlassian.confluence.extra.masterdetail.rest.DetailsSummaryLines;
import com.atlassian.confluence.extra.masterdetail.rest.ResourceErrorType;
import com.atlassian.confluence.extra.masterdetail.rest.ResourceException;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.atlassian.renderer.RenderContext;
import java.util.List;
import java.util.Objects;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;

@AnonymousAllowed
@Path(value="detailssummary")
public class DetailsSummaryResource {
    private final DetailsSummaryBuilder detailsSummaryBuilder;
    private final ContentRetriever contentRetriever;
    private final EventPublisher eventPublisher;
    private final ContentEntityManager contentEntityManager;

    public DetailsSummaryResource(@ComponentImport EventPublisher eventPublisher, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, ContentRetriever contentRetriever, DetailsSummaryBuilder detailsSummaryBuilder) {
        this.eventPublisher = eventPublisher;
        this.contentEntityManager = contentEntityManager;
        this.contentRetriever = contentRetriever;
        this.detailsSummaryBuilder = detailsSummaryBuilder;
    }

    @GET
    @Path(value="lines")
    @Produces(value={"application/json"})
    public DetailsSummaryLines getDetailLines(@QueryParam(value="cql") String cql, @QueryParam(value="detailsId") String detailsId, @QueryParam(value="sortBy") String sortBy, @QueryParam(value="reverseSort") @DefaultValue(value="false") boolean reverseSort, @QueryParam(value="spaceKey") String contextSpaceKey, @QueryParam(value="contentId") ContentId contentId, @QueryParam(value="pageSize") @DefaultValue(value="30") int pageSize, @QueryParam(value="pageIndex") @DefaultValue(value="0") int currentPage, @QueryParam(value="countComments") @DefaultValue(value="false") boolean countComments, @QueryParam(value="countLikes") @DefaultValue(value="false") boolean countLikes, @QueryParam(value="headings") String headings) {
        if (StringUtils.isEmpty((CharSequence)contextSpaceKey)) {
            throw new ResourceException("'spaceKey' parameter is required", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_MISSING, (Object)"spaceKey");
        }
        if (pageSize <= 0) {
            throw new ResourceException("Requested page size is not valid", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_INVALID, (Object)"pageSize");
        }
        if (currentPage < 0) {
            throw new ResourceException("Requested page index is not valid", Response.Status.BAD_REQUEST, ResourceErrorType.PARAMETER_INVALID, (Object)"pageIndex");
        }
        try {
            DetailsSummaryMacroMetricsEvent.Builder metrics = DetailsSummaryMacroMetricsEvent.builder(DetailsSummaryMacroMetricsEvent.Type.REST_RESOURCE);
            SearchContext searchContext = SearchContext.builder().spaceKey(contextSpaceKey).contentId(contentId).build();
            ContentRetrieverResult contentRetrieverResult = this.contentRetriever.getContentWithMetaData(cql, reverseSort, searchContext, metrics);
            List<ContentEntityObject> content = contentRetrieverResult.getRows();
            DetailsSummaryParameters summaryParams = new DetailsSummaryParameters().setPageSize(pageSize).setCurrentPage(currentPage).setCountComments(countComments).setCountLikes(countLikes).setHeadingsString(headings).setSortBy(sortBy).setReverseSort(reverseSort).setContent(content).setId(detailsId);
            ContentEntityObject reportPageCEO = null;
            if (Objects.nonNull(contentId)) {
                reportPageCEO = this.contentEntityManager.getById(contentId.asLong());
            }
            DefaultConversionContext newConversionContext = new DefaultConversionContext((RenderContext)new PageContext(reportPageCEO), ConversionContextOutputType.DISPLAY.value());
            PaginatedDetailLines paginatedDetailLines = this.detailsSummaryBuilder.getPaginatedDetailLines(summaryParams, true, metrics, (ConversionContext)newConversionContext);
            this.eventPublisher.publish((Object)metrics.build());
            return new DetailsSummaryLines(summaryParams.getCurrentPage(), summaryParams.getTotalPages(), paginatedDetailLines.getRenderedHeadings(), paginatedDetailLines.getDetailLines(), paginatedDetailLines.isAsyncRenderSafe());
        }
        catch (MacroExecutionException e) {
            throw new ResourceException(e, Response.Status.INTERNAL_SERVER_ERROR, ResourceErrorType.RENDERING_MACRO);
        }
    }
}

