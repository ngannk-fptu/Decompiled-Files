/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types.dsl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.BeanPath;
import com.querydsl.core.types.dsl.PathInits;
import javax.annotation.Nullable;

public class EntityPathBase<T>
extends BeanPath<T>
implements EntityPath<T> {
    private static final long serialVersionUID = -8610055828414880996L;

    public EntityPathBase(Class<? extends T> type, String variable) {
        super(type, variable);
    }

    public EntityPathBase(Class<? extends T> type, PathMetadata metadata) {
        super(type, metadata);
    }

    public EntityPathBase(Class<? extends T> type, PathMetadata metadata, @Nullable PathInits inits) {
        super(type, metadata, inits);
    }

    @Override
    public Object getMetadata(Path<?> property) {
        return null;
    }
}

