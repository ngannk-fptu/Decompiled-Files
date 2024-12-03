/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.util.Assert
 */
package org.springframework.data.repository.core.support;

import org.springframework.data.repository.core.EntityInformation;
import org.springframework.util.Assert;

public abstract class AbstractEntityInformation<T, ID>
implements EntityInformation<T, ID> {
    private final Class<T> domainClass;

    public AbstractEntityInformation(Class<T> domainClass) {
        Assert.notNull(domainClass, (String)"Domain class must not be null");
        this.domainClass = domainClass;
    }

    @Override
    public boolean isNew(T entity) {
        Object id = this.getId(entity);
        Class idType = this.getIdType();
        if (!idType.isPrimitive()) {
            return id == null;
        }
        if (id instanceof Number) {
            return ((Number)id).longValue() == 0L;
        }
        throw new IllegalArgumentException(String.format("Unsupported primitive id type %s!", idType));
    }

    @Override
    public Class<T> getJavaType() {
        return this.domainClass;
    }
}

