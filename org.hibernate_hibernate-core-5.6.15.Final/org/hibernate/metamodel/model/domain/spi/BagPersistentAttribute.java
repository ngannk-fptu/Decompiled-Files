/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.CollectionAttribute
 */
package org.hibernate.metamodel.model.domain.spi;

import java.util.Collection;
import javax.persistence.metamodel.CollectionAttribute;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.metamodel.model.domain.spi.PluralPersistentAttribute;
import org.hibernate.metamodel.model.domain.spi.SimpleTypeDescriptor;

public interface BagPersistentAttribute<D, E>
extends CollectionAttribute<D, E>,
PluralPersistentAttribute<D, Collection<E>, E> {
    @Override
    public SimpleTypeDescriptor<E> getValueGraphType();

    @Override
    public SimpleTypeDescriptor<E> getElementType();

    @Override
    public ManagedTypeDescriptor<D> getDeclaringType();
}

