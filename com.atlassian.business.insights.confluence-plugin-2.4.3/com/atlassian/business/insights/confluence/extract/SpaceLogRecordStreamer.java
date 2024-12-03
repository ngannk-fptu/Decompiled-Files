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
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.spaces.SpaceDescription
 *  com.google.common.collect.ImmutableList
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.bonnie.Searchable;
import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.confluence.attribute.SpaceAttributes;
import com.atlassian.business.insights.confluence.extract.IndexValidator;
import com.atlassian.business.insights.confluence.extract.SpaceToLogRecordConverter;
import com.atlassian.business.insights.confluence.extract.filter.SpaceOptOutFilter;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.spaces.SpaceDescription;
import com.google.common.collect.ImmutableList;
import java.time.Instant;
import java.util.Collection;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class SpaceLogRecordStreamer
implements LogRecordStreamer {
    public static final int SPACE_PAGE_SIZE = 500;
    private final IndexValidator indexValidator;
    private final SpaceToLogRecordConverter converter;
    private final EntityPrefetchProvider entityPrefetchProvider;

    public SpaceLogRecordStreamer(IndexValidator indexValidator, SpaceToLogRecordConverter converter, EntityPrefetchProvider entityPrefetchProvider) {
        this.indexValidator = indexValidator;
        this.converter = converter;
        this.entityPrefetchProvider = entityPrefetchProvider;
    }

    @Nonnull
    public StreamerValidationResult isReady() {
        return this.indexValidator.validate();
    }

    public Stream<LogRecord> stream(@Nonnull EntityStreamerQuery entityStreamerQuery) {
        SpaceOptOutFilter spaceOptOutFilter = new SpaceOptOutFilter(entityStreamerQuery.getOptOutEntityIdentifiers());
        ContentTypeQuery searchQuery = new ContentTypeQuery((Collection)ImmutableList.of((Object)ContentTypeEnum.SPACE_DESCRIPTION, (Object)ContentTypeEnum.PERSONAL_SPACE_DESCRIPTION));
        return this.entityPrefetchProvider.prefetchAndConvert(500, (SearchQuery)searchQuery, this::toLogRecord).filter(logRecord -> this.isNotInOptedOutSpace((LogRecord)logRecord, spaceOptOutFilter));
    }

    private LogRecord toLogRecord(Searchable searchable) {
        if (searchable instanceof SpaceDescription) {
            SpaceDescription spaceDescription = (SpaceDescription)searchable;
            return this.converter.convert((Entity<Long, SpaceDescription>)Entity.getInstance((Object)spaceDescription.getId(), (Instant)spaceDescription.getLastModificationDate().toInstant(), (Object)spaceDescription));
        }
        return null;
    }

    private boolean isNotInOptedOutSpace(LogRecord logRecord, SpaceOptOutFilter spaceOptOutFilter) {
        String spaceKey = (String)logRecord.getPayload().get(SpaceAttributes.SPACE_KEY_ATTR.getInternalName());
        return spaceOptOutFilter.test(spaceKey);
    }
}

