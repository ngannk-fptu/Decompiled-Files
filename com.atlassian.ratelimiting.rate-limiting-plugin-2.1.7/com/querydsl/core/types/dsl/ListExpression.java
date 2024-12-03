/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnegative
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.CollectionExpression;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.SimpleExpression;
import java.util.List;
import javax.annotation.Nonnegative;

public interface ListExpression<E, Q extends SimpleExpression<? super E>>
extends CollectionExpression<List<E>, E> {
    public Q get(Expression<Integer> var1);

    public Q get(@Nonnegative int var1);
}

