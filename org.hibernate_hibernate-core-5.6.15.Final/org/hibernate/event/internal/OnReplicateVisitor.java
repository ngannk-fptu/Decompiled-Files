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

public class OnReplicateVisitor
extends ReattachVisitor {
    private boolean isUpdate;

    public OnReplicateVisitor(EventSource session, Serializable key, Object owner, boolean isUpdate) {
        super(session, key, owner);
        this.isUpdate = isUpdate;
    }

    @Override
    public Object processCollection(Object collection, CollectionType type) throws HibernateException {
        if (collection == CollectionType.UNFETCHED_COLLECTION) {
            return null;
        }
        EventSource session = this.getSession();
        CollectionPersister persister = session.getFactory().getMetamodel().collectionPersister(type.getRole());
        if (this.isUpdate) {
            this.removeCollection(persister, this.extractCollectionKeyFromOwner(persister), session);
        }
        if (collection != null && collection instanceof PersistentCollection) {
            PersistentCollection wrapper = (PersistentCollection)collection;
            wrapper.setCurrentSession(session);
            if (wrapper.wasInitialized()) {
                session.getPersistenceContextInternal().addNewCollection(persister, wrapper);
            } else {
                this.reattachCollection(wrapper, type);
            }
        }
        return null;
    }
}

