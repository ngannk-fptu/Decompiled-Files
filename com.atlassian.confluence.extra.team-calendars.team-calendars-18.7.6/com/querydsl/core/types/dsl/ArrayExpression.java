/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.core.types.dsl.SimpleExpression;
import javax.annotation.Nonnegative;

public interface ArrayExpression<A, T>
extends Expression<A> {
    public NumberExpression<Integer> size();

    public SimpleExpression<T> get(Expression<Integer> var1);

    public SimpleExpression<T> get(@Nonnegative int var1);
}

