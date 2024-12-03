/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.types.Expression;

public interface GroupExpression<T, R>
extends Expression<R> {
    public Expression<T> getExpression();

    public GroupCollector<T, R> createGroupCollector();
}

