/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.AuditService
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditAttribute
 *  com.atlassian.audit.entity.AuditEntity
 *  com.atlassian.audit.entity.AuditEvent
 *  com.atlassian.audit.entity.AuditType
 *  com.atlassian.audit.entity.CoverageArea
 *  com.atlassian.audit.entity.CoverageLevel
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.ao.service;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.AuditService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditAttribute;
import com.atlassian.audit.entity.AuditEntity;
import com.atlassian.audit.entity.AuditEvent;
import com.atlassian.audit.entity.AuditType;
import com.atlassian.audit.entity.CoverageArea;
import com.atlassian.audit.entity.CoverageLevel;
import java.util.Comparator;
import java.util.Optional;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AuditedSearchService
implements AuditSearchService {
    private static final AuditType AUDIT_EVENT_SEARCHED = AuditType.fromI18nKeys((CoverageArea)CoverageArea.AUDIT_LOG, (CoverageLevel)CoverageLevel.BASE, (String)"atlassian.audit.event.category.audit", (String)"atlassian.audit.event.action.audit.search").build();
    private final AuditSearchService delegate;
    private final AuditService auditService;

    public AuditedSearchService(AuditSearchService delegate, AuditService auditService) {
        this.delegate = delegate;
        this.auditService = auditService;
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) throws TimeoutException {
        Page page = this.delegate.findBy(query, pageRequest, scanLimit);
        this.auditService.audit(this.createSearchAuditEvent(query, (Page<AuditEntity, AuditEntityCursor>)page, scanLimit));
        return page;
    }

    @VisibleForTesting
    public AuditEvent createSearchAuditEvent(AuditQuery query, Page<AuditEntity, AuditEntityCursor> page, int scanLimit) {
        String queryStr = AuditedSearchService.auditQueryToString(query, scanLimit);
        Optional<AuditEntity> minId = page.getValues().stream().min(Comparator.comparingLong(AuditEntity::getId));
        Optional<AuditEntity> maxId = page.getValues().stream().max(Comparator.comparingLong(AuditEntity::getId));
        Optional<AuditEntity> minTimestamp = page.getValues().stream().min(Comparator.comparing(AuditEntity::getTimestamp));
        Optional<AuditEntity> maxTimestamp = page.getValues().stream().max(Comparator.comparing(AuditEntity::getTimestamp));
        return AuditEvent.builder((AuditType)AUDIT_EVENT_SEARCHED).extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.query", (String)queryStr).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.results", (String)String.valueOf(page.getSize())).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.timestamp", (String)(minTimestamp.isPresent() && maxTimestamp.isPresent() ? String.format("%s - %s", minTimestamp.get().getTimestamp(), maxTimestamp.get().getTimestamp()) : "no results")).build()).extraAttribute(AuditAttribute.fromI18nKeys((String)"atlassian.audit.event.attribute.id", (String)(minId.isPresent() && maxId.isPresent() ? String.format("%s - %s", minId.get().getId(), maxId.get().getId()) : "no results")).build()).build();
    }

    public static String auditQueryToString(AuditQuery query, int scanLimit) {
        return (query.getFrom().isPresent() ? String.format("From : %s;", query.getFrom().get()) : "") + (query.getTo().isPresent() ? String.format("To : %s;", query.getTo().get()) : "") + (!query.getUserIds().isEmpty() ? String.format("UserIds : %s;", query.getUserIds()) : "") + (!query.getResources().isEmpty() ? String.format("Resources : %s", query.getResources().toString()) : "") + (!query.getCategories().isEmpty() ? String.format("Categories : %s;", query.getCategories()) : "") + (!query.getActions().isEmpty() ? String.format("Actions : %s;", query.getActions()) : "") + (scanLimit < Integer.MAX_VALUE ? String.format("ScanLimit : %s;", scanLimit) : "") + (query.getSearchText().isPresent() ? String.format("Freetext : %s;", query.getSearchText().get()) : "");
    }

    public void stream(@Nonnull AuditQuery query, int offset, int limit, @Nonnull Consumer<AuditEntity> consumer) throws TimeoutException {
        this.delegate.stream(query, offset, limit, consumer);
    }

    public long count(@Nullable AuditQuery query) throws TimeoutException {
        return this.delegate.count(query);
    }
}

