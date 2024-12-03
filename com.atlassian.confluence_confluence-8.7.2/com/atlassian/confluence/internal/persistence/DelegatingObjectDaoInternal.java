/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 */
package com.atlassian.confluence.internal.persistence;

import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.confluence.internal.persistence.ObjectDaoInternal;
import com.atlassian.core.bean.EntityObject;
import java.util.List;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;

public class DelegatingObjectDaoInternal<T extends EntityObject>
implements ObjectDaoInternal<T> {
    private final ObjectDaoInternal<T> delegate;

    public DelegatingObjectDaoInternal(ObjectDaoInternal<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    @Deprecated
    public void remove(EntityObject objectToRemove) {
        this.delegate.remove(objectToRemove);
    }

    @Override
    public void removeEntity(T objectToRemove) {
        this.delegate.removeEntity(objectToRemove);
    }

    @Override
    @Deprecated
    public void refresh(EntityObject objectToRefresh) {
        this.delegate.refresh(objectToRefresh);
    }

    @Override
    public void refreshEntity(T objectToRefresh) {
        this.delegate.refreshEntity(objectToRefresh);
    }

    @Override
    @Deprecated
    public void replicate(Object objectToReplicate) {
        this.delegate.replicate(objectToReplicate);
    }

    @Override
    public void replicateEntity(T objectToReplicate) {
        this.delegate.replicateEntity(objectToReplicate);
    }

    @Override
    public @NonNull List<T> findAll() {
        return this.delegate.findAll();
    }

    @Override
    public @NonNull List<T> findAllSorted(String sortField) {
        return this.delegate.findAllSorted(sortField);
    }

    @Override
    public @NonNull List<T> findAllSorted(String sortField, boolean cacheable, int offset, int maxResultCount) {
        return this.delegate.findAllSorted(sortField, cacheable, offset, maxResultCount);
    }

    @Override
    @Deprecated
    public <E> @NonNull PageResponse<E> findByClassIds(Iterable<Long> ids, LimitedRequest limitedRequest, com.google.common.base.Predicate<? super E> filter) {
        return this.delegate.findByClassIds(ids, limitedRequest, filter);
    }

    @Override
    public @NonNull PageResponse<T> findByClassIdsFiltered(Iterable<Long> ids, LimitedRequest limitedRequest, Predicate<? super T> filter) {
        return this.delegate.findByClassIdsFiltered(ids, limitedRequest, filter);
    }

    @Override
    @Deprecated
    public void save(EntityObject objectToSave) {
        this.delegate.save(objectToSave);
    }

    @Override
    public void saveEntity(T objectToSave) {
        this.delegate.saveEntity(objectToSave);
    }

    @Override
    public Class<T> getPersistentClass() {
        return this.delegate.getPersistentClass();
    }

    @Override
    @Deprecated
    public void saveRaw(EntityObject objectToSave) {
        this.delegate.saveRaw(objectToSave);
    }

    @Override
    public void saveRawEntity(T objectToSave) {
        this.delegate.saveRawEntity(objectToSave);
    }
}

