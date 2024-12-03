/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package com.querydsl.core.dml;

import com.querydsl.core.dml.DMLClause;
import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import javax.annotation.Nullable;

public interface StoreClause<C extends StoreClause<C>>
extends DMLClause<C> {
    public <T> C set(Path<T> var1, @Nullable T var2);

    public <T> C set(Path<T> var1, Expression<? extends T> var2);

    public <T> C setNull(Path<T> var1);

    public boolean isEmpty();
}

