/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.types;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.PathType;
import javax.annotation.Nonnegative;

public final class PathMetadataFactory {
    public static PathMetadata forArrayAccess(Path<?> parent, Expression<Integer> index) {
        return new PathMetadata(parent, index, PathType.ARRAYVALUE);
    }

    public static PathMetadata forArrayAccess(Path<?> parent, @Nonnegative int index) {
        return new PathMetadata(parent, index, PathType.ARRAYVALUE_CONSTANT);
    }

    public static PathMetadata forCollectionAny(Path<?> parent) {
        return new PathMetadata(parent, "", PathType.COLLECTION_ANY);
    }

    public static <T> PathMetadata forDelegate(Path<T> delegate) {
        return new PathMetadata(delegate, delegate, PathType.DELEGATE);
    }

    public static PathMetadata forListAccess(Path<?> parent, Expression<Integer> index) {
        return new PathMetadata(parent, index, PathType.LISTVALUE);
    }

    public static PathMetadata forListAccess(Path<?> parent, @Nonnegative int index) {
        return new PathMetadata(parent, index, PathType.LISTVALUE_CONSTANT);
    }

    public static <KT> PathMetadata forMapAccess(Path<?> parent, Expression<KT> key) {
        return new PathMetadata(parent, key, PathType.MAPVALUE);
    }

    public static <KT> PathMetadata forMapAccess(Path<?> parent, KT key) {
        return new PathMetadata(parent, key, PathType.MAPVALUE_CONSTANT);
    }

    public static PathMetadata forProperty(Path<?> parent, String property) {
        return new PathMetadata(parent, property, PathType.PROPERTY);
    }

    public static PathMetadata forVariable(String variable) {
        return new PathMetadata(null, variable, PathType.VARIABLE);
    }

    private PathMetadataFactory() {
    }
}

