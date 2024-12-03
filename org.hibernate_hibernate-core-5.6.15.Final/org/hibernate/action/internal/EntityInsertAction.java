/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.AbstractEntityInsertAction;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.PostCommitInsertEventListener;
import org.hibernate.event.spi.PostInsertEvent;
import org.hibernate.event.spi.PostInsertEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;

public class EntityInsertAction
extends AbstractEntityInsertAction {
    private Object version;
    private Object cacheEntry;

    public EntityInsertAction(Serializable id, Object[] state, Object instance, Object version, EntityPersister persister, boolean isVersionIncrementDisabled, SharedSessionContractImplementor session) {
        super(id, state, instance, isVersionIncrementDisabled, persister, session);
        this.version = version;
    }

    public Object getVersion() {
        return this.version;
    }

    public void setVersion(Object version) {
        this.version = version;
    }

    protected Object getCacheEntry() {
        return this.cacheEntry;
    }

    protected void setCacheEntry(Object cacheEntry) {
        this.cacheEntry = cacheEntry;
    }

    @Override
    public boolean isEarlyInsert() {
        return false;
    }

    @Override
    protected EntityKey getEntityKey() {
        return this.getSession().generateEntityKey(this.getId(), this.getPersister());
    }

    @Override
    public void execute() throws HibernateException {
        this.nullifyTransientReferencesIfNotAlready();
        EntityPersister persister = this.getPersister();
        SharedSessionContractImplementor session = this.getSession();
        Object instance = this.getInstance();
        Serializable id = this.getId();
        boolean veto = this.preInsert();
        if (!veto) {
            persister.insert(id, this.getState(), instance, session);
            PersistenceContext persistenceContext = session.getPersistenceContextInternal();
            EntityEntry entry = persistenceContext.getEntry(instance);
            if (entry == null) {
                throw new AssertionFailure("possible non-threadsafe access to session");
            }
            entry.postInsert(this.getState());
            if (persister.hasInsertGeneratedProperties()) {
                persister.processInsertGeneratedProperties(id, instance, this.getState(), session);
                if (persister.isVersionPropertyGenerated()) {
                    this.version = Versioning.getVersion(this.getState(), persister);
                }
                entry.postUpdate(instance, this.getState(), this.version);
            }
            persistenceContext.registerInsertedKey(persister, this.getId());
        }
        SessionFactoryImplementor factory = session.getFactory();
        StatisticsImplementor statistics = factory.getStatistics();
        if (this.isCachePutEnabled(persister, session)) {
            CacheEntry ce = persister.buildCacheEntry(instance, this.getState(), this.version, session);
            this.cacheEntry = persister.getCacheEntryStructure().structure(ce);
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            Object ck = cache.generateCacheKey(id, persister, factory, session.getTenantIdentifier());
            boolean put = this.cacheInsert(persister, ck);
            if (put && statistics.isStatisticsEnabled()) {
                statistics.entityCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
            }
        }
        this.handleNaturalIdPostSaveNotifications(id);
        this.postInsert();
        if (statistics.isStatisticsEnabled() && !veto) {
            statistics.insertEntity(this.getPersister().getEntityName());
        }
        this.markExecuted();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean cacheInsert(EntityPersister persister, Object ck) {
        SharedSessionContractImplementor session = this.getSession();
        try {
            session.getEventListenerManager().cachePutStart();
            boolean bl = persister.getCacheAccessStrategy().insert(session, ck, this.cacheEntry, this.version);
            return bl;
        }
        finally {
            session.getEventListenerManager().cachePutEnd();
        }
    }

    protected void postInsert() {
        this.getFastSessionServices().eventListenerGroup_POST_INSERT.fireLazyEventOnEachListener(this::newPostInsertEvent, PostInsertEventListener::onPostInsert);
    }

    private PostInsertEvent newPostInsertEvent() {
        return new PostInsertEvent(this.getInstance(), this.getId(), this.getState(), this.getPersister(), this.eventSource());
    }

    protected void postCommitInsert(boolean success) {
        this.getFastSessionServices().eventListenerGroup_POST_COMMIT_INSERT.fireLazyEventOnEachListener(this::newPostInsertEvent, success ? PostInsertEventListener::onPostInsert : this::postCommitOnFailure);
    }

    private void postCommitOnFailure(PostInsertEventListener listener, PostInsertEvent event) {
        if (listener instanceof PostCommitInsertEventListener) {
            ((PostCommitInsertEventListener)listener).onPostInsertCommitFailed(event);
        } else {
            listener.onPostInsert(event);
        }
    }

    protected boolean preInsert() {
        boolean veto = false;
        EventListenerGroup<PreInsertEventListener> listenerGroup = this.getFastSessionServices().eventListenerGroup_PRE_INSERT;
        if (listenerGroup.isEmpty()) {
            return veto;
        }
        PreInsertEvent event = new PreInsertEvent(this.getInstance(), this.getId(), this.getState(), this.getPersister(), this.eventSource());
        for (PreInsertEventListener listener : listenerGroup.listeners()) {
            veto |= listener.onPreInsert(event);
        }
        return veto;
    }

    @Override
    public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws HibernateException {
        EntityPersister persister = this.getPersister();
        if (success && this.isCachePutEnabled(persister, this.getSession())) {
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            SessionFactoryImplementor factory = session.getFactory();
            Object ck = cache.generateCacheKey(this.getId(), persister, factory, session.getTenantIdentifier());
            boolean put = this.cacheAfterInsert(cache, ck);
            StatisticsImplementor statistics = factory.getStatistics();
            if (put && statistics.isStatisticsEnabled()) {
                statistics.entityCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
            }
        }
        this.postCommitInsert(success);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean cacheAfterInsert(EntityDataAccess cache, Object ck) {
        SharedSessionContractImplementor session = this.getSession();
        SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
        try {
            eventListenerManager.cachePutStart();
            boolean bl = cache.afterInsert(session, ck, this.cacheEntry, this.version);
            return bl;
        }
        finally {
            eventListenerManager.cachePutEnd();
        }
    }

    @Override
    protected boolean hasPostCommitEventListeners() {
        EventListenerGroup<PostInsertEventListener> group = this.getFastSessionServices().eventListenerGroup_POST_COMMIT_INSERT;
        for (PostInsertEventListener listener : group.listeners()) {
            if (!listener.requiresPostCommitHandling(this.getPersister())) continue;
            return true;
        }
        return false;
    }

    protected boolean isCachePutEnabled(EntityPersister persister, SharedSessionContractImplementor session) {
        return persister.canWriteToCache() && !persister.isCacheInvalidationRequired() && session.getCacheMode().isPutEnabled();
    }
}

