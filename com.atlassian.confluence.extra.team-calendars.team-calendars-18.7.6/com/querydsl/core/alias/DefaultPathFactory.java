/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.alias;

import com.querydsl.core.alias.PathFactory;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.core.types.dsl.Expressions;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DefaultPathFactory
implements PathFactory {
    @Override
    public <T> Path<T[]> createArrayPath(Class<T[]> arrayType, PathMetadata metadata) {
        return Expressions.arrayPath(arrayType, metadata);
    }

    @Override
    public Path<Boolean> createBooleanPath(PathMetadata metadata) {
        return Expressions.booleanPath(metadata);
    }

    @Override
    public <E> Path<Collection<E>> createCollectionPath(Class<E> elementType, PathMetadata metadata) {
        return Expressions.collectionPath(elementType, EntityPathBase.class, metadata);
    }

    @Override
    public <T extends Comparable<?>> Path<T> createComparablePath(Class<T> type, PathMetadata metadata) {
        return Expressions.comparablePath(type, metadata);
    }

    @Override
    public <T extends Comparable<?>> Path<T> createDatePath(Class<T> type, PathMetadata metadata) {
        return Expressions.datePath(type, metadata);
    }

    @Override
    public <T extends Comparable<?>> Path<T> createDateTimePath(Class<T> type, PathMetadata metadata) {
        return Expressions.dateTimePath(type, metadata);
    }

    @Override
    public <T> Path<T> createEntityPath(Class<T> type, PathMetadata metadata) {
        if (Comparable.class.isAssignableFrom(type)) {
            return Expressions.comparableEntityPath(type, metadata);
        }
        return new EntityPathBase<T>(type, metadata);
    }

    @Override
    public <T extends Enum<T>> Path<T> createEnumPath(Class<T> type, PathMetadata metadata) {
        return Expressions.enumPath(type, metadata);
    }

    @Override
    public <E> Path<List<E>> createListPath(Class<E> elementType, PathMetadata metadata) {
        return Expressions.listPath(elementType, EntityPathBase.class, metadata);
    }

    @Override
    public <K, V> Path<Map<K, V>> createMapPath(Class<K> keyType, Class<V> valueType, PathMetadata metadata) {
        return Expressions.mapPath(keyType, valueType, EntityPathBase.class, metadata);
    }

    @Override
    public <T extends Number> Path<T> createNumberPath(Class<T> type, PathMetadata metadata) {
        return Expressions.numberPath(type, metadata);
    }

    @Override
    public <E> Path<Set<E>> createSetPath(Class<E> elementType, PathMetadata metadata) {
        return Expressions.setPath(elementType, EntityPathBase.class, metadata);
    }

    @Override
    public <T> Path<T> createSimplePath(Class<T> type, PathMetadata metadata) {
        return Expressions.path(type, metadata);
    }

    @Override
    public Path<String> createStringPath(PathMetadata metadata) {
        return Expressions.stringPath(metadata);
    }

    @Override
    public <T extends Comparable<?>> Path<T> createTimePath(Class<T> type, PathMetadata metadata) {
        return Expressions.timePath(type, metadata);
    }
}

