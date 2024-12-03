/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.mapping;

import org.hibernate.MappingException;
import org.hibernate.mapping.Property;
import org.hibernate.property.access.internal.PropertyAccessStrategyIndexBackRefImpl;
import org.hibernate.property.access.spi.PropertyAccessStrategy;

public class IndexBackref
extends Property {
    private String collectionRole;
    private String entityName;
    private PropertyAccessStrategy accessStrategy;

    @Override
    public boolean isBackRef() {
        return true;
    }

    @Override
    public boolean isSynthetic() {
        return true;
    }

    public String getCollectionRole() {
        return this.collectionRole;
    }

    public void setCollectionRole(String collectionRole) {
        this.collectionRole = collectionRole;
    }

    @Override
    public boolean isBasicPropertyAccessor() {
        return false;
    }

    @Override
    public PropertyAccessStrategy getPropertyAccessStrategy(Class clazz) throws MappingException {
        if (this.accessStrategy == null) {
            this.accessStrategy = new PropertyAccessStrategyIndexBackRefImpl(this.collectionRole, this.entityName);
        }
        return this.accessStrategy;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }
}

