/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.mapping.context;

import java.util.Collection;
import java.util.function.Predicate;
import org.springframework.data.mapping.MappingException;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.mapping.PersistentPropertyPath;
import org.springframework.data.mapping.PersistentPropertyPaths;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.data.mapping.context.InvalidPersistentPropertyPath;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;

public interface MappingContext<E extends PersistentEntity<?, P>, P extends PersistentProperty<P>> {
    public Collection<E> getPersistentEntities();

    @Nullable
    public E getPersistentEntity(Class<?> var1);

    default public E getRequiredPersistentEntity(Class<?> type) throws MappingException {
        E entity = this.getPersistentEntity((P)type);
        if (entity != null) {
            return entity;
        }
        throw new MappingException(String.format("Couldn't find PersistentEntity for type %s!", type));
    }

    public boolean hasPersistentEntityFor(Class<?> var1);

    @Nullable
    public E getPersistentEntity(TypeInformation<?> var1);

    default public E getRequiredPersistentEntity(TypeInformation<?> type) throws MappingException {
        E entity = this.getPersistentEntity((P)type);
        if (entity != null) {
            return entity;
        }
        throw new MappingException(String.format("Couldn't find PersistentEntity for type %s!", type));
    }

    @Nullable
    public E getPersistentEntity(P var1);

    default public E getRequiredPersistentEntity(P persistentProperty) throws MappingException {
        E entity = this.getPersistentEntity(persistentProperty);
        if (entity != null) {
            return entity;
        }
        throw new MappingException(String.format("Couldn't find PersistentEntity for property %s!", persistentProperty));
    }

    public PersistentPropertyPath<P> getPersistentPropertyPath(PropertyPath var1) throws InvalidPersistentPropertyPath;

    public PersistentPropertyPath<P> getPersistentPropertyPath(String var1, Class<?> var2) throws InvalidPersistentPropertyPath;

    public <T> PersistentPropertyPaths<T, P> findPersistentPropertyPaths(Class<T> var1, Predicate<? super P> var2);

    public Collection<TypeInformation<?>> getManagedTypes();
}

