/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;

public interface Constant<T>
extends Expression<T> {
    public T getConstant();
}

