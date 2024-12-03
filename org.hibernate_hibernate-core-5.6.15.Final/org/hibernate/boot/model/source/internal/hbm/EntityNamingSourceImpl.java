/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.model.source.internal.hbm;

import org.hibernate.boot.model.source.spi.EntityNamingSource;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.mapping.PersistentClass;

class EntityNamingSourceImpl
implements EntityNamingSource {
    private final String entityName;
    private final String className;
    private final String jpaEntityName;
    private final String typeName;

    public EntityNamingSourceImpl(String entityName, String className, String jpaEntityName) {
        this.entityName = entityName;
        this.className = className;
        this.jpaEntityName = jpaEntityName;
        this.typeName = StringHelper.isNotEmpty(className) ? className : entityName;
    }

    public EntityNamingSourceImpl(PersistentClass entityBinding) {
        this(entityBinding.getEntityName(), entityBinding.getClassName(), entityBinding.getJpaEntityName());
    }

    @Override
    public String getEntityName() {
        return this.entityName;
    }

    @Override
    public String getClassName() {
        return this.className;
    }

    @Override
    public String getJpaEntityName() {
        return this.jpaEntityName;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }
}

