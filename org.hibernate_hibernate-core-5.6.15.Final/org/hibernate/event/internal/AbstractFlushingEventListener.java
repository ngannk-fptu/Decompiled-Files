/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.event.internal;

import java.io.Serializable;
import java.util.IdentityHashMap;
import java.util.Map;
import org.hibernate.HibernateException;
import org.hibernate.Interceptor;
import org.hibernate.action.internal.CollectionRecreateAction;
import org.hibernate.action.internal.CollectionRemoveAction;
import org.hibernate.action.internal.CollectionUpdateAction;
import org.hibernate.action.internal.QueuedOperationCollectionAction;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.Cascade;
import org.hibernate.engine.internal.CascadePoint;
import org.hibernate.engine.internal.Collections;
import org.hibernate.engine.jdbc.spi.JdbcCoordinator;
import org.hibernate.engine.spi.ActionQueue;
import org.hibernate.engine.spi.CascadingAction;
import org.hibernate.engine.spi.CascadingActions;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.JpaBootstrapSensitive;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.FlushEntityEvent;
import org.hibernate.event.spi.FlushEntityEventListener;
import org.hibernate.event.spi.FlushEvent;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.EntityPrinter;
import org.hibernate.persister.entity.EntityPersister;
import org.jboss.logging.Logger;

