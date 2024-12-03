/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.alias;

import com.querydsl.core.types.Path;
import com.querydsl.core.types.PathMetadata;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface PathFactory {
    public <T> Path<T[]> createArrayPath(Class<T[]> var1, PathMetadata var2);

    public <T> Path<T> createEntityPath(Class<T> var1, PathMetadata var2);

    public <T> Path<T> createSimplePath(Class<T> var1, PathMetadata var2);

    public <T extends Comparable<?>> Path<T> createComparablePath(Class<T> var1, PathMetadata var2);

    public <T extends Enum<T>> Path<T> createEnumPath(Class<T> var1, PathMetadata var2);

    public <T extends Comparable<?>> Path<T> createDatePath(Class<T> var1, PathMetadata var2);

    public <T extends Comparable<?>> Path<T> createTimePath(Class<T> var1, PathMetadata var2);

    public <T extends Comparable<?>> Path<T> createDateTimePath(Class<T> var1, PathMetadata var2);

    public <T extends Number> Path<T> createNumberPath(Class<T> var1, PathMetadata var2);

    public Path<Boolean> createBooleanPath(PathMetadata var1);

    public Path<String> createStringPath(PathMetadata var1);

    public <E> Path<List<E>> createListPath(Class<E> var1, PathMetadata var2);

    public <E> Path<Set<E>> createSetPath(Class<E> var1, PathMetadata var2);

    public <E> Path<Collection<E>> createCollectionPath(Class<E> var1, PathMetadata var2);

    public <K, V> Path<Map<K, V>> createMapPath(Class<K> var1, Class<V> var2, PathMetadata var3);
}

