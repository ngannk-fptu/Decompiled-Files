/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.mapping.PersistentProperty;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.lang.Nullable;

public class PersistentEntityInformation<T, ID>
implements EntityInformation<T, ID> {
    private final PersistentEntity<T, ? extends PersistentProperty<?>> persistentEntity;

    public PersistentEntityInformation(PersistentEntity<T, ? extends PersistentProperty<?>> persistentEntity) {
        this.persistentEntity = persistentEntity;
    }

    @Override
    public boolean isNew(T entity) {
        return this.persistentEntity.isNew(entity);
    }

    @Override
    @Nullable
    public ID getId(T entity) {
        return (ID)this.persistentEntity.getIdentifierAccessor(entity).getIdentifier();
    }

    @Override
    public Class<T> getJavaType() {
        return this.persistentEntity.getType();
    }

    @Override
    public Class<ID> getIdType() {
        return this.persistentEntity.getRequiredIdProperty().getType();
    }
}

