/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnegative
 */
package com.querydsl.core;

import com.querydsl.core.FilteredClause;
import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.ParamExpression;
import javax.annotation.Nonnegative;

public interface SimpleQuery<Q extends SimpleQuery<Q>>
extends FilteredClause<Q> {
    public Q limit(@Nonnegative long var1);

    public Q offset(@Nonnegative long var1);

    public Q restrict(QueryModifiers var1);

    public Q orderBy(OrderSpecifier<?> ... var1);

    public <T> Q set(ParamExpression<T> var1, T var2);

    public Q distinct();
}

