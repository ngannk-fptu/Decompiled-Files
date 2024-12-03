/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.collection.internal.PersistentList;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.TypeFactory;

public class ListType
extends CollectionType {
    @Deprecated
    public ListType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public ListType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
        return new PersistentList(session);
    }

    @Override
    public Class getReturnedClass() {
        return List.class;
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentList(session, (List)collection);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize <= 0 ? new ArrayList() : new ArrayList(anticipatedSize + 1);
    }

    @Override
    public Object indexOf(Object collection, Object element) {
        List list = (List)collection;
        for (int i = 0; i < list.size(); ++i) {
            if (list.get(i) != element) continue;
            return i;
        }
        return null;
    }
}

