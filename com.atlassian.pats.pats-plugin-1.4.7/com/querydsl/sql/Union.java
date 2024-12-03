/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.mysema.commons.lang.CloseableIterator;
import com.querydsl.core.Fetchable;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.SubQueryExpression;
import java.util.List;

public interface Union<RT>
extends SubQueryExpression<RT>,
Fetchable<RT> {
    @Deprecated
    public List<RT> list();

    @Override
    public CloseableIterator<RT> iterate();

    public Union<RT> groupBy(Expression<?> ... var1);

    public Union<RT> having(Predicate ... var1);

    public Union<RT> orderBy(OrderSpecifier<?> ... var1);

    public Expression<RT> as(String var1);

    public Expression<RT> as(Path<RT> var1);
}

