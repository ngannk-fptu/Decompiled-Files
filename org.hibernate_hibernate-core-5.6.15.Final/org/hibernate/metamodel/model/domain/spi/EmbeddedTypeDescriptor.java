/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain.spi;

import org.hibernate.metamodel.model.domain.EmbeddedDomainType;
import org.hibernate.metamodel.model.domain.spi.ManagedTypeDescriptor;
import org.hibernate.type.CompositeType;

public interface EmbeddedTypeDescriptor<J>
extends EmbeddedDomainType<J>,
ManagedTypeDescriptor<J> {
    public CompositeType getHibernateType();

    public ManagedTypeDescriptor<?> getParent();
}

