/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.dml;

import com.querydsl.core.FilteredClause;
import com.querydsl.core.dml.StoreClause;
import com.querydsl.core.types.Path;
import java.util.List;

public interface UpdateClause<C extends UpdateClause<C>>
extends StoreClause<C>,
FilteredClause<C> {
    @Override
    public C set(List<? extends Path<?>> var1, List<?> var2);
}

