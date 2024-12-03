/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.ComparablePath;

public class ComparableEntityPath<T extends Comparable>
extends ComparablePath<T>
implements EntityPath<T> {
    private static final long serialVersionUID = -7115848171352092315L;

    protected ComparableEntityPath(Class<? extends T> type, Path<?> parent, String property) {
        super(type, parent, property);
    }

    protected ComparableEntityPath(Class<? extends T> type, PathMetadata metadata) {
        super(type, metadata);
    }

    protected ComparableEntityPath(Class<? extends T> type, String var) {
        super(type, var);
    }

    @Override
    public Object getMetadata(Path<?> property) {
        return null;
    }
}

