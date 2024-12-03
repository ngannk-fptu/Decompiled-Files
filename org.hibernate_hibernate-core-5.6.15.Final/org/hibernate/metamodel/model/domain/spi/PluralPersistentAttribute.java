/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.PluralAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import javax.persistence.metamodel.PluralAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PersistentAttributeDescriptor;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public interface PluralPersistentAttribute<D, C, E>
extends PluralAttribute<D, C, E>,
PersistentAttributeDescriptor<D, C> {
    @Override
    public ManagedTypeDescriptor<D> getDeclaringType();

    public SimpleTypeDescriptor<E> getElementType();

    @Override
    public SimpleTypeDescriptor<E> getValueGraphType();
}

