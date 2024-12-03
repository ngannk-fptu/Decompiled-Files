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
import org.hibernate.event.spi.PostCollectionUpdateEvent;
import org.hibernate.event.spi.PostCollectionUpdateEventListener;
import org.hibernate.event.spi.PreCollectionUpdateEvent;
import org.hibernate.event.spi.PreCollectionUpdateEventListener;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.stat.spi.StatisticsImplementor;

public final class CollectionUpdateAction
extends CollectionAction {
    private final boolean emptySnapshot;

    public CollectionUpdateAction(PersistentCollection collection, CollectionPersister persister, Serializable id, boolean emptySnapshot, SharedSessionContractImplementor session) {
        super(persister, collection, id, session);
        this.emptySnapshot = emptySnapshot;
    }

    @Override
    public void execute() throws HibernateException {
        Serializable id = this.getKey();
        SharedSessionContractImplementor session = this.getSession();
        CollectionPersister persister = this.getPersister();
        PersistentCollection collection = this.getCollection();
        boolean affectedByFilters = persister.isAffectedByEnabledFilters(session);
        this.preUpdate();
        if (!collection.wasInitialized()) {
            if (!collection.isDirty()) {
                throw new AssertionFailure("collection is not dirty");
            }
        } else if (!affectedByFilters && collection.empty()) {
            if (!this.emptySnapshot) {
                persister.remove(id, session);
            }
        } else if (collection.needsRecreate(persister)) {
            if (affectedByFilters) {
                throw new HibernateException("cannot recreate collection while filter is enabled: " + MessageHelper.collectionInfoString(persister, collection, id, session));
            }
            if (!this.emptySnapshot) {
                persister.remove(id, session);
            }
            persister.recreate(collection, id, session);
        } else {
            persister.deleteRows(collection, id, session);
            persister.updateRows(collection, id, session);
            persister.insertRows(collection, id, session);
        }
        session.getPersistenceContextInternal().getCollectionEntry(collection).afterAction(collection);
        this.evict();
        this.postUpdate();
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.updateCollection(persister.getRole());
        }
    }

    private void preUpdate() {
        this.getFastSessionServices().eventListenerGroup_PRE_COLLECTION_UPDATE.fireLazyEventOnEachListener(this::newPreCollectionUpdateEvent, PreCollectionUpdateEventListener::onPreUpdateCollection);
    }

    private PreCollectionUpdateEvent newPreCollectionUpdateEvent() {
        return new PreCollectionUpdateEvent(this.getPersister(), this.getCollection(), this.eventSource());
    }

    private void postUpdate() {
        this.getFastSessionServices().eventListenerGroup_POST_COLLECTION_UPDATE.fireLazyEventOnEachListener(this::newPostCollectionUpdateEvent, PostCollectionUpdateEventListener::onPostUpdateCollection);
    }

    private PostCollectionUpdateEvent newPostCollectionUpdateEvent() {
        return new PostCollectionUpdateEvent(this.getPersister(), this.getCollection(), this.eventSource());
    }
}

