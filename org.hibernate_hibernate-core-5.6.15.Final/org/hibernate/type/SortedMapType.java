/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;
import org.hibernate.collection.internal.PersistentSortedMap;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.MapType;
import org.hibernate.type.TypeFactory;

public class SortedMapType
extends MapType {
    private final Comparator comparator;

    @Deprecated
    public SortedMapType(TypeFactory.TypeScope typeScope, String role, String propertyRef, Comparator comparator) {
        this(role, propertyRef, comparator);
    }

    public SortedMapType(String role, String propertyRef, Comparator comparator) {
        super(role, propertyRef);
        this.comparator = comparator;
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
        PersistentSortedMap map = new PersistentSortedMap(session);
        map.setComparator(this.comparator);
        return map;
    }

    @Override
    public Class getReturnedClass() {
        return SortedMap.class;
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return new TreeMap(this.comparator);
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentSortedMap(session, (SortedMap)collection);
    }
}

