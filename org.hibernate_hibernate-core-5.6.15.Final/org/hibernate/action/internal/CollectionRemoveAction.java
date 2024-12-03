/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.CollectionAction;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PostCollectionRemoveEvent;
import org.hibernate.event.spi.PostCollectionRemoveEventListener;
import org.hibernate.event.spi.PreCollectionRemoveEvent;
import org.hibernate.event.spi.PreCollectionRemoveEventListener;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.stat.spi.StatisticsImplementor;

public final class CollectionRemoveAction
extends CollectionAction {
    private final Object affectedOwner;
    private boolean emptySnapshot;

    public CollectionRemoveAction(PersistentCollection collection, CollectionPersister persister, Serializable id, boolean emptySnapshot, SharedSessionContractImplementor session) {
        super(persister, collection, id, session);
        if (collection == null) {
            throw new AssertionFailure("collection == null");
        }
        this.emptySnapshot = emptySnapshot;
        this.affectedOwner = session.getPersistenceContextInternal().getLoadedCollectionOwnerOrNull(collection);
    }

    public CollectionRemoveAction(Object affectedOwner, CollectionPersister persister, Serializable id, boolean emptySnapshot, SharedSessionContractImplementor session) {
        super(persister, null, id, session);
        if (affectedOwner == null) {
            throw new AssertionFailure("affectedOwner == null");
        }
        this.emptySnapshot = emptySnapshot;
        this.affectedOwner = affectedOwner;
    }

    @Override
    public void execute() throws HibernateException {
        PersistentCollection collection;
        this.preRemove();
        SharedSessionContractImplementor session = this.getSession();
        if (!this.emptySnapshot) {
            this.getPersister().remove(this.getKey(), session);
        }
        if ((collection = this.getCollection()) != null) {
            session.getPersistenceContextInternal().getCollectionEntry(collection).afterAction(collection);
        }
        this.evict();
        this.postRemove();
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.removeCollection(this.getPersister().getRole());
        }
    }

    private void preRemove() {
        this.getFastSessionServices().eventListenerGroup_PRE_COLLECTION_REMOVE.fireLazyEventOnEachListener(this::newPreCollectionRemoveEvent, PreCollectionRemoveEventListener::onPreRemoveCollection);
    }

    private PreCollectionRemoveEvent newPreCollectionRemoveEvent() {
        return new PreCollectionRemoveEvent(this.getPersister(), this.getCollection(), this.eventSource(), this.affectedOwner);
    }

    private void postRemove() {
        this.getFastSessionServices().eventListenerGroup_POST_COLLECTION_REMOVE.fireLazyEventOnEachListener(this::newPostCollectionRemoveEvent, PostCollectionRemoveEventListener::onPostRemoveCollection);
    }

    private PostCollectionRemoveEvent newPostCollectionRemoveEvent() {
        return new PostCollectionRemoveEvent(this.getPersister(), this.getCollection(), this.eventSource(), this.affectedOwner);
    }
}

