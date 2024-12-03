/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Type$PersistenceType
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import javax.persistence.metamodel.Type;
import org.hibernate.metamodel.model.domain.spi.BasicTypeDescriptor;

public class BasicTypeImpl<J>
implements BasicTypeDescriptor<J>,
Serializable {
    private final Class<J> clazz;
    private Type.PersistenceType persistenceType;

    @Override
    public Type.PersistenceType getPersistenceType() {
        return this.persistenceType;
    }

    public Class<J> getJavaType() {
        return this.clazz;
    }

    public BasicTypeImpl(Class<J> clazz, Type.PersistenceType persistenceType) {
        this.clazz = clazz;
        this.persistenceType = persistenceType;
    }

    @Override
    public String getTypeName() {
        return this.clazz.getName();
    }
}

