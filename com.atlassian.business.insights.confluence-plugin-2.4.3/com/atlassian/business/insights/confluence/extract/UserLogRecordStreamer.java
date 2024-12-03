/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.bonnie.Searchable
 *  com.atlassian.business.insights.api.Entity
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityStreamerQuery
 *  com.atlassian.business.insights.api.extract.EntityToLogRecordConverter
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult
 *  com.atlassian.confluence.search.service.ContentTypeEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.ContentTypeQuery
 *  com.atlassian.confluence.user.PersonalInformation
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.atlassian.bonnie.Searchable;
import com.atlassian.business.insights.api.Entity;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.EntityToLogRecordConverter;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.confluence.extract.IndexValidator;
import com.atlassian.business.insights.confluence.prefetch.EntityPrefetchProvider;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.user.PersonalInformation;
import java.time.Instant;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class UserLogRecordStreamer
implements LogRecordStreamer {
    public static final int USER_PAGE_SIZE = 500;
    private final IndexValidator indexValidator;
    private final EntityToLogRecordConverter<Long, PersonalInformation> converter;
    private final EntityPrefetchProvider entityPrefetchProvider;

    public UserLogRecordStreamer(IndexValidator indexValidator, EntityToLogRecordConverter<Long, PersonalInformation> converter, EntityPrefetchProvider entityPrefetchProvider) {
        this.indexValidator = indexValidator;
        this.converter = converter;
        this.entityPrefetchProvider = entityPrefetchProvider;
    }

    @Nonnull
    public StreamerValidationResult isReady() {
        return this.indexValidator.validate();
    }

    public Stream<LogRecord> stream(@Nonnull EntityStreamerQuery entityStreamerQuery) {
        SearchQuery searchQuery = (SearchQuery)BooleanQuery.builder().addMust((Object)new ContentTypeQuery(ContentTypeEnum.PERSONAL_INFORMATION)).build();
        return this.entityPrefetchProvider.prefetchAndConvert(500, searchQuery, this::toLogRecord);
    }

    private LogRecord toLogRecord(Searchable searchable) {
        if (searchable instanceof PersonalInformation) {
            PersonalInformation personalInformation = (PersonalInformation)searchable;
            return this.converter.convert(Entity.getInstance((Object)personalInformation.getId(), (Instant)personalInformation.getLastModificationDate().toInstant(), (Object)personalInformation));
        }
        return null;
    }
}

