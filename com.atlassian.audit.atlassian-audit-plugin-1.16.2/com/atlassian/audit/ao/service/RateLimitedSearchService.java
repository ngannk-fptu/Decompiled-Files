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
 *  com.google.common.base.Preconditions
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.audit.ao.service;

import com.atlassian.audit.api.AuditEntityCursor;
import com.atlassian.audit.api.AuditQuery;
import com.atlassian.audit.api.AuditSearchService;
import com.atlassian.audit.api.util.pagination.Page;
import com.atlassian.audit.api.util.pagination.PageRequest;
import com.atlassian.audit.entity.AuditEntity;
import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RateLimitedSearchService
implements AuditSearchService {
    private final Semaphore textSearchLimiter;
    private final Semaphore nonTextSearchLimiter;
    private final int queryTimeoutSeconds;
    private final AuditSearchService delegate;

    public RateLimitedSearchService(int maxConcurrentTextSearchRequests, int maxConcurrentNonTextSearchRequests, int queryTimeoutSeconds, AuditSearchService delegate) {
        Preconditions.checkArgument((maxConcurrentTextSearchRequests > 0 ? 1 : 0) != 0, (Object)"Max concurrent text search requests should be at least 1");
        Preconditions.checkArgument((maxConcurrentNonTextSearchRequests > 0 ? 1 : 0) != 0, (Object)"Max concurrent non-text search requests should be at least 1");
        this.textSearchLimiter = new Semaphore(maxConcurrentTextSearchRequests);
        this.nonTextSearchLimiter = new Semaphore(maxConcurrentNonTextSearchRequests);
        this.queryTimeoutSeconds = queryTimeoutSeconds;
        this.delegate = delegate;
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest) throws TimeoutException {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(pageRequest, "pageRequest");
        try {
            if (query.getSearchText().isPresent()) {
                return this.tryFindAuditEntities(query, pageRequest, this.textSearchLimiter);
            }
            return this.tryFindAuditEntities(query, pageRequest, this.nonTextSearchLimiter);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    @Nonnull
    public Page<AuditEntity, AuditEntityCursor> findBy(@Nonnull AuditQuery query, @Nonnull PageRequest<AuditEntityCursor> pageRequest, int scanLimit) throws TimeoutException {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(pageRequest, "pageRequest");
        try {
            if (query.getSearchText().isPresent()) {
                return this.tryFindAuditEntities(query, pageRequest, this.textSearchLimiter, scanLimit);
            }
            return this.tryFindAuditEntities(query, pageRequest, this.nonTextSearchLimiter, scanLimit);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    public void stream(@Nonnull AuditQuery query, int offset, int limit, @Nonnull Consumer<AuditEntity> consumer) throws TimeoutException {
        Objects.requireNonNull(query, "query");
        Objects.requireNonNull(consumer, "consumer");
        try {
            if (query.getSearchText().isPresent()) {
                this.tryStream(query, consumer, this.textSearchLimiter, offset, limit);
            } else {
                this.tryStream(query, consumer, this.nonTextSearchLimiter, offset, limit);
            }
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void tryStream(AuditQuery query, Consumer<AuditEntity> consumer, Semaphore semaphore, int offset, int limit) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(this.queryTimeoutSeconds, TimeUnit.SECONDS)) {
            try {
                this.delegate.stream(query, offset, limit, consumer);
            }
            finally {
                semaphore.release();
            }
        } else {
            throw new TimeoutException("Can't perform streamed search as there are many other search requests in progress");
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Page<AuditEntity, AuditEntityCursor> tryFindAuditEntities(AuditQuery query, PageRequest<AuditEntityCursor> pageRequest, Semaphore semaphore, int scanLimit) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(this.queryTimeoutSeconds, TimeUnit.SECONDS)) {
            try {
                Page page = this.delegate.findBy(query, pageRequest, scanLimit);
                return page;
            }
            finally {
                semaphore.release();
            }
        }
        throw new TimeoutException("Can't perform search as there are many other search requests in progress");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Page<AuditEntity, AuditEntityCursor> tryFindAuditEntities(AuditQuery query, PageRequest<AuditEntityCursor> pageRequest, Semaphore semaphore) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(this.queryTimeoutSeconds, TimeUnit.SECONDS)) {
            try {
                Page page = this.delegate.findBy(query, pageRequest);
                return page;
            }
            finally {
                semaphore.release();
            }
        }
        throw new TimeoutException("Can't perform search as there are many other search requests in progress");
    }

    public long count(@Nullable AuditQuery query) throws TimeoutException {
        try {
            if (query != null && query.getSearchText().isPresent()) {
                return this.tryCountAuditEntities(query, this.textSearchLimiter);
            }
            return this.tryCountAuditEntities(query, this.nonTextSearchLimiter);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private long tryCountAuditEntities(AuditQuery query, Semaphore semaphore) throws InterruptedException, TimeoutException {
        if (semaphore.tryAcquire(this.queryTimeoutSeconds, TimeUnit.SECONDS)) {
            try {
                long l = this.delegate.count(query);
                return l;
            }
            finally {
                semaphore.release();
            }
        }
        throw new TimeoutException("Can't perform count as there are many other count requests in progress");
    }
}

