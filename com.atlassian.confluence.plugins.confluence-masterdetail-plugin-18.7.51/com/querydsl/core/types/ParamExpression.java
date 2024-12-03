/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;

public interface ParamExpression<T>
extends Expression<T> {
    public String getName();

    public boolean isAnon();

    public String getNotSetMessage();
}

