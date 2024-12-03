/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.PathMetadata;
import java.lang.reflect.AnnotatedElement;

public interface Path<T>
extends Expression<T> {
    public PathMetadata getMetadata();

    public Path<?> getRoot();

    public AnnotatedElement getAnnotatedElement();
}

