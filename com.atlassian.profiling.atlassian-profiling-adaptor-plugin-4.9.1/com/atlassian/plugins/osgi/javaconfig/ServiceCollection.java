/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  javax.annotation.Nonnull
 *  org.eclipse.gemini.blueprint.service.importer.support.CollectionType
 */
package com.atlassian.plugins.osgi.javaconfig;

import com.atlassian.annotations.PublicApi;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.annotation.Nonnull;
import org.eclipse.gemini.blueprint.service.importer.support.CollectionType;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@PublicApi
public final class ServiceCollection<T> {
    private final Class<?> serviceClass;
    private final CollectionType collectionType;
    private final Comparator<?> comparator;

    private ServiceCollection(Class<?> serviceClass, CollectionType collectionType) {
        this(serviceClass, collectionType, null);
    }

    private ServiceCollection(Class<?> serviceClass, CollectionType collectionType, Comparator<?> comparator) {
        Objects.requireNonNull(serviceClass, " Service class should not be null.");
        this.serviceClass = serviceClass;
        this.collectionType = collectionType;
        this.comparator = comparator;
    }

    public static <S> ServiceCollection<List<S>> list(@Nonnull Class<S> serviceClass) {
        return new ServiceCollection<List<S>>(serviceClass, CollectionType.LIST);
    }

    public static <S> ServiceCollection<List<S>> sortedList(@Nonnull Class<S> serviceClass) {
        return new ServiceCollection<List<S>>(serviceClass, CollectionType.SORTED_LIST);
    }

    public static <S> ServiceCollection<List<S>> sortedList(@Nonnull Class<S> serviceClass, Comparator<? super S> comparator) {
        return new ServiceCollection<List<S>>(serviceClass, CollectionType.SORTED_LIST, comparator);
    }

    public static <S> ServiceCollection<Set<S>> set(@Nonnull Class<S> serviceClass) {
        return new ServiceCollection<Set<S>>(serviceClass, CollectionType.SET);
    }

    public static <S> ServiceCollection<Set<S>> sortedSet(@Nonnull Class<S> serviceClass) {
        return new ServiceCollection<Set<S>>(serviceClass, CollectionType.SORTED_SET);
    }

    public static <S> ServiceCollection<Set<S>> sortedSet(@Nonnull Class<S> serviceClass, Comparator<? super S> comparator) {
        return new ServiceCollection<Set<S>>(serviceClass, CollectionType.SORTED_SET, comparator);
    }

    public Comparator<?> getComparator() {
        return this.comparator;
    }

    CollectionType getCollectionType() {
        return this.collectionType;
    }

    Class<?> getServiceClass() {
        return this.serviceClass;
    }
}

