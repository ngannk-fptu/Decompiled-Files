/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain.internal;

import java.io.Serializable;
import org.hibernate.metamodel.model.domain.spi.DomainTypeDescriptor;

public abstract class AbstractType<X>
implements DomainTypeDescriptor<X>,
Serializable {
    private final Class<X> javaType;
    private final String typeName;

    protected AbstractType(Class<X> javaType) {
        this(javaType, javaType != null ? javaType.getName() : null);
    }

    protected AbstractType(Class<X> javaType, String typeName) {
        this.javaType = javaType;
        this.typeName = typeName == null ? "unknown" : typeName;
    }

    public Class<X> getJavaType() {
        return this.javaType;
    }

    @Override
    public String getTypeName() {
        return this.typeName;
    }
}

