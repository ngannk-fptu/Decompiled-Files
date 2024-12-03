/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.SimpleQuery;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;

public interface Query<Q extends Query<Q>>
extends SimpleQuery<Q> {
    public Q groupBy(Expression<?> ... var1);

    public Q having(Predicate ... var1);
}

