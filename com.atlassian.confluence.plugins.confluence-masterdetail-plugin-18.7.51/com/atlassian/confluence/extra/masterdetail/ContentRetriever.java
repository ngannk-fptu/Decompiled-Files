/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.Content
 *  com.atlassian.confluence.api.model.content.ContentStatus
 *  com.atlassian.confluence.api.model.content.Space
 *  com.atlassian.confluence.api.model.pagination.PageRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.confluence.api.model.pagination.SimplePageRequest
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.api.model.search.SearchContext
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.api.service.search.CQLSearchService
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.macro.MacroExecutionException
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  io.atlassian.fugue.Pair
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.beans.factory.annotation.Qualifier
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.extra.masterdetail;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.Content;
import com.atlassian.confluence.api.model.content.ContentStatus;
import com.atlassian.confluence.api.model.pagination.PageRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.api.model.pagination.SimplePageRequest;
import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.api.model.search.SearchContext;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.search.CQLSearchService;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.extra.masterdetail.ContentRetrieverResult;
import com.atlassian.confluence.extra.masterdetail.MasterDetailConfigurator;
import com.atlassian.confluence.extra.masterdetail.analytics.DetailsSummaryMacroMetricsEvent;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.message.I18nResolver;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import io.atlassian.fugue.Pair;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class ContentRetriever {
    private static final Logger logger = LoggerFactory.getLogger(ContentRetriever.class);
    private final CQLSearchService searchService;
    private final PageManager pageManager;
    private final ContentEntityManager contentEntityManager;
    private final I18nResolver i18nResolver;
    private final Supplier<Integer> maxResultsSupplier;
    private final int BATCH_SIZE;

    @Autowired
    public ContentRetriever(@ComponentImport CQLSearchService searchService, @ComponentImport @Qualifier(value="pageManager") PageManager pageManager, @ComponentImport @Qualifier(value="contentEntityManager") ContentEntityManager contentEntityManager, @ComponentImport I18nResolver i18nResolver, MasterDetailConfigurator configurator) {
        this.searchService = searchService;
        this.i18nResolver = i18nResolver;
        this.pageManager = pageManager;
        this.contentEntityManager = contentEntityManager;
        this.maxResultsSupplier = configurator::getPagePropertiesReportContentRetrieverMaxResult;
        this.BATCH_SIZE = configurator.getPagePropertiesReportContentRetrieverBatchSize();
    }

    public void iterateThoughContentByCQL(String fullCql, SearchContext context, int batchSize, Consumer<ContentRetrieverResult> callback) throws MacroExecutionException {
        SearchContext searchContext = context == null ? SearchContext.builder().contentStatus(Arrays.asList(ContentStatus.CURRENT)).build() : context;
        int MAX_RESULTS = this.maxResultsSupplier.get();
        try {
            boolean hasMoreContent = true;
            int startIndex = 0;
            int nextLimit = startIndex + batchSize;
            while (hasMoreContent && nextLimit <= MAX_RESULTS) {
                SimplePageRequest nextBatchPageRequest = new SimplePageRequest(startIndex, batchSize);
                PageResponse contents = this.searchService.searchContent(fullCql, searchContext, (PageRequest)nextBatchPageRequest, new Expansion[0]);
                hasMoreContent = contents.hasMore();
                nextLimit = (startIndex += contents.size()) + batchSize;
                try {
                    List idList = contents.getResults().stream().map(content -> content.getId().asLong()).collect(Collectors.toList());
                    List<ContentEntityObject> contentList = this.transformToCEO(contents.getResults());
                    callback.accept(new ContentRetrieverResult(contentList, nextLimit >= MAX_RESULTS && hasMoreContent));
                }
                catch (Exception e) {
                    logger.error("Retrieving contents for Page Property Report has failed:", (Throwable)e);
                }
            }
            if (nextLimit > MAX_RESULTS) {
                logger.warn("Reaching hard limit of ContentRetriever will skip the rest of remaining data");
            }
        }
        catch (ServiceException e) {
            String msg = this.i18nResolver.getText("detailssummary.error.searchservice.exception", new Serializable[]{fullCql, e.getMessage()});
            throw new MacroExecutionException(msg, (Throwable)e);
        }
    }

    public ContentRetrieverResult getContentWithMetaData(@Nonnull String cql, boolean reverseSort, SearchContext searchContext, DetailsSummaryMacroMetricsEvent.Builder metrics) throws MacroExecutionException {
        metrics.maxResultConfig(this.maxResultsSupplier.get());
        ArrayList results = Lists.newArrayList();
        Boolean[] rowsLimited = new Boolean[]{false};
        String fullCql = "(" + cql + ") and macro = details" + this.buildOrderByClause(reverseSort);
        metrics.contentSearchStart();
        this.iterateThoughContentByCQL(fullCql, searchContext, this.BATCH_SIZE, listContent -> {
            results.addAll(listContent.getRows());
            rowsLimited[0] = listContent.isLimited();
        });
        metrics.contentSearchFinish();
        metrics.labelledContentCount(results.size());
        return new ContentRetrieverResult(results, rowsLimited[0]);
    }

    private String buildOrderByClause(boolean reverseSort) {
        Object order = " order by lastModified";
        if (!reverseSort) {
            order = (String)order + " desc";
        }
        return order;
    }

    public List<ContentEntityObject> getContentEntityObjects(Collection<Long> sortedPagedContentIds) {
        Objects.nonNull(sortedPagedContentIds);
        int requestedItem = sortedPagedContentIds.size();
        if (requestedItem == 0) {
            return Collections.EMPTY_LIST;
        }
        ArrayList<ContentEntityObject> ceos = new ArrayList<ContentEntityObject>(requestedItem);
        StreamSupport.stream(Iterables.partition(sortedPagedContentIds, (int)this.BATCH_SIZE).spliterator(), false).flatMap(orderIds -> Stream.of(new Pair(orderIds, (Object)this.pageManager.getAbstractPages((Iterable)orderIds)))).flatMap(pairResult -> {
            List orderIds = (List)pairResult.left();
            List unOrderPage = (List)pairResult.right();
            Map<Long, AbstractPage> unsortedMap = unOrderPage.stream().collect(Collectors.toMap(page -> page.getId(), page -> page));
            return Stream.of(orderIds.stream().map(id -> (AbstractPage)unsortedMap.get(id)).collect(Collectors.toList()));
        }).forEach(batchCEOResult -> ceos.addAll((Collection<ContentEntityObject>)batchCEOResult));
        return ceos;
    }

    private List<ContentEntityObject> transformToCEO(List<Content> results) {
        return results.stream().map(content -> {
            if ("comment".equals(content.getType().getType())) {
                return this.contentEntityManager.getById(content.getId().asLong());
            }
            return new IdOnlyCEO((Content)content);
        }).collect(Collectors.toList());
    }

    public class IdOnlyCEO
    extends AbstractPage {
        private Content content;
        private String urlPath;

        public IdOnlyCEO(Content content) {
            this.content = content;
            this.setId(content.getId().asLong());
            this.setTitle(content.getTitle());
            this.setSpace(new Space(com.atlassian.confluence.api.model.content.Space.getSpaceKey((Reference)content.getSpaceRef())));
        }

        public void setUrlPath(String urlPath) {
            this.urlPath = urlPath;
        }

        public String getUrlPath() {
            if (StringUtils.isNotEmpty((CharSequence)this.urlPath)) {
                return this.urlPath;
            }
            return super.getUrlPath();
        }

        public String getType() {
            return this.content.getType().getType();
        }

        public String getNameForComparison() {
            throw new UnsupportedOperationException();
        }

        public String getLinkWikiMarkup() {
            throw new UnsupportedOperationException();
        }
    }
}

