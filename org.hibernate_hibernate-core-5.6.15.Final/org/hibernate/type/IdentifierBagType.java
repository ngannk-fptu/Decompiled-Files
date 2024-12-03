/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentIdentifierBag;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.TypeFactory;

public class IdentifierBagType
extends CollectionType {
    @Deprecated
    public IdentifierBagType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public IdentifierBagType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) throws HibernateException {
        return new PersistentIdentifierBag(session);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize <= 0 ? new ArrayList() : new ArrayList(anticipatedSize + 1);
    }

    @Override
    public Class getReturnedClass() {
        return Collection.class;
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentIdentifierBag(session, (Collection)collection);
    }
}

