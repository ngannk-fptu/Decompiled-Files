/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityStreamerQuery
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult
 *  com.atlassian.confluence.pages.Comment
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery
 *  com.atlassian.confluence.search.v2.query.DateRangeQuery$DateRangeQueryType
 *  com.atlassian.confluence.search.v2.query.TermQuery
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.bonnie.Searchable;
import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.confluence.attribute.CommentAttributes;
import com.atlassian.business.insights.confluence.extract.CommentToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.IndexValidator;
import com.atlassian.business.insights.confluence.extract.filter.SpaceOptOutFilter;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.confluence.pages.Comment;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.time.Instant;
import java.util.Date;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class CommentLogRecordStreamer
implements LogRecordStreamer {
    public static final int COMMENT_PAGE_SIZE = 500;
    private final IndexValidator indexValidator;
    private final CommentToLogRecordConverter commentToLogRecordConverter;
    private final EntityPrefetchProvider entityPrefetchProvider;

    public CommentLogRecordStreamer(IndexValidator indexValidator, CommentToLogRecordConverter commentToLogRecordConverter, EntityPrefetchProvider entityPrefetchProvider) {
        this.indexValidator = indexValidator;
        this.commentToLogRecordConverter = commentToLogRecordConverter;
        this.entityPrefetchProvider = entityPrefetchProvider;
    }

    @Nonnull
    public StreamerValidationResult isReady() {
        return this.indexValidator.validate();
    }

    public Stream<LogRecord> stream(@Nonnull EntityStreamerQuery entityStreamerQuery) {
        SpaceOptOutFilter spaceOptOutFilter = new SpaceOptOutFilter(entityStreamerQuery.getOptOutEntityIdentifiers());
        SearchQuery searchQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new TermQuery("contentStatus", "current")).addMust((Object)new ContentTypeQuery(ContentTypeEnum.COMMENT)).addMust((Object)DateRangeQuery.newDateRangeQuery((DateRangeQuery.DateRangeQueryType)DateRangeQuery.DateRangeQueryType.MODIFIED).includeFrom(true).includeTo(true).fromDate(Date.from(entityStreamerQuery.getFrom())).toDate(Date.from(Instant.now())).build()).build();
        return this.entityPrefetchProvider.prefetchAndConvert(500, searchQuery, this::toLogRecord).filter(logRecord -> this.isNotInOptedOutSpace((LogRecord)logRecord, spaceOptOutFilter));
    }

    private LogRecord toLogRecord(Searchable searchable) {
        if (searchable instanceof Comment) {
            Comment comment = (Comment)searchable;
            return this.commentToLogRecordConverter.convert((Entity<Long, Comment>)Entity.getInstance((Object)comment.getId(), (Instant)comment.getLastModificationDate().toInstant(), (Object)comment));
        }
        return null;
    }

    private boolean isNotInOptedOutSpace(LogRecord logRecord, SpaceOptOutFilter spaceOptOutFilter) {
        String spaceKey = (String)logRecord.getPayload().get(CommentAttributes.SPACE_KEY_ATTR.getInternalName());
        return spaceOptOutFilter.test(spaceKey);
    }
}

