/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.type;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.collection.internal.PersistentMap;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.TypeFactory;

public class MapType
extends CollectionType {
    @Deprecated
    public MapType(TypeFactory.TypeScope typeScope, String role, String propertyRef) {
        this(role, propertyRef);
    }

    public MapType(String role, String propertyRef) {
        super(role, propertyRef);
    }

    @Override
    public PersistentCollection instantiate(SharedSessionContractImplementor session, CollectionPersister persister, Serializable key) {
        return new PersistentMap(session);
    }

    @Override
    public Class getReturnedClass() {
        return Map.class;
    }

    @Override
    public Iterator getElementsIterator(Object collection) {
        return ((Map)collection).values().iterator();
    }

    @Override
    public PersistentCollection wrap(SharedSessionContractImplementor session, Object collection) {
        return new PersistentMap(session, (Map)collection);
    }

    @Override
    public Object instantiate(int anticipatedSize) {
        return anticipatedSize <= 0 ? new HashMap() : new HashMap(anticipatedSize + (int)((float)anticipatedSize * 0.75f), 0.75f);
    }

    @Override
    public Object replaceElements(Object original, Object target, Object owner, Map copyCache, SharedSessionContractImplementor session) throws HibernateException {
        CollectionPersister cp = session.getFactory().getMetamodel().collectionPersister(this.getRole());
        Map result = (Map)target;
        result.clear();
        Iterator iterator = ((Map)original).entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry o;
            Map.Entry me = o = iterator.next();
            Object key = cp.getIndexType().replace(me.getKey(), null, session, owner, copyCache);
            Object value = cp.getElementType().replace(me.getValue(), null, session, owner, copyCache);
            result.put(key, value);
        }
        return result;
    }

    @Override
    public Object indexOf(Object collection, Object element) {
        for (Map.Entry o : ((Map)collection).entrySet()) {
            Map.Entry me = o;
            if (me.getValue() != element) continue;
            return me.getKey();
        }
        return null;
    }
}

