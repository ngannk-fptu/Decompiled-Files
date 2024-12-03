/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core;

import org.springframework.data.repository.core.EntityMetadata;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public interface EntityInformation<T, ID>
extends EntityMetadata<T> {
    public boolean isNew(T var1);

    @Nullable
    public ID getId(T var1);

    default public ID getRequiredId(T entity) throws IllegalArgumentException {
        Assert.notNull(entity, (String)"Entity must not be null!");
        ID id = this.getId(entity);
        if (id != null) {
            return id;
        }
        throw new IllegalArgumentException(String.format("Could not obtain required identifier from entity %s!", entity));
    }

    public Class<ID> getIdType();
}

