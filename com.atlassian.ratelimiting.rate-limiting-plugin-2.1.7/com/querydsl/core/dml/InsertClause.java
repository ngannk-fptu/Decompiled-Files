/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.dml;

import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.SubQueryExpression;

public interface InsertClause<C extends InsertClause<C>>
extends StoreClause<C> {
    public C columns(Path<?> ... var1);

    public C select(SubQueryExpression<?> var1);

    public C values(Object ... var1);
}

