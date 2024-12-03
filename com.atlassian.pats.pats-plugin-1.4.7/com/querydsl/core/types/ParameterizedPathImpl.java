/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.ParameterizedExpression;
import com.querydsl.core.types.PathImpl;
import com.querydsl.core.types.PathMetadata;

public class ParameterizedPathImpl<T>
extends PathImpl<T>
implements ParameterizedExpression<T> {
    private static final long serialVersionUID = -498707460985111265L;
    private final Class<?>[] parameterTypes;

    public ParameterizedPathImpl(Class<? extends T> type, PathMetadata metadata, Class<?> ... parameterTypes) {
        super(type, metadata);
        this.parameterTypes = parameterTypes;
    }

    @Override
    public Class<?> getParameter(int index) {
        return this.parameterTypes[index];
    }
}

