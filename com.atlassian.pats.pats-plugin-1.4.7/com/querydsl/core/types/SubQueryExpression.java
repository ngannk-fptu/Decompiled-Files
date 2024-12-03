/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.QueryMetadata;
import com.querydsl.core.types.Expression;

public interface SubQueryExpression<T>
extends Expression<T> {
    public QueryMetadata getMetadata();
}

