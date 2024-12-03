/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.support;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.SubQueryExpression;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.BooleanOperation;
import java.util.Collection;

public interface ExtendedSubQuery<T>
extends SubQueryExpression<T> {
    public BooleanExpression eq(Expression<? extends T> var1);

    public BooleanExpression eq(T var1);

    public BooleanExpression ne(Expression<? extends T> var1);

    public BooleanExpression ne(T var1);

    public BooleanExpression contains(Expression<? extends T> var1);

    public BooleanExpression contains(T var1);

    public BooleanExpression exists();

    public BooleanExpression notExists();

    public BooleanExpression lt(Expression<? extends T> var1);

    public BooleanExpression lt(T var1);

    public BooleanExpression gt(Expression<? extends T> var1);

    public BooleanExpression gt(T var1);

    public BooleanExpression loe(Expression<? extends T> var1);

    public BooleanExpression loe(T var1);

    public BooleanExpression goe(Expression<? extends T> var1);

    public BooleanExpression goe(T var1);

    public BooleanOperation isNull();

    public BooleanOperation isNotNull();

    public BooleanExpression in(Collection<? extends T> var1);

    public BooleanExpression in(T ... var1);
}

