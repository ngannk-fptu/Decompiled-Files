/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.atlassian.applinks.internal.rest.model;

import com.atlassian.applinks.internal.rest.model.ReadOnlyRestRepresentation;
import com.atlassian.applinks.internal.rest.model.RestRepresentation;
import com.google.common.base.Function;
import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class RestRepresentations {
    private RestRepresentations() {
        throw new AssertionError((Object)("Do not instantiate " + this.getClass().getSimpleName()));
    }

    @Nonnull
    public static <R extends RestRepresentation<?>> R fromMap(@Nonnull Map<String, Object> original, @Nonnull Class<R> entityType) {
        Objects.requireNonNull(original, "original");
        Objects.requireNonNull(entityType, "entityType");
        try {
            return (R)((RestRepresentation)entityType.getConstructor(Map.class).newInstance(original));
        }
        catch (Exception e) {
            throw new RuntimeException("Expected Map constructor in RestRepresentation " + entityType.getName(), e);
        }
    }

    @Nullable
    public static <T, R extends ReadOnlyRestRepresentation<T>> R fromDomainObject(@Nullable T original, @Nonnull Class<R> restRepresentationType) {
        Objects.requireNonNull(restRepresentationType, "restRepresentationType");
        if (original == null) {
            return null;
        }
        try {
            return (R)((ReadOnlyRestRepresentation)RestRepresentations.findConstructor(original.getClass(), restRepresentationType).newInstance(original));
        }
        catch (Exception e) {
            throw new IllegalStateException(String.format("Failed to instantiate REST representation '%s' from '%s'", restRepresentationType.getName(), original.getClass().getName()), e);
        }
    }

    @Nonnull
    public static <T, R extends ReadOnlyRestRepresentation<T>> Function<T, R> fromDomainFunction(final @Nonnull Class<R> restRepresentationType) {
        Objects.requireNonNull(restRepresentationType, "restRepresentationType");
        return new Function<T, R>(){

            public R apply(@Nullable T input) {
                return RestRepresentations.fromDomainObject(input, restRepresentationType);
            }
        };
    }

    private static <T, R extends ReadOnlyRestRepresentation<T>> Constructor<R> findConstructor(Class<?> domainClass, Class<R> representationClass) {
        for (Constructor<?> constructor : representationClass.getConstructors()) {
            if (constructor.getParameterTypes().length != 1 || !constructor.getParameterTypes()[0].isAssignableFrom(domainClass)) continue;
            return constructor;
        }
        throw new IllegalStateException(String.format("REST representation class '%s' does not provide copy constructor accepting domain class '%s', as required by the contract in ReadOnlyRestRepresentation", representationClass.getName(), domainClass.getName()));
    }
}

