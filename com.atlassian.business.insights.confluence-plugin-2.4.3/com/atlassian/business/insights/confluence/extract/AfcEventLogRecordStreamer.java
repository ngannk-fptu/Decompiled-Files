/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.addonengine.addons.analytics.service.Event
 *  com.addonengine.addons.analytics.service.EventQuery
 *  com.addonengine.addons.analytics.service.EventService
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.business.insights.api.LogRecord
 *  com.atlassian.business.insights.api.extract.EntityStreamerQuery
 *  com.atlassian.business.insights.api.extract.LogRecordStreamer
 *  com.atlassian.business.insights.api.extract.StreamerValidationResult
 *  com.atlassian.business.insights.core.util.DateTimeConverter
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.UrlMode
 *  javax.annotation.Nonnull
 */
package com.atlassian.business.insights.confluence.extract;

import com.addonengine.addons.analytics.service.Event;
import com.addonengine.addons.analytics.service.EventQuery;
import com.addonengine.addons.analytics.service.EventService;
import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.api.LogRecord;
import com.atlassian.business.insights.api.extract.EntityStreamerQuery;
import com.atlassian.business.insights.api.extract.LogRecordStreamer;
import com.atlassian.business.insights.api.extract.StreamerValidationResult;
import com.atlassian.business.insights.confluence.attribute.AfcEventAttributes;
import com.atlassian.business.insights.confluence.attribute.SharedAttributes;
import com.atlassian.business.insights.confluence.extract.filter.SpaceOptOutFilter;
import com.atlassian.business.insights.core.util.DateTimeConverter;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.UrlMode;
import java.time.Instant;
import java.util.HashMap;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class AfcEventLogRecordStreamer
implements LogRecordStreamer {
    private final EventService eventService;
    private final ApplicationProperties applicationProperties;

    public AfcEventLogRecordStreamer(EventService eventService, ApplicationProperties applicationProperties) {
        this.eventService = eventService;
        this.applicationProperties = applicationProperties;
    }

    @Nonnull
    public StreamerValidationResult isReady() {
        return StreamerValidationResult.pass();
    }

    public Stream<LogRecord> stream(@Nonnull EntityStreamerQuery entityStreamerQuery) {
        SpaceOptOutFilter spaceOptOutFilter = new SpaceOptOutFilter(entityStreamerQuery.getOptOutEntityIdentifiers());
        EventQuery eventQuery = new EventQuery(entityStreamerQuery.getFrom(), Instant.now());
        return this.eventService.streamUnsecured(eventQuery).filter(event -> spaceOptOutFilter.test(event.getSpaceKey())).map(this::convert);
    }

    @VisibleForTesting
    LogRecord convert(Event event) {
        HashMap<String, Object> payload = new HashMap<String, Object>();
        payload.put(SharedAttributes.INSTANCE_URL.getInternalName(), this.applicationProperties.getBaseUrl(UrlMode.CANONICAL));
        payload.put(AfcEventAttributes.ID_ATTR.getInternalName(), event.getId());
        payload.put(AfcEventAttributes.NAME_ATTR.getInternalName(), event.getName());
        payload.put(SharedAttributes.CREATED_DATE.getInternalName(), DateTimeConverter.convertTimestampToDateTime((Instant)Instant.ofEpochMilli(event.getEventAt())));
        payload.put(AfcEventAttributes.AUTHOR_ATTR.getInternalName(), event.getUserKey());
        payload.put(AfcEventAttributes.SPACE_KEY_ATTR.getInternalName(), event.getSpaceKey());
        payload.put(AfcEventAttributes.CONTAINER_ID_ATTR.getInternalName(), event.getContainerId());
        payload.put(AfcEventAttributes.CONTENT_ID_ATTR.getInternalName(), event.getContentId());
        return LogRecord.getInstance((Object)event.getId(), (long)event.getEventAt(), payload);
    }
}

