/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.CollectionAction;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.spi.PostCollectionRecreateEvent;
import org.hibernate.event.spi.PostCollectionRecreateEventListener;
import org.hibernate.event.spi.PreCollectionRecreateEvent;
import org.hibernate.event.spi.PreCollectionRecreateEventListener;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.stat.spi.StatisticsImplementor;

public final class CollectionRecreateAction
extends CollectionAction {
    public CollectionRecreateAction(PersistentCollection collection, CollectionPersister persister, Serializable id, SharedSessionContractImplementor session) {
        super(persister, collection, id, session);
    }

    @Override
    public void execute() throws HibernateException {
        PersistentCollection collection = this.getCollection();
        this.preRecreate();
        SharedSessionContractImplementor session = this.getSession();
        this.getPersister().recreate(collection, this.getKey(), session);
        session.getPersistenceContextInternal().getCollectionEntry(collection).afterAction(collection);
        this.evict();
        this.postRecreate();
        StatisticsImplementor statistics = session.getFactory().getStatistics();
        if (statistics.isStatisticsEnabled()) {
            statistics.recreateCollection(this.getPersister().getRole());
        }
    }

    private void preRecreate() {
        this.getFastSessionServices().eventListenerGroup_PRE_COLLECTION_RECREATE.fireLazyEventOnEachListener(this::newPreCollectionRecreateEvent, PreCollectionRecreateEventListener::onPreRecreateCollection);
    }

    private PreCollectionRecreateEvent newPreCollectionRecreateEvent() {
        return new PreCollectionRecreateEvent(this.getPersister(), this.getCollection(), this.eventSource());
    }

    private void postRecreate() {
        this.getFastSessionServices().eventListenerGroup_POST_COLLECTION_RECREATE.fireLazyEventOnEachListener(this::newPostCollectionRecreateEvent, PostCollectionRecreateEventListener::onPostRecreateCollection);
    }

    private PostCollectionRecreateEvent newPostCollectionRecreateEvent() {
        return new PostCollectionRecreateEvent(this.getPersister(), this.getCollection(), this.eventSource());
    }
}

