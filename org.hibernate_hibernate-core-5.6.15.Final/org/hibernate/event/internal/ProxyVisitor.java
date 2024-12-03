/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.event.internal.AbstractVisitor;
import org.hibernate.event.spi.EventSource;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.type.CollectionType;
import org.hibernate.type.EntityType;

public abstract class ProxyVisitor
extends AbstractVisitor {
    public ProxyVisitor(EventSource session) {
        super(session);
    }

    @Override
    Object processEntity(Object value, EntityType entityType) throws HibernateException {
        if (value != null) {
            this.getSession().getPersistenceContext().reassociateIfUninitializedProxy(value);
        }
        return null;
    }

    protected static boolean isOwnerUnchanged(PersistentCollection snapshot, CollectionPersister persister, Serializable id) {
        return ProxyVisitor.isCollectionSnapshotValid(snapshot) && persister.getRole().equals(snapshot.getRole()) && id.equals(snapshot.getKey());
    }

    private static boolean isCollectionSnapshotValid(PersistentCollection snapshot) {
        return snapshot != null && snapshot.getRole() != null && snapshot.getKey() != null;
    }

    protected void reattachCollection(PersistentCollection collection, CollectionType type) throws HibernateException {
        EventSource session = this.getSession();
        if (collection.wasInitialized()) {
            CollectionPersister collectionPersister = session.getFactory().getCollectionPersister(type.getRole());
            session.getPersistenceContext().addInitializedDetachedCollection(collectionPersister, collection);
        } else {
            if (!ProxyVisitor.isCollectionSnapshotValid(collection)) {
                throw new HibernateException("could not re-associate uninitialized transient collection");
            }
            CollectionPersister collectionPersister = session.getFactory().getCollectionPersister(collection.getRole());
            session.getPersistenceContext().addUninitializedDetachedCollection(collectionPersister, collection);
        }
    }
}

