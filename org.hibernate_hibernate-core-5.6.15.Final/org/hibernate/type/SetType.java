/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import org.hibernate.collection.internal.PersistentSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.TypeFactory;

public class SetType
extends CollectionType {
    @Deprecated
    public SetType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public SetType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
        return new PersistentSet(session);
    }

    @Override
    public Class getReturnedClass() {
        return Set.class;
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentSet(session, (Set)collection);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize <= 0 ? new HashSet() : new HashSet(anticipatedSize + (int)((float)anticipatedSize * 0.75f), 0.75f);
    }
}

