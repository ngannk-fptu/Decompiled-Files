/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.action.internal.DelayedPostInsertIdentifier;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.action.spi.BeforeTransactionCompletionProcess;
import org.hibernate.action.spi.Executable;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.FastSessionServices;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.pretty.MessageHelper;

public abstract class CollectionAction
implements Executable,
Serializable,
Comparable {
    private transient CollectionPersister persister;
    private transient SharedSessionContractImplementor session;
    private final PersistentCollection collection;
    private final Serializable key;
    private final String collectionRole;
    private AfterTransactionCompletionProcess afterTransactionProcess;

    protected CollectionAction(CollectionPersister persister, PersistentCollection collection, Serializable key, SharedSessionContractImplementor session) {
        this.persister = persister;
        this.session = session;
        this.key = key;
        this.collectionRole = persister.getRole();
        this.collection = collection;
    }

    protected PersistentCollection getCollection() {
        return this.collection;
    }

    @Override
    public void afterDeserialize(SharedSessionContractImplementor session) {
        if (this.session != null || this.persister != null) {
            throw new IllegalStateException("already attached to a session.");
        }
        if (session != null) {
            this.session = session;
            this.persister = session.getFactory().getMetamodel().collectionPersister(this.collectionRole);
        }
    }

    @Override
    public final void beforeExecutions() throws CacheException {
        if (this.persister.hasCache()) {
            CollectionDataAccess cache = this.persister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(this.key, this.persister, this.session.getFactory(), this.session.getTenantIdentifier());
            SoftLock lock = cache.lockItem(this.session, ck, null);
            this.afterTransactionProcess = new CacheCleanupProcess(this.key, this.persister, lock);
        }
    }

    @Override
    public BeforeTransactionCompletionProcess getBeforeTransactionCompletionProcess() {
        return null;
    }

    @Override
    public AfterTransactionCompletionProcess getAfterTransactionCompletionProcess() {
        return this.afterTransactionProcess;
    }

    @Override
    public Serializable[] getPropertySpaces() {
        return this.persister.getCollectionSpaces();
    }

    protected final CollectionPersister getPersister() {
        return this.persister;
    }

    protected final Serializable getKey() {
        Serializable finalKey = this.key;
        if (!(this.key instanceof DelayedPostInsertIdentifier) || (finalKey = this.session.getPersistenceContextInternal().getEntry(this.collection.getOwner()).getId()) == this.key) {
            // empty if block
        }
        return finalKey;
    }

    protected final SharedSessionContractImplementor getSession() {
        return this.session;
    }

    protected final void evict() throws CacheException {
        if (this.persister.hasCache()) {
            CollectionDataAccess cache = this.persister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(this.key, this.persister, this.session.getFactory(), this.session.getTenantIdentifier());
            cache.remove(this.session, ck);
        }
    }

    public String toString() {
        return StringHelper.unqualify(this.getClass().getName()) + MessageHelper.infoString(this.collectionRole, this.key);
    }

    public int compareTo(Object other) {
        CollectionAction action = (CollectionAction)other;
        int roleComparison = this.collectionRole.compareTo(action.collectionRole);
        if (roleComparison != 0) {
            return roleComparison;
        }
        return this.persister.getKeyType().compare(this.key, action.key);
    }

    @Deprecated
    protected <T> EventListenerGroup<T> listenerGroup(EventType<T> eventType) {
        return this.getSession().getFactory().getServiceRegistry().getService(EventListenerRegistry.class).getEventListenerGroup(eventType);
    }

    protected EventSource eventSource() {
        return (EventSource)this.getSession();
    }

    protected FastSessionServices getFastSessionServices() {
        return this.session.getFactory().getFastSessionServices();
    }

    private static class CacheCleanupProcess
    implements AfterTransactionCompletionProcess {
        private final Serializable key;
        private final CollectionPersister persister;
        private final SoftLock lock;

        private CacheCleanupProcess(Serializable key, CollectionPersister persister, SoftLock lock) {
            this.key = key;
            this.persister = persister;
            this.lock = lock;
        }

        @Override
        public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
            CollectionDataAccess cache = this.persister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(this.key, this.persister, session.getFactory(), session.getTenantIdentifier());
            cache.unlockItem(session, ck, this.lock);
        }
    }
}

