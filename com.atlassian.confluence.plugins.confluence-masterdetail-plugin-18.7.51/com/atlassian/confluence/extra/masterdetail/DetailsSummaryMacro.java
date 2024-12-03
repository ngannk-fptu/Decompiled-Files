/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.Streamable
 *  com.atlassian.confluence.content.render.xhtml.Streamables
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.core.SpaceContentEntityObject
 *  com.atlassian.confluence.macro.Macro$BodyType
 *  com.atlassian.confluence.macro.Macro$OutputType
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.macro.StreamableMacro
 *  com.atlassian.confluence.pages.Draft
 *  com.atlassian.confluence.plugins.createcontent.api.services.CreateButtonService
 *  com.atlassian.confluence.renderer.template.TemplateRenderer
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.ModuleCompleteKey
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.TokenType
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.renderer.v2.RenderUtils
 *  com.atlassian.renderer.v2.macro.BaseMacro
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.webresource.api.assembler.PageBuilderService
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.Streamable;
import com.atlassian.confluence.content.render.xhtml.Streamables;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.SpaceContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.ContentRetriever;
import com.atlassian.confluence.extra.masterdetail.ContentRetrieverResult;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryBuilder;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryMacroThreadLocalContext;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryParameters;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryRenderingStrategy;
import com.atlassian.confluence.extra.masterdetail.MasterDetailConfigurator;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailSummaryMacroFailedEvent;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroNestedRecursiveEvent;
import com.atlassian.confluence.extra.masterdetail.entities.PaginatedDetailLines;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.macro.StreamableMacro;
import com.atlassian.confluence.pages.Draft;
import com.atlassian.confluence.plugins.createcontent.api.services.CreateButtonService;
import com.atlassian.confluence.renderer.template.TemplateRenderer;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.ModuleCompleteKey;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.TokenType;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.renderer.v2.RenderUtils;
import com.atlassian.renderer.v2.macro.BaseMacro;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.webresource.api.assembler.PageBuilderService;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.lang3.StringUtils;

