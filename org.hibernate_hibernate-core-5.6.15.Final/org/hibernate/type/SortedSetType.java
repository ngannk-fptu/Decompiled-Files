/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;
import org.hibernate.collection.internal.PersistentSortedSet;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.SetType;
import org.hibernate.type.TypeFactory;

public class SortedSetType
extends SetType {
    private final Comparator comparator;

    @Deprecated
    public SortedSetType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
        this(role, propertyRef, comparator);
    }

    public SortedSetType(String role, String propertyRef, Comparator comparator) {
        super(role, propertyRef);
        this.comparator = comparator;
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
        PersistentSortedSet set = new PersistentSortedSet(session);
        set.setComparator(this.comparator);
        return set;
    }

    @Override
    public Class getReturnedClass() {
        return SortedSet.class;
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return new TreeSet(this.comparator);
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentSortedSet(session, (SortedSet)collection);
    }
}

