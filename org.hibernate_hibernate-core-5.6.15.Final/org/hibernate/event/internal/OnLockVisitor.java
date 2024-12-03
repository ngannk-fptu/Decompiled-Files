/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.internal.ReattachVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;

public class OnLockVisitor
extends ReattachVisitor {
    public OnLockVisitor(EventSource session, Serializable key, Object owner) {
        super(session, key, owner);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection == null) {
            return null;
        }
        EventSource session = this.getSession();
        CollectionPersister persister = session.getFactory().getCollectionPersister(type.getRole());
        if (!(collection instanceof PersistentCollection)) throw new HibernateException("re-associated object has dirty collection reference (or an array)");
        PersistentCollection persistentCollection = (PersistentCollection)collection;
        if (!persistentCollection.setCurrentSession(session)) throw new HibernateException("re-associated object has dirty collection reference");
        if (!OnLockVisitor.isOwnerUnchanged(persistentCollection, persister, this.extractCollectionKeyFromOwner(persister))) throw new HibernateException("re-associated object has dirty collection reference");
        if (persistentCollection.isDirty()) {
            throw new HibernateException("re-associated object has dirty collection");
        }
        this.reattachCollection(persistentCollection, type);
        return null;
    }
}

