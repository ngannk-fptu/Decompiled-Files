/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityStreamerQuery
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult
 *  com.atlassian.business.insights.core.extract.EntityPage
 *  com.atlassian.business.insights.core.extract.EntityPageIterator
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  com.atlassian.sal.api.transaction.TransactionTemplate
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.confluence.attribute.PageAttributes;
import com.atlassian.business.insights.confluence.extract.IndexValidator;
import com.atlassian.business.insights.confluence.extract.PageToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.filter.SpaceOptOutFilter;
import com.atlassian.business.insights.confluence.prefetch.DocIdsHolder;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.business.insights.core.extract.EntityPage;
import com.atlassian.business.insights.core.extract.EntityPageIterator;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import com.atlassian.sal.api.transaction.TransactionTemplate;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import javax.annotation.Nonnull;

public class PageLogRecordStreamer
implements LogRecordStreamer {
    public static final int PAGE_PAGE_SIZE = 1000;
    private final IndexValidator indexValidator;
    private final PageToLogRecordConverter pageConverter;
    private final EntityPrefetchProvider entityPrefetchProvider;
    private final TransactionTemplate transactionTemplate;
    private final PageManager pageManager;

    public PageLogRecordStreamer(IndexValidator indexValidator, PageToLogRecordConverter pageConverter, TransactionTemplate transactionTemplate, EntityPrefetchProvider entityPrefetchProvider, PageManager pageManager) {
        this.indexValidator = indexValidator;
        this.pageConverter = pageConverter;
        this.entityPrefetchProvider = entityPrefetchProvider;
        this.transactionTemplate = transactionTemplate;
        this.pageManager = pageManager;
    }

    @Nonnull
    public StreamerValidationResult isReady() {
        return this.indexValidator.validate();
    }

    public Stream<LogRecord> stream(@Nonnull EntityStreamerQuery entityStreamerQuery) {
        SpaceOptOutFilter spaceOptOutFilter = new SpaceOptOutFilter(entityStreamerQuery.getOptOutEntityIdentifiers());
        SearchQuery searchQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery("contentStatus", "current")).addMust((Object)new ContentTypeQuery((Collection)ImmutableList.of((Object)ContentTypeEnum.PAGE, (Object)ContentTypeEnum.BLOG))).addMust((Object)DateRangeQuery.newDateRangeQuery((DateRangeQuery.DateRangeQueryType)DateRangeQuery.DateRangeQueryType.MODIFIED).includeFrom(true).includeTo(true).fromDate(Date.from(entityStreamerQuery.getFrom())).toDate(Date.from(Instant.now())).build()).build();
        DocIdsHolder docIdsHolder = this.entityPrefetchProvider.prefetchDocIds(searchQuery);
        Iterable pagesIterable = () -> new EntityPageIterator(1000, (offset, limit) -> this.fetchPages(docIdsHolder, (int)offset, (int)limit));
        return StreamSupport.stream(pagesIterable.spliterator(), false).map(EntityPage::getValues).flatMap(Collection::stream).filter(logRecord -> this.isNotInOptedOutSpace((LogRecord)logRecord, spaceOptOutFilter));
    }

    private List<LogRecord> fetchPages(DocIdsHolder docIdsHolder, int offset, int limit) {
        if (offset > docIdsHolder.size()) {
            return Collections.emptyList();
        }
        return (List)this.transactionTemplate.execute(() -> this.pageManager.getAbstractPages(docIdsHolder.getContentIds(offset, limit)).stream().map(this::toLogRecord).filter(Objects::nonNull).collect(Collectors.toList()));
    }

    private LogRecord toLogRecord(AbstractPage page) {
        return this.pageConverter.convert((Entity<Long, AbstractPage>)Entity.getInstance((Object)page.getId(), (Instant)page.getLastModificationDate().toInstant(), (Object)page));
    }

    private boolean isNotInOptedOutSpace(LogRecord logRecord, SpaceOptOutFilter spaceOptOutFilter) {
        String spaceKey = (String)logRecord.getPayload().get(PageAttributes.SPACE_KEY_ATTR.getInternalName());
        return spaceOptOutFilter.test(spaceKey);
    }
}

