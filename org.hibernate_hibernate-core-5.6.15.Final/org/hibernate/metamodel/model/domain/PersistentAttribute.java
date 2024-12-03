/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.metamodel.Attribute
 */
package org.hibernate.metamodel.model.domain;

import javax.persistence.metamodel.Attribute;
import org.hibernate.metamodel.model.domain.ManagedDomainType;
import org.hibernate.metamodel.model.domain.SimpleDomainType;

public interface PersistentAttribute<D, J>
extends Attribute<D, J> {
    public ManagedDomainType<D> getDeclaringType();

    public SimpleDomainType<?> getValueGraphType();

    public SimpleDomainType<?> getKeyGraphType();
}