public abstract class AbstractFlushingEventListener
implements JpaBootstrapSensitive,
Serializable {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)AbstractFlushingEventListener.class.getName());
    private boolean jpaBootstrap;

    @Override
    public void wasJpaBootstrap(boolean wasJpaBootstrap) {
        this.jpaBootstrap = wasJpaBootstrap;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void flushEverythingToExecutions(FlushEvent event) throws HibernateException {
        LOG.trace("Flushing session");
        EventSource session = event.getSession();
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        session.getInterceptor().preFlush(persistenceContext.managedEntitiesIterator());
        this.prepareEntityFlushes(session, persistenceContext);
        this.prepareCollectionFlushes(persistenceContext);
        persistenceContext.setFlushing(true);
        try {
            int entityCount = this.flushEntities(event, persistenceContext);
            int collectionCount = this.flushCollections(session, persistenceContext);
            event.setNumberOfEntitiesProcessed(entityCount);
            event.setNumberOfCollectionsProcessed(collectionCount);
        }
        finally {
            persistenceContext.setFlushing(false);
        }
        this.logFlushResults(event);
    }

    protected void logFlushResults(FlushEvent event) {
        if (!LOG.isDebugEnabled()) {
            return;
        }
        EventSource session = event.getSession();
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        LOG.debugf("Flushed: %s insertions, %s updates, %s deletions to %s objects", new Object[]{session.getActionQueue().numberOfInsertions(), session.getActionQueue().numberOfUpdates(), session.getActionQueue().numberOfDeletions(), persistenceContext.getNumberOfManagedEntities()});
        LOG.debugf("Flushed: %s (re)creations, %s updates, %s removals to %s collections", new Object[]{session.getActionQueue().numberOfCollectionCreations(), session.getActionQueue().numberOfCollectionUpdates(), session.getActionQueue().numberOfCollectionRemovals(), persistenceContext.getCollectionEntriesSize()});
        new EntityPrinter(session.getFactory()).toString(persistenceContext.getEntitiesByKey().entrySet());
    }

    private void prepareEntityFlushes(EventSource session, PersistenceContext persistenceContext) throws HibernateException {
        LOG.debug("Processing flush-time cascades");
        Object anything = this.getAnything();
        for (Map.Entry<Object, EntityEntry> me : persistenceContext.reentrantSafeEntityEntries()) {
            EntityEntry entry = me.getValue();
            Status status = entry.getStatus();
            if (status != Status.MANAGED && status != Status.SAVING && status != Status.READ_ONLY) continue;
            this.cascadeOnFlush(session, entry.getPersister(), me.getKey(), anything);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void cascadeOnFlush(EventSource session, EntityPersister persister, Object object, Object anything) throws HibernateException {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        persistenceContext.incrementCascadeLevel();
        try {
            Cascade.cascade(this.getCascadingAction(), CascadePoint.BEFORE_FLUSH, session, persister, object, anything);
        }
        finally {
            persistenceContext.decrementCascadeLevel();
        }
    }

    protected Object getAnything() {
        if (this.jpaBootstrap) {
            return new IdentityHashMap(10);
        }
        return null;
    }

    protected CascadingAction getCascadingAction() {
        if (this.jpaBootstrap) {
            return CascadingActions.PERSIST_ON_FLUSH;
        }
        return CascadingActions.SAVE_UPDATE;
    }

    private void prepareCollectionFlushes(PersistenceContext persistenceContext) throws HibernateException {
        LOG.debug("Dirty checking collections");
        persistenceContext.forEachCollectionEntry((pc, ce) -> ce.preFlush((PersistentCollection)pc), true);
    }

    private int flushEntities(FlushEvent event, PersistenceContext persistenceContext) throws HibernateException {
        LOG.trace("Flushing entities and processing referenced collections");
        EventSource source = event.getSession();
        EventListenerGroup<FlushEntityEventListener> flushListeners = source.getFactory().getFastSessionServices().eventListenerGroup_FLUSH_ENTITY;
        Map.Entry<Object, EntityEntry>[] entityEntries = persistenceContext.reentrantSafeEntityEntries();
        int count = entityEntries.length;
        for (Map.Entry<Object, EntityEntry> me : entityEntries) {
            EntityEntry entry = me.getValue();
            Status status = entry.getStatus();
            if (status == Status.LOADING || status == Status.GONE) continue;
            FlushEntityEvent entityEvent = new FlushEntityEvent(source, me.getKey(), entry);
            flushListeners.fireEventOnEachListener(entityEvent, FlushEntityEventListener::onFlushEntity);
        }
        source.getActionQueue().sortActions();
        return count;
    }

    private int flushCollections(EventSource session, PersistenceContext persistenceContext) throws HibernateException {
        LOG.trace("Processing unreferenced collections");
        int count = persistenceContext.getCollectionEntriesSize();
        persistenceContext.forEachCollectionEntry((persistentCollection, collectionEntry) -> {
            if (!collectionEntry.isReached() && !collectionEntry.isIgnore()) {
                Collections.processUnreachableCollection(persistentCollection, session);
            }
        }, true);
        LOG.trace("Scheduling collection removes/(re)creates/updates");
        ActionQueue actionQueue = session.getActionQueue();
        Interceptor interceptor = session.getInterceptor();
        persistenceContext.forEachCollectionEntry((coll, ce) -> {
            if (ce.isDorecreate()) {
                interceptor.onCollectionRecreate(coll, ce.getCurrentKey());
                actionQueue.addAction(new CollectionRecreateAction((PersistentCollection)coll, ce.getCurrentPersister(), ce.getCurrentKey(), (SharedSessionContractImplementor)session));
            }
            if (ce.isDoremove()) {
                interceptor.onCollectionRemove(coll, ce.getLoadedKey());
                actionQueue.addAction(new CollectionRemoveAction((PersistentCollection)coll, ce.getLoadedPersister(), ce.getLoadedKey(), ce.isSnapshotEmpty((PersistentCollection)coll), (SharedSessionContractImplementor)session));
            }
            if (ce.isDoupdate()) {
                interceptor.onCollectionUpdate(coll, ce.getLoadedKey());
                actionQueue.addAction(new CollectionUpdateAction((PersistentCollection)coll, ce.getLoadedPersister(), ce.getLoadedKey(), ce.isSnapshotEmpty((PersistentCollection)coll), session));
            }
            if (!coll.wasInitialized() && coll.hasQueuedOperations()) {
                actionQueue.addAction(new QueuedOperationCollectionAction((PersistentCollection)coll, ce.getLoadedPersister(), ce.getLoadedKey(), (SharedSessionContractImplementor)session));
            }
        }, true);
        actionQueue.sortCollectionActions();
        return count;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void performExecutions(EventSource session) {
        LOG.trace("Executing flush");
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        JdbcCoordinator jdbcCoordinator = session.getJdbcCoordinator();
        try {
            jdbcCoordinator.flushBeginning();
            persistenceContext.setFlushing(true);
            ActionQueue actionQueue = session.getActionQueue();
            actionQueue.prepareActions();
            actionQueue.executeActions();
        }
        finally {
            persistenceContext.setFlushing(false);
            jdbcCoordinator.flushEnding();
        }
    }

    protected void postFlush(SessionImplementor session) throws HibernateException {
        LOG.trace("Post flush");
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        persistenceContext.clearCollectionsByKey();
        persistenceContext.getBatchFetchQueue().clear();
        persistenceContext.forEachCollectionEntry((persistentCollection, collectionEntry) -> {
            collectionEntry.postFlush((PersistentCollection)persistentCollection);
            if (collectionEntry.getLoadedPersister() == null) {
                persistentCollection.unsetSession(session);
                persistenceContext.removeCollectionEntry((PersistentCollection)persistentCollection);
            } else {
                CollectionKey collectionKey = new CollectionKey(collectionEntry.getLoadedPersister(), collectionEntry.getLoadedKey());
                persistenceContext.addCollectionByKey(collectionKey, (PersistentCollection)persistentCollection);
            }
        }, true);
    }

    protected void postPostFlush(SessionImplementor session) {
        session.getInterceptor().postFlush(session.getPersistenceContextInternal().managedEntitiesIterator());
    }
}

