/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  bucket.core.persistence.ObjectDao
 *  com.atlassian.confluence.api.model.pagination.LimitedRequest
 *  com.atlassian.confluence.api.model.pagination.PageResponse
 *  com.atlassian.core.bean.EntityObject
 *  com.google.common.base.Predicate
 *  org.checkerframework.checker.nullness.qual.NonNull
 *  org.springframework.transaction.annotation.Propagation
 *  org.springframework.transaction.annotation.Transactional
 */
package com.atlassian.confluence.internal.persistence;

import bucket.core.persistence.ObjectDao;
import com.atlassian.confluence.api.model.pagination.LimitedRequest;
import com.atlassian.confluence.api.model.pagination.PageResponse;
import com.atlassian.core.bean.EntityObject;
import java.util.List;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ObjectDaoInternal<T extends EntityObject>
extends ObjectDao {
    @Deprecated
    @Transactional
    public void remove(EntityObject var1);

    @Transactional
    public void removeEntity(T var1);

    @Deprecated
    @Transactional
    public void refresh(EntityObject var1);

    @Transactional
    public void refreshEntity(T var1);

    @Deprecated
    @Transactional
    public void replicate(Object var1);

    @Transactional
    public void replicateEntity(T var1);

    public @NonNull List<T> findAll();

    public @NonNull List<T> findAllSorted(String var1);

    public @NonNull List<T> findAllSorted(String var1, boolean var2, int var3, int var4);

    @Deprecated
    public <E> @NonNull PageResponse<E> findByClassIds(Iterable<Long> var1, LimitedRequest var2, com.google.common.base.Predicate<? super E> var3);

    public @NonNull PageResponse<T> findByClassIdsFiltered(Iterable<Long> var1, LimitedRequest var2, Predicate<? super T> var3);

    @Deprecated
    @Transactional
    public void save(EntityObject var1);

    @Transactional
    public void saveEntity(T var1);

    @Transactional(readOnly=true, propagation=Propagation.SUPPORTS)
    public Class<T> getPersistentClass();

    @Deprecated
    @Transactional
    public void saveRaw(EntityObject var1);

    @Transactional
    @Deprecated(forRemoval=true)
    public void saveRawEntity(T var1);
}

