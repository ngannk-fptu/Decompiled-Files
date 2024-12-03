/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.audit.api.AuditEntityCursor
 *  com.atlassian.audit.api.AuditQuery
 *  com.atlassian.audit.api.AuditSearchService
 *  com.atlassian.audit.api.util.pagination.Page
 *  com.atlassian.audit.api.util.pagination.PageRequest
 *  com.atlassian.audit.entity.AuditEntity
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.ao.service;

import com.atlassian.audit.ao.dao.AuditEntityDao;
import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import java.util.Objects;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DatabaseAuditSearchService
implements AuditSearchService {
    private static final int MAX_LIMIT = 10000;
    private final AuditEntityDao dao;

    public DatabaseAuditSearchService(AuditEntityDao dao) {
        this.dao = dao;
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(pageRequest, "pageRequest");
        if (pageRequest.getLimit() > 10000) {
            throw new IllegalArgumentException("Maximum allowed page size is 10000");
        }
        if (scanLimit == Integer.MAX_VALUE) {
            return this.dao.findBy(query, pageRequest);
        }
        return this.dao.findBy(query, pageRequest, scanLimit);
    }

    public void stream(@Nonnull AuditQuery query, int offset, int limit, @Nonnull Consumer<AuditEntity> consumer) {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(consumer, "consumer");
        this.dao.stream(query, consumer, offset, limit);
    }

    public long count(@Nullable AuditQuery query) {
        if (query == null) {
            return this.dao.count();
        }
        return this.dao.count(query);
    }
}

