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

public class OnUpdateVisitor
extends ReattachVisitor {
    public OnUpdateVisitor(EventSource session, Serializable key, Object owner) {
        super(session, key, owner);
    }

    @Override
    Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection == CollectionType.UNFETCHED_COLLECTION) {
            return null;
        }
        EventSource session = this.getSession();
        CollectionPersister persister = session.getFactory().getCollectionPersister(type.getRole());
        Serializable collectionKey = this.extractCollectionKeyFromOwner(persister);
        if (collection != null && collection instanceof PersistentCollection) {
            PersistentCollection wrapper = (PersistentCollection)collection;
            if (wrapper.setCurrentSession(session)) {
                if (!OnUpdateVisitor.isOwnerUnchanged(wrapper, persister, collectionKey)) {
                    this.removeCollection(persister, collectionKey, session);
                }
                this.reattachCollection(wrapper, type);
            } else {
                this.removeCollection(persister, collectionKey, session);
            }
        } else {
            this.removeCollection(persister, collectionKey, session);
        }
        return null;
    }
}

