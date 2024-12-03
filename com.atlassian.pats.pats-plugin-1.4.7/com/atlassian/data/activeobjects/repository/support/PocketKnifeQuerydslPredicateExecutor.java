/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package com.atlassian.data.activeobjects.repository.support;

import com.atlassian.pocketknife.api.querydsl.DatabaseConnection;
import com.atlassian.pocketknife.api.querydsl.util.OnRollback;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import java.util.List;
import java.util.function.Function;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.lang.Nullable;

public interface PocketKnifeQuerydslPredicateExecutor<T>
extends QuerydslPredicateExecutor<T> {
    @Override
    public List<T> findAll(Predicate var1);

    @Override
    public List<T> findAll(Predicate var1, Sort var2);

    @Override
    public List<T> findAll(Predicate var1, OrderSpecifier<?> ... var2);

    @Override
    public List<T> findAll(OrderSpecifier<?> ... var1);

    public <R> R executeQuery(@Nullable TransactionType var1, Function<DatabaseConnection, R> var2, @Nullable OnRollback var3);

    public static enum TransactionType {
        IN_NEW_TRANSACTION,
        IN_TRANSACTION;

    }
}

