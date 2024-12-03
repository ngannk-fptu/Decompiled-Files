/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.SingularAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import javax.persistence.metamodel.SingularAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public interface SingularPersistentAttribute<D, J>
extends SingularAttribute<D, J>,
PersistentAttributeDescriptor<D, J> {
    public SimpleTypeDescriptor<J> getType();

    @Override
    public ManagedTypeDescriptor<D> getDeclaringType();

    @Override
    default public SimpleTypeDescriptor<?> getValueGraphType() {
        return this.getType();
    }

    default public Class<J> getJavaType() {
        return this.getType().getJavaType();
    }
}

