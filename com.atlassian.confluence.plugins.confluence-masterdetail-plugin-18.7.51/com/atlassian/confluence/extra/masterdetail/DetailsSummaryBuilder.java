/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.like.LikeManager
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.pages.CommentManager
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.fugue.Either
 *  com.atlassian.fugue.Functions
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.atlassian.util.concurrent.ThreadFactories
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Sets
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.commons.lang3.text.StrTokenizer
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.ConversionContextOutputType;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.ContentRetriever;
import com.atlassian.confluence.extra.masterdetail.DetailsHeading;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryMacroThreadLocalContext;
import com.atlassian.confluence.extra.masterdetail.DetailsSummaryParameters;
import com.atlassian.confluence.extra.masterdetail.ExtractedDetails;
import com.atlassian.confluence.extra.masterdetail.ExtractedDetailsComparator;
import com.atlassian.confluence.extra.masterdetail.MasterDetailConfigurator;
import com.atlassian.confluence.extra.masterdetail.RenderError;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.extra.masterdetail.entities.DetailLine;
import com.atlassian.confluence.extra.masterdetail.entities.PaginatedDetailLines;
import com.atlassian.confluence.extra.masterdetail.services.InternalPagePropertiesService;
import com.atlassian.confluence.like.LikeManager;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.pages.CommentManager;
import com.atlassian.confluence.plugins.pageproperties.api.model.PageProperty;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.fugue.Either;
import com.atlassian.fugue.Functions;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.renderer.RenderContext;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.atlassian.util.concurrent.ThreadFactories;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Nullable;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.StrTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class DetailsSummaryBuilder
implements DisposableBean {
    private static final Logger log = LoggerFactory.getLogger(DetailsSummaryBuilder.class);
    private static final Integer ROWS_PER_PARALLEL_RENDER = 50;
    private static final Long RENDER_TIMEOUT_SECONDS = 60L;
    private static final String THREAD_PREFIX = DetailsSummaryBuilder.class.getSimpleName();
    private static ExecutorService pool;
    private final int THREAD_POOL_SIZE;
    private final XhtmlContent xhtmlContent;
    private final CommentManager commentManager;
    private final LikeManager likeManager;
    private final InternalPagePropertiesService pagePropertiesService;
    private final TransactionTemplate transactionTemplate;
    private final ContentEntityManager contentEntityManager;

    @Autowired
    public DetailsSummaryBuilder(XhtmlContent xhtmlContent, @ComponentImport CommentManager commentManager, @ComponentImport LikeManager likeManager, InternalPagePropertiesService pagePropertiesService, @ComponentImport TransactionTemplate transactionTemplate, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, MasterDetailConfigurator configurator) {
        this.xhtmlContent = xhtmlContent;
        this.commentManager = commentManager;
        this.likeManager = likeManager;
        this.pagePropertiesService = pagePropertiesService;
        this.transactionTemplate = transactionTemplate;
        this.contentEntityManager = contentEntityManager;
        this.THREAD_POOL_SIZE = configurator.getPagePropertiesThreadPoolSize();
        if (Objects.isNull(pool)) {
            pool = Executors.newFixedThreadPool(this.THREAD_POOL_SIZE, ThreadFactories.namedThreadFactory((String)THREAD_PREFIX));
        }
    }

    private List<DetailsHeading> buildHeadingsFromDetails(Collection<ExtractedDetails> detailLines, ConversionContext originalContext) {
        TreeMap<String, String> headers = new TreeMap<String, String>();
        for (ExtractedDetails detailLine : detailLines) {
            if (detailLine.getDetails().isEmpty()) continue;
            for (String headingText : detailLine.getDetails().keySet()) {
                if (headers.get(headingText) != null) continue;
                try {
                    ContentEntityObject safeLineContent = this.contentEntityManager.getById(detailLine.getContent().getId());
                    String renderedValue = headingText;
                    DefaultConversionContext context = new DefaultConversionContext((RenderContext)safeLineContent.toPageContext());
                    context.setProperty("details_summary_depth", originalContext.getProperty("details_summary_depth", (Object)0));
                    String headingStorageFormat = detailLine.getDetails().get(headingText).getHeadingStorageFormat();
                    if (StringUtils.isNotBlank((CharSequence)headingStorageFormat)) {
                        renderedValue = this.xhtmlContent.convertStorageToView(headingStorageFormat, (ConversionContext)context);
                    }
                    headers.put(headingText, renderedValue);
                }
                catch (XhtmlException | XMLStreamException e) {
                    log.error("Cannot render xhtml content for heading in page properties macro:" + headingText, e);
                }
            }
        }
        return Lists.newArrayList((Iterable)Collections2.transform(headers.entrySet(), entry -> new DetailsHeading((String)entry.getKey(), (String)entry.getValue())));
    }

    private static List<DetailsHeading> buildHeadingsFromParameter(String headingsString) {
        LinkedHashSet headings = Sets.newLinkedHashSet();
        StrTokenizer tokenizer = StrTokenizer.getCSVInstance((String)headingsString);
        while (tokenizer.hasNext()) {
            String heading = tokenizer.nextToken();
            if (!StringUtils.isNotBlank((CharSequence)heading)) continue;
            String escapedHeading = StringEscapeUtils.escapeHtml4((String)heading);
            headings.add(new DetailsHeading(escapedHeading, escapedHeading));
        }
        return new ArrayList<DetailsHeading>(headings);
    }

    private void fillWithCountsAndLink(List<DetailLine> detailLines, boolean countComments, boolean countLikes, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        Map<Searchable, Integer> likesCounts = countLikes ? this.countLikes(detailLines, metrics) : null;
        Map<Searchable, Integer> commentsCounts = countComments ? this.countComments(detailLines, metrics) : null;
        for (DetailLine detailLine : detailLines) {
            DetailsSummaryBuilder.fillLineTitleLink(detailLine);
            if (commentsCounts != null) {
                Integer likes = commentsCounts.get(detailLine.getContent());
                detailLine.setCommentsCount(likes);
            }
            if (likesCounts == null) continue;
            Integer comments = likesCounts.get(detailLine.getContent());
            detailLine.setLikesCount(comments);
        }
    }

    private Map<Searchable, Integer> countLikes(List<DetailLine> detailLines, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        metrics.summaryTableCountLikesStart();
        Map likesCounts = this.likeManager.countLikes(DetailsSummaryBuilder.searchablesFrom(detailLines));
        metrics.summaryTableCountLikesFinish(DetailsSummaryBuilder.count(likesCounts.values()));
        return likesCounts;
    }

    private Map<Searchable, Integer> countComments(List<DetailLine> detailLines, DetailsSummaryMacroMetricsEvent.Builder metrics) {
        metrics.summaryTableCountCommentsStart();
        Map commentsCounts = this.commentManager.countComments(DetailsSummaryBuilder.searchablesFrom(detailLines));
        metrics.summaryTableCountCommentsFinish(DetailsSummaryBuilder.count(commentsCounts.values()));
        return commentsCounts;
    }

    private static int count(Iterable<Integer> numbers) {
        return (Integer)Functions.fold((arg1, arg2) -> arg1 + arg2, (Object)0, numbers);
    }

    private static Collection<Searchable> searchablesFrom(Collection<DetailLine> detailLines) {
        return Collections2.transform(detailLines, DetailLine::getContent);
    }

    private static void fillLineTitleLink(DetailLine detailLine) {
        ContentEntityObject content = detailLine.getContent();
        String type = content.getType();
        if ("comment".equals(type)) {
            Comment comment = (Comment)content;
            ContentEntityObject owner = comment.getContainer();
            detailLine.setTitle(owner.getDisplayTitle());
            detailLine.setSubTitle(comment.getDisplayTitle());
            detailLine.setRelativeLink(owner.getUrlPath());
            detailLine.setSubRelativeLink(content.getUrlPath());
        } else {
            detailLine.setTitle(content.getDisplayTitle());
            detailLine.setRelativeLink(content.getUrlPath());
        }
    }

    private List<DetailsHeading> getHeadings(@Nullable String headingsString, Collection<ExtractedDetails> detailLines, DetailsSummaryMacroMetricsEvent.Builder metrics, ConversionContext conversionContext) {
        metrics.summaryTableHeadersBuildStart();
        List<DetailsHeading> headers = StringUtils.isNotBlank((CharSequence)headingsString) ? DetailsSummaryBuilder.buildHeadingsFromParameter(headingsString) : this.buildHeadingsFromDetails(detailLines, conversionContext);
        metrics.summaryTableHeadersBuildFinish(headers.size());
        return headers;
    }

    public PaginatedDetailLines getPaginatedDetailLines(DetailsSummaryParameters params, boolean requireAsyncRenderSafe, DetailsSummaryMacroMetricsEvent.Builder metrics, ConversionContext conversionContext) {
        List<ExtractedDetails> extractedDetails = this.pagePropertiesService.getDetailsFromContent(params.getContent(), params.getId(), metrics);
        if (extractedDetails.isEmpty()) {
            return PaginatedDetailLines.empty();
        }
        params.setTotalRenderedLines(extractedDetails.size());
        params.checkPageBounds();
        if (StringUtils.isNotBlank((CharSequence)params.getSortBy())) {
            extractedDetails.sort(new ExtractedDetailsComparator(StringEscapeUtils.escapeHtml4((String)params.getSortBy()), params.isReverseSort()));
        }
        List<DetailsHeading> headings = this.getHeadings(params.getHeadingsString(), extractedDetails, metrics, conversionContext);
        List renderedHeadings = Lists.transform(headings, DetailsHeading::getRenderedHeading);
        List pagedDetails = Lists.partition(extractedDetails, (int)params.getPageSize());
        List detailsForCurrentPage = (List)pagedDetails.get(params.getCurrentPage());
        boolean isPaginated = params.getTotalPages() > 1;
        return (PaginatedDetailLines)this.renderDetailRows(headings, (List<ExtractedDetails>)detailsForCurrentPage, requireAsyncRenderSafe, metrics, isPaginated, conversionContext).fold(renderError -> DetailsSummaryBuilder.renderErrorResult(renderError, renderedHeadings), renderedLines -> {
            this.fillWithCountsAndLink((List<DetailLine>)renderedLines, params.isCountComments(), params.isCountLikes(), metrics);
            return new PaginatedDetailLines(renderedHeadings, (List<DetailLine>)renderedLines, true);
        });
    }

    private static PaginatedDetailLines renderErrorResult(@Nullable RenderError input, List<String> headings) {
        boolean asyncRenderSafe = input != RenderError.NON_ASYNC_RENDER_SAFE;
        return new PaginatedDetailLines(headings, Collections.emptyList(), asyncRenderSafe, input);
    }

    private Either<RenderError, List<DetailLine>> renderDetailRows(List<DetailsHeading> headings, List<ExtractedDetails> pagedDetailLines, boolean requireAsyncRenderSafe, DetailsSummaryMacroMetricsEvent.Builder metrics, boolean isPaginated, ConversionContext conversionContext) {
        metrics.summaryTableBodyBuildStart();
        ArrayList renderedLines = new ArrayList();
        if (pagedDetailLines.isEmpty()) {
            return Either.right(renderedLines);
        }
        UUID reportId = UUID.randomUUID();
        log.debug("New report id created {}", (Object)reportId);
        ArrayList futures = new ArrayList();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        Lists.partition(pagedDetailLines, (int)ROWS_PER_PARALLEL_RENDER).forEach(lines -> futures.add(CompletableFuture.supplyAsync(() -> this.renderDetailRowsWithTransaction(headings, (List<ExtractedDetails>)lines, requireAsyncRenderSafe, isPaginated, conversionContext, currentUser, reportId), pool)));
        for (CompletableFuture future : futures) {
            Either futureResult = null;
            try {
                futureResult = (Either)future.get(RENDER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                if (futureResult.isLeft()) {
                    return Either.left((Object)((Object)((RenderError)((Object)futureResult.left().get()))));
                }
                renderedLines.addAll((Collection)futureResult.right().get());
            }
            catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.warn("Timeout when rendering the page properties report", (Throwable)e);
                future.cancel(true);
            }
        }
        metrics.summaryTableBodyBuildFinish(renderedLines.size());
        return Either.right(renderedLines);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Either<RenderError, List<DetailLine>> renderDetailRowsWithTransaction(List<DetailsHeading> headings, List<ExtractedDetails> pagedDetailLines, boolean requireAsyncRenderSafe, boolean isPaginated, ConversionContext conversionContext, ConfluenceUser currentUser, UUID reportId) {
        try {
            AuthenticatedUserThreadLocal.set((ConfluenceUser)currentUser);
            Either either = (Either)this.transactionTemplate.execute(() -> this.renderDetailRows(headings, pagedDetailLines, requireAsyncRenderSafe, isPaginated, conversionContext, reportId));
            return either;
        }
        catch (Exception e) {
            log.error("Transaction error occurred when rendering the page property report.", (Throwable)e);
            Either either = Either.right(new ArrayList());
            return either;
        }
        finally {
            AuthenticatedUserThreadLocal.reset();
        }
    }

    private Either<RenderError, List<DetailLine>> renderDetailRows(List<DetailsHeading> headings, List<ExtractedDetails> pagedDetailLines, boolean requireAsyncRenderSafe, boolean isPaginated, ConversionContext conversionContext, UUID reportId) {
        ArrayList<DetailLine> renderedLines = new ArrayList<DetailLine>();
        DetailsSummaryMacroThreadLocalContext.setContextRecursionDepth((Integer)conversionContext.getProperty("details_summary_depth", (Object)0));
        UUID theadUUID = UUID.randomUUID();
        log.debug("Rendering thread for report id {} in thread {}", (Object)reportId, (Object)theadUUID);
        for (ExtractedDetails line : pagedDetailLines) {
            ArrayList<String> cells = new ArrayList<String>(headings.size());
            ContentEntityObject lineContent = line.getContent();
            ContentEntityObject safeLineContent = this.contentEntityManager.getById(lineContent.getId());
            Map<String, PageProperty> lineDetails = line.getDetails();
            PageContext pageContext = safeLineContent.toPageContext();
            pageContext.setOutputType(conversionContext.getOutputType());
            if (lineContent instanceof ContentRetriever.IdOnlyCEO) {
                ContentRetriever.IdOnlyCEO idOnlyCEO = (ContentRetriever.IdOnlyCEO)lineContent;
                idOnlyCEO.setUrlPath(safeLineContent.getUrlPath());
            }
            if (isPaginated) {
                pageContext.setOutputType(ConversionContextOutputType.PREVIEW.value());
            }
            DefaultConversionContext subContext = new DefaultConversionContext((RenderContext)pageContext);
            subContext.setProperty("details_summary_depth", conversionContext.getProperty("details_summary_depth", (Object)0));
            for (DetailsHeading heading : headings) {
                PageProperty pageProperty = lineDetails.get(heading.getHeading());
                boolean added = false;
                if (pageProperty != null && StringUtils.isNotBlank((CharSequence)pageProperty.getDetailStorageFormat())) {
                    String value = pageProperty.getDetailStorageFormat();
                    try {
                        String renderedValue = this.xhtmlContent.convertStorageToView(value, (ConversionContext)subContext);
                        if (!subContext.isAsyncRenderSafe() && requireAsyncRenderSafe && !isPaginated) {
                            return Either.left((Object)((Object)RenderError.NON_ASYNC_RENDER_SAFE));
                        }
                        cells.add(renderedValue);
                        added = true;
                    }
                    catch (XhtmlException | XMLStreamException e) {
                        log.error("Cannot render xhtml content for page properties macro:" + value, e);
                    }
                }
                if (added) continue;
                cells.add("");
            }
            renderedLines.add(new DetailLine(lineContent, cells));
        }
        return Either.right(renderedLines);
    }

    public void destroy() throws Exception {
        pool.shutdown();
        pool = null;
    }
}