@ParametersAreNonnullByDefault
public class DetailsSummaryMacro
extends BaseMacro
implements StreamableMacro {
    public static final String PARAM_ID = "id";
    public static final String PARAM_SPACE_KEY = "spaceKey";
    public static final String PARAM_CONTENT_ID = "contentId";
    private static final String PARAM_SHOW_COMMENTS_COUNT = "showCommentsCount";
    private static final String PARAM_SHOW_LIKES_COUNT = "showLikesCount";
    static final String PARAM_PAGE_SIZE = "pageSize";
    private static final String PARAM_SORT_BY = "sortBy";
    private static final String PARAM_REVERSE_SORT = "reverseSort";
    private static final String PARAM_HEADINGS = "headings";
    private static final String TEMPLATE_PARAM_TOTAL_PAGES = "totalPages";
    private static final String TEMPLATE_PARAM_CURRENT_PAGE = "currentPage";
    private static final String TEMPLATE_PARAM_PAGE_SIZE = "pageSize";
    private static final String TEMPLATE_PARAM_LABEL = "label";
    private static final String TEMPLATE_PARAM_CQL = "cql";
    private static final String TEMPLATE_PARAM_HEADINGS = "headings";
    private static final String TEMPLATE_PARAM_DETAILS = "details";
    private static final String TEMPLATE_PARAM_BLUEPRINT_PRESENT = "blueprintPresent";
    private static final String TEMPLATE_PARAM_LIMIT_REACHED = "limitReached";
    private static final String TEMPLATE_PARAM_WARNING_LINK = "warningLink";
    public static final int DEFAULT_PAGE_SIZE = 30;
    public static final int MAX_PAGESIZE = 1000;
    public static final int PARAM_ID_MAX_LENGTH = 256;
    public static final String CONTEXT_RECURSION_DEPTH_PROPERTY = "details_summary_depth";
    private final Supplier<Integer> maxResultsSupplier;
    private final Supplier<Integer> maxRecursionDepthSupplier;
    private final XhtmlContent xhtmlContent;
    private final PageBuilderService pageBuilderService;
    private final I18nResolver i18nResolver;
    private final ContentRetriever contentRetriever;
    private final DetailsSummaryBuilder detailsSummaryBuilder;
    private final SpaceManager spaceManager;
    private final TemplateRenderer templateRenderer;
    private final CreateButtonService createButtonService;
    private final EventPublisher eventPublisher;

    public DetailsSummaryMacro(@ComponentImport I18nResolver i18nResolver, @ComponentImport XhtmlContent xhtmlContent, @ComponentImport SpaceManager spaceManager, @ComponentImport PageBuilderService pageBuilderService, @ComponentImport CreateButtonService createButtonService, ContentRetriever contentRetriever, DetailsSummaryBuilder detailsSummaryBuilder, @ComponentImport TemplateRenderer templateRenderer, @ComponentImport EventPublisher eventPublisher, MasterDetailConfigurator configurator) {
        this.xhtmlContent = xhtmlContent;
        this.pageBuilderService = pageBuilderService;
        this.templateRenderer = templateRenderer;
        this.createButtonService = createButtonService;
        this.contentRetriever = contentRetriever;
        this.i18nResolver = i18nResolver;
        this.detailsSummaryBuilder = detailsSummaryBuilder;
        this.spaceManager = spaceManager;
        this.eventPublisher = eventPublisher;
        this.maxResultsSupplier = configurator::getPagePropertiesReportContentRetrieverMaxResult;
        this.maxRecursionDepthSupplier = configurator::getPagePropertiesReportMaximumRecursionDepth;
    }

    public String execute(Map macroParameters, String bodyWikiMarkup, RenderContext renderContext) {
        ArrayList wikiMarkupToStorageConversionErrors = Lists.newArrayList();
        DefaultConversionContext conversionContext = new DefaultConversionContext(renderContext);
        String storageFormatBody = this.xhtmlContent.convertWikiToStorage(bodyWikiMarkup, (ConversionContext)conversionContext, (List)wikiMarkupToStorageConversionErrors);
        return this.execute((Map<String, String>)macroParameters, storageFormatBody, (ConversionContext)conversionContext);
    }

    public String execute(Map<String, String> parameters, String body, ConversionContext context) {
        return Streamables.writeToString((Streamable)this.executeToStream(parameters, Streamables.from((String)body), context));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Streamable executeToStream(Map<String, String> macroParameters, Streamable body, ConversionContext conversionContext) {
        try {
            this.validateContextOwner(conversionContext);
            String id = macroParameters.get(PARAM_ID);
            if (id != null && id.length() > 256) {
                throw new MacroExecutionException(this.i18nResolver.getText("details.error.id.length"));
            }
            DetailsSummaryMacroMetricsEvent.Builder metrics = DetailsSummaryMacroMetricsEvent.builder(DetailsSummaryMacroMetricsEvent.Type.MACRO_EXECUTION).maxResultConfig(this.maxResultsSupplier.get()).macroOutputType(conversionContext.getOutputType());
            metrics.startReport();
            int recursionDepth = (Integer)Optional.ofNullable(conversionContext.getProperty(CONTEXT_RECURSION_DEPTH_PROPERTY)).orElseGet(DetailsSummaryMacroThreadLocalContext::getContextRecursionDepth);
            if (recursionDepth >= this.maxRecursionDepthSupplier.get()) {
                this.eventPublisher.publish((Object)new DetailsSummaryMacroNestedRecursiveEvent());
                throw new MacroExecutionException(this.i18nResolver.getText("detailssummary.error.nested.recursive"));
            }
            conversionContext.setProperty(CONTEXT_RECURSION_DEPTH_PROPERTY, (Object)(++recursionDepth));
            Streamable streamable = this.streamResponse(metrics, this.buildTemplateModel(macroParameters, conversionContext, metrics));
            return streamable;
        }
        catch (MacroExecutionException e) {
            String message = e.getMessage();
            this.eventPublisher.publish((Object)new DetailSummaryMacroFailedEvent());
            Streamable streamable = Streamables.from((String)RenderUtils.blockError((String)message, (String)""));
            return streamable;
        }
        finally {
            conversionContext.setProperty(CONTEXT_RECURSION_DEPTH_PROPERTY, null);
        }
    }

    private Streamable streamResponse(DetailsSummaryMacroMetricsEvent.Builder metrics, Map<String, Object> templateModel) {
        this.pageBuilderService.assembler().resources().requireWebResource("confluence.extra.masterdetail:master-details-resources");
        return writer -> {
            metrics.templateRenderStart();
            this.templateRenderer.renderTo((Appendable)writer, "confluence.extra.masterdetail:master-details-resources", "Confluence.Templates.Macro.MasterDetail.detailsSummary.soy", templateModel);
            metrics.templateRenderFinish();
            metrics.finishReport();
            this.eventPublisher.publish((Object)metrics.build());
        };
    }

    Map<String, Object> buildTemplateModel(Map<String, String> macroParameters, ConversionContext conversionContext, DetailsSummaryMacroMetricsEvent.Builder metrics) throws MacroExecutionException {
        Space currentSpace = this.spaceManager.getSpace(conversionContext.getSpaceKey());
        String contentId = "";
        if (conversionContext.getPageContext() != null && conversionContext.getPageContext().getEntity() != null) {
            contentId = conversionContext.getPageContext().getEntity().getIdAsString();
        }
        macroParameters.put(PARAM_CONTENT_ID, contentId);
        if (currentSpace == null) {
            throw new MacroExecutionException("ConversionContext returned invalid space key '" + conversionContext.getSpaceKey() + "'");
        }
        DetailsSummaryRenderingStrategy renderingStrategy = DetailsSummaryRenderingStrategy.strategyFor(conversionContext);
        String cql = macroParameters.get(TEMPLATE_PARAM_CQL);
        SearchContext searchContext = conversionContext.getPageContext().toSearchContext().build();
        boolean reverseSort = this.getBooleanParam(macroParameters, PARAM_REVERSE_SORT);
        List<ContentEntityObject> content = new ArrayList<ContentEntityObject>();
        if (renderingStrategy == DetailsSummaryRenderingStrategy.SERVER_SIDE) {
            ContentRetrieverResult contentResult = this.contentRetriever.getContentWithMetaData(cql, reverseSort, searchContext, metrics);
            content = contentResult.getRows();
            if (contentResult.isLimited()) {
                macroParameters.put("limitedRows", String.valueOf(this.maxResultsSupplier.get()));
                macroParameters.put(TEMPLATE_PARAM_WARNING_LINK, "/dosearchsite.action?cql=" + HtmlUtil.urlEncode((String)cql.replace("currentSpace()", currentSpace.getKey())));
            }
        }
        DetailsSummaryParameters params = this.getDetailsSummaryParameters(macroParameters, content, renderingStrategy, conversionContext);
        PaginatedDetailLines paginatedDetailLines = this.getPaginatedDetailLines(renderingStrategy, metrics, params, conversionContext);
        BlueprintParameters blueprint = new BlueprintParameters(macroParameters, currentSpace);
        return this.buildTemplateModel(macroParameters, paginatedDetailLines, params, blueprint);
    }

    private DetailsSummaryParameters getDetailsSummaryParameters(Map<String, String> macroParameters, List<ContentEntityObject> content, DetailsSummaryRenderingStrategy renderingStrategy, ConversionContext conversionContext) throws MacroExecutionException {
        String id;
        boolean currentPage = false;
        int pageSize = ConversionContextOutputType.PDF.value().equals(conversionContext.getOutputType()) || ConversionContextOutputType.WORD.value().equals(conversionContext.getOutputType()) || ConversionContextOutputType.HTML_EXPORT.value().equals(conversionContext.getOutputType()) ? 1000 : renderingStrategy.calculatePageSize(macroParameters);
        if (pageSize <= 0) {
            pageSize = 30;
        }
        if ((id = macroParameters.get(PARAM_ID)) != null && id.length() > 256) {
            throw new MacroExecutionException(this.i18nResolver.getText("details.error.id.length"));
        }
        boolean countComments = this.getBooleanParam(macroParameters, PARAM_SHOW_COMMENTS_COUNT);
        boolean countLikes = this.getBooleanParam(macroParameters, PARAM_SHOW_LIKES_COUNT);
        String sortBy = macroParameters.get(PARAM_SORT_BY);
        boolean reverseSort = this.getBooleanParam(macroParameters, PARAM_REVERSE_SORT);
        String headings = macroParameters.get("headings");
        return new DetailsSummaryParameters().setPageSize(pageSize).setCurrentPage(0).setCountComments(countComments).setCountLikes(countLikes).setHeadingsString(headings).setSortBy(sortBy).setReverseSort(reverseSort).setContent(content).setId(id);
    }

    private PaginatedDetailLines getPaginatedDetailLines(DetailsSummaryRenderingStrategy renderingStrategy, DetailsSummaryMacroMetricsEvent.Builder metrics, DetailsSummaryParameters params, ConversionContext conversersionContext) {
        if (renderingStrategy == DetailsSummaryRenderingStrategy.SERVER_SIDE) {
            return this.detailsSummaryBuilder.getPaginatedDetailLines(params, false, metrics, conversersionContext);
        }
        return null;
    }

    private Map<String, Object> buildTemplateModel(Map<String, String> macroParameters, @Nullable PaginatedDetailLines paginatedDetailLines, DetailsSummaryParameters params, BlueprintParameters blueprint) {
        HashMap model = Maps.newHashMap();
        String firstColumnParam = macroParameters.get("firstcolumn");
        String firstColumnHeading = StringUtils.isBlank((CharSequence)firstColumnParam) ? this.i18nResolver.getText("detailssummary.heading.title") : firstColumnParam;
        model.put("firstColumnHeading", firstColumnHeading);
        model.put(TEMPLATE_PARAM_CQL, macroParameters.get(TEMPLATE_PARAM_CQL));
        model.put(TEMPLATE_PARAM_LABEL, macroParameters.get(TEMPLATE_PARAM_LABEL));
        model.put("macroHeadings", macroParameters.get("headings"));
        if (paginatedDetailLines != null) {
            model.put("renderedHeadings", paginatedDetailLines.getRenderedHeadings());
            model.put(TEMPLATE_PARAM_DETAILS, paginatedDetailLines.getDetailLines());
        }
        model.put(PARAM_SORT_BY, params.getSortBy());
        model.put(PARAM_ID, macroParameters.get(PARAM_ID));
        model.put(PARAM_SPACE_KEY, blueprint.currentSpace.getKey());
        model.put(PARAM_CONTENT_ID, macroParameters.get(PARAM_CONTENT_ID));
        model.put(PARAM_REVERSE_SORT, params.isReverseSort());
        model.put("analyticsKey", macroParameters.get("analytics-key"));
        model.put(PARAM_SHOW_COMMENTS_COUNT, this.getBooleanParam(macroParameters, PARAM_SHOW_COMMENTS_COUNT));
        model.put(PARAM_SHOW_LIKES_COUNT, this.getBooleanParam(macroParameters, PARAM_SHOW_LIKES_COUNT));
        model.put(TEMPLATE_PARAM_TOTAL_PAGES, params.getTotalPages());
        model.put(TEMPLATE_PARAM_CURRENT_PAGE, params.getCurrentPage());
        model.put("pageSize", params.getPageSize());
        model.put(TEMPLATE_PARAM_LIMIT_REACHED, macroParameters.get("limitedRows"));
        model.put(TEMPLATE_PARAM_WARNING_LINK, macroParameters.get(TEMPLATE_PARAM_WARNING_LINK));
        if (blueprint.isPresent() && (paginatedDetailLines == null || paginatedDetailLines.getDetailLines().isEmpty())) {
            blueprint.decorate(model);
        } else {
            model.put(TEMPLATE_PARAM_BLUEPRINT_PRESENT, false);
        }
        return model;
    }

    private void validateContextOwner(ConversionContext conversionContext) throws MacroExecutionException {
        ContentEntityObject owner = conversionContext.getEntity();
        if (!(owner instanceof SpaceContentEntityObject) && !(owner instanceof Draft)) {
            throw new MacroExecutionException(this.i18nResolver.getText("detailssummary.error.must.be.inside.space"));
        }
    }

    public RenderMode getBodyRenderMode() {
        return RenderMode.NO_RENDER;
    }

    public boolean hasBody() {
        return false;
    }

    public TokenType getTokenType(Map parameters, String body, RenderContext context) {
        return TokenType.BLOCK;
    }

    public Macro.BodyType getBodyType() {
        return Macro.BodyType.NONE;
    }

    public Macro.OutputType getOutputType() {
        return Macro.OutputType.BLOCK;
    }

    private boolean getBooleanParam(Map<String, String> macroParams, String paramName) {
        return Boolean.parseBoolean(macroParams.get(paramName));
    }

    private Space getCurrentSpace(String spaceKey) {
        return this.spaceManager.getSpace(spaceKey);
    }

    private class BlueprintParameters {
        private final Map<String, String> macroParameters;
        private final Space currentSpace;
        private final String blueprintModuleCompleteKey;
        private final String contentBlueprintId;

        private BlueprintParameters(Map<String, String> macroParameters, Space currentSpace) {
            this.macroParameters = macroParameters;
            this.currentSpace = currentSpace;
            this.blueprintModuleCompleteKey = StringUtils.trimToEmpty((String)macroParameters.get("blueprintModuleCompleteKey"));
            this.contentBlueprintId = StringUtils.trimToEmpty((String)macroParameters.get("contentBlueprintId"));
        }

        boolean isPresent() {
            return StringUtils.isNotBlank((CharSequence)this.blueprintModuleCompleteKey) || StringUtils.isNotBlank((CharSequence)this.contentBlueprintId);
        }

        void decorate(Map<String, Object> model) {
            model.put(DetailsSummaryMacro.TEMPLATE_PARAM_BLUEPRINT_PRESENT, true);
            model.put("blankTitle", this.macroParameters.get("blankTitle"));
            model.put("blankDescription", this.macroParameters.get("blankDescription"));
            model.put("blueprintModuleKey", new ModuleCompleteKey(this.blueprintModuleCompleteKey).getModuleKey());
            model.put("createFromTemplateHtml", DetailsSummaryMacro.this.createButtonService.renderBlueprintButton(this.currentSpace, this.contentBlueprintId, this.blueprintModuleCompleteKey, this.macroParameters.get("createButtonLabel"), null));
        }
    }
}

