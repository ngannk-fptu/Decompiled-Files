/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import org.hibernate.HibernateException;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.Collections;
import org.hibernate.event.internal.AbstractVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.type.CollectionType;

public class FlushVisitor
extends AbstractVisitor {
    private Object owner;

    public FlushVisitor(EventSource session, Object owner) {
        super(session);
        this.owner = owner;
    }

    @Override
    Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection == CollectionType.UNFETCHED_COLLECTION) {
            return null;
        }
        if (collection != null) {
            PersistentCollection coll;
            EventSource session = this.getSession();
            if (type.hasHolder()) {
                coll = session.getPersistenceContextInternal().getCollectionHolder(collection);
            } else if (collection == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
                coll = (PersistentCollection)type.resolve(collection, session, this.owner);
            } else if (collection instanceof PersistentCollection) {
                coll = (PersistentCollection)collection;
            } else {
                return null;
            }
            Collections.processReachableCollection(coll, type, this.owner, session);
        }
        return null;
    }

    @Override
    boolean includeEntityProperty(Object[] values, int i) {
        return true;
    }
}

