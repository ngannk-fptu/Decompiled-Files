/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import java.util.List;
import javax.annotation.Nullable;

public interface FactoryExpression<T>
extends Expression<T> {
    public List<Expression<?>> getArgs();

    @Nullable
    public T newInstance(Object ... var1);
}

