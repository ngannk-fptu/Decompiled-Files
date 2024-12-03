/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.data.activeobjects.repository.support.PocketKnifeQuerydslPredicateExecutor;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.lang.Nullable;

public interface PocketKnifeCrudQuerydslPredicateExecutor<T, ID>
extends PocketKnifeQuerydslPredicateExecutor<T> {
    public <S extends T> S save(S var1);

    public <S extends T> S save(S var1, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var2, @Nullable OnRollback var3);

    public <S extends T> List<S> saveAll(S ... var1);

    public <S extends T> List<S> saveAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var1, @Nullable OnRollback var2, S ... var3);

    public Optional<T> findById(ID var1);

    public boolean existsById(ID var1);

    public long count();

    public long delete(Predicate var1);

    public long delete(Predicate var1, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var2, @Nullable OnRollback var3);

    public long deleteById(ID var1);

    public long deleteById(ID var1, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var2, @Nullable OnRollback var3);

    public long delete(T var1);

    public long delete(T var1, @Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var2, @Nullable OnRollback var3);

    public long deleteAll(T ... var1);

    public long deleteAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var1, @Nullable OnRollback var2, T ... var3);

    public long deleteAll();

    public long deleteAll(@Nullable PocketKnifeQuerydslPredicateExecutor.TransactionType var1, @Nullable OnRollback var2);

    public Page<T> findAll(Pageable var1);

    public List<T> findAll(Sort var1);
}

