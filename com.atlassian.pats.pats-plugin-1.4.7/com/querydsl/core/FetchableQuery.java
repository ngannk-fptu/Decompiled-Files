/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.Fetchable;
import com.querydsl.core.ResultTransformer;
import com.querydsl.core.SimpleQuery;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Expression;

public interface FetchableQuery<T, Q extends FetchableQuery<T, Q>>
extends SimpleQuery<Q>,
Fetchable<T> {
    public <U> FetchableQuery<U, ?> select(Expression<U> var1);

    public FetchableQuery<Tuple, ?> select(Expression<?> ... var1);

    public <S> S transform(ResultTransformer<S> var1);
}

