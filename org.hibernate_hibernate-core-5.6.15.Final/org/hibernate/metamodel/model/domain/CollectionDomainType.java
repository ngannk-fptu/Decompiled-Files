/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.metamodel.model.domain;

import org.hibernate.metamodel.model.domain.DomainType;

public interface CollectionDomainType<C, E>
extends DomainType<C> {
    public Element<E> getElementDescriptor();

    public static interface Element<E> {
        public Class<E> getJavaType();
    }
}

