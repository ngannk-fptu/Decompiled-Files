/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;

public interface ParameterizedExpression<T>
extends Expression<T> {
    public Class<?> getParameter(int var1);
}

