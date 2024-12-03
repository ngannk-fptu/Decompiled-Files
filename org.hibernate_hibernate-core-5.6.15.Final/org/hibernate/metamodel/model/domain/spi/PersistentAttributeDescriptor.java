/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain.spi;

import org.hibernate.metamodel.model.domain.PersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public interface PersistentAttributeDescriptor<D, J>
extends PersistentAttribute<D, J> {
    @Override
    public ManagedTypeDescriptor<D> getDeclaringType();

    @Override
    public SimpleTypeDescriptor<?> getValueGraphType();

    @Override
    public SimpleTypeDescriptor<?> getKeyGraphType();
}

