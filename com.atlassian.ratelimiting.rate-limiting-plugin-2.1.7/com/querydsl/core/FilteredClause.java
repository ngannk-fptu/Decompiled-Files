/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core;

import com.querydsl.core.types.Predicate;

public interface FilteredClause<C extends FilteredClause<C>> {
    public C where(Predicate ... var1);
}

