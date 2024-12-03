/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.FlushModeType
 *  javax.persistence.LockModeType
 */
package com.atlassian.migration.agent.store.jpa;

import com.atlassian.migration.agent.store.jpa.Page;
import com.atlassian.migration.agent.store.jpa.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.persistence.FlushModeType;
import javax.persistence.LockModeType;

public interface QueryBuilder<T> {
    public QueryBuilder<T> first(int var1);

    public QueryBuilder<T> max(int var1);

    public QueryBuilder<T> param(String var1, Object var2);

    public QueryBuilder<T> param(int var1, Object var2);

    public QueryBuilder<T> hint(String var1, Object var2);

    public QueryBuilder<T> lock(LockModeType var1);

    public QueryBuilder<T> flushMode(FlushModeType var1);

    public QueryBuilder<T> flush(boolean var1);

    public List<T> list();

    public T single();

    public Optional<T> first();

    public Stream<T> stream();

    public int update();

    public Page<T> page(int var1);

    public <P> Page<P> page(int var1, Function<T, P> var2);

    public Page<T> page(Pageable var1);

    public <P> Page<P> page(Pageable var1, Function<T, P> var2);
}

