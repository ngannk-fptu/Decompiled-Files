/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Operator;
import java.util.List;

public interface Operation<T>
extends Expression<T> {
    public Expression<?> getArg(int var1);

    public List<Expression<?>> getArgs();

    public Operator getOperator();
}

