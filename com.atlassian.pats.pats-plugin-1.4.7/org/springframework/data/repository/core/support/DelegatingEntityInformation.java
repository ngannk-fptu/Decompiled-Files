/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.lang.Nullable;

public class DelegatingEntityInformation<T, ID>
implements EntityInformation<T, ID> {
    private final EntityInformation<T, ID> delegate;

    public DelegatingEntityInformation(EntityInformation<T, ID> delegate) {
        this.delegate = delegate;
    }

    @Override
    public Class<T> getJavaType() {
        return this.delegate.getJavaType();
    }

    @Override
    public boolean isNew(T entity) {
        return this.delegate.isNew(entity);
    }

    @Override
    @Nullable
    public ID getId(T entity) {
        return this.delegate.getId(entity);
    }

    @Override
    public Class<ID> getIdType() {
        return this.delegate.getIdType();
    }
}

