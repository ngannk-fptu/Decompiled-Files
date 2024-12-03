/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.action.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.HibernateException;
import org.hibernate.action.internal.EntityAction;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.service.spi.EventListenerGroup;
import org.hibernate.event.spi.PostCommitUpdateEventListener;
import org.hibernate.event.spi.PostUpdateEvent;
import org.hibernate.event.spi.PostUpdateEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.tuple.entity.EntityMetamodel;
import org.hibernate.type.TypeHelper;

public class EntityUpdateAction
extends EntityAction {
    private final Object[] state;
    private final Object[] previousState;
    private final Object previousVersion;
    private final int[] dirtyFields;
    private final boolean hasDirtyCollection;
    private final Object rowId;
    private final Object[] previousNaturalIdValues;
    private Object nextVersion;
    private Object cacheEntry;
    private SoftLock lock;

    public EntityUpdateAction(Serializable id, Object[] state, int[] dirtyProperties, boolean hasDirtyCollection, Object[] previousState, Object previousVersion, Object nextVersion, Object instance, Object rowId, EntityPersister persister, SharedSessionContractImplementor session) {
        super(session, id, instance, persister);
        this.state = state;
        this.previousState = previousState;
        this.previousVersion = previousVersion;
        this.nextVersion = nextVersion;
        this.dirtyFields = dirtyProperties;
        this.hasDirtyCollection = hasDirtyCollection;
        this.rowId = rowId;
        this.previousNaturalIdValues = this.determinePreviousNaturalIdValues(persister, previousState, session, id);
        session.getPersistenceContextInternal().getNaturalIdHelper().manageLocalNaturalIdCrossReference(persister, id, state, this.previousNaturalIdValues, CachedNaturalIdValueSource.UPDATE);
    }

    private Object[] determinePreviousNaturalIdValues(EntityPersister persister, Object[] previousState, SharedSessionContractImplementor session, Serializable id) {
        if (!persister.hasNaturalIdentifier()) {
            return null;
        }
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        if (previousState != null) {
            return persistenceContext.getNaturalIdHelper().extractNaturalIdValues(previousState, persister);
        }
        return persistenceContext.getNaturalIdSnapshot(id, persister);
    }

    public Object[] getState() {
        return this.state;
    }

    public Object[] getPreviousState() {
        return this.previousState;
    }

    public Object getPreviousVersion() {
        return this.previousVersion;
    }

    public Object getNextVersion() {
        return this.nextVersion;
    }

    public void setNextVersion(Object nextVersion) {
        this.nextVersion = nextVersion;
    }

    public int[] getDirtyFields() {
        return this.dirtyFields;
    }

    public boolean hasDirtyCollection() {
        return this.hasDirtyCollection;
    }

    public Object getRowId() {
        return this.rowId;
    }

    public Object[] getPreviousNaturalIdValues() {
        return this.previousNaturalIdValues;
    }

    protected Object getCacheEntry() {
        return this.cacheEntry;
    }

    protected void setCacheEntry(Object cacheEntry) {
        this.cacheEntry = cacheEntry;
    }

    protected SoftLock getLock() {
        return this.lock;
    }

    protected void setLock(SoftLock lock) {
        this.lock = lock;
    }

    @Override
    public void execute() throws HibernateException {
        Object ck;
        Serializable id = this.getId();
        EntityPersister persister = this.getPersister();
        SharedSessionContractImplementor session = this.getSession();
        Object instance = this.getInstance();
        if (this.preUpdate()) {
            return;
        }
        SessionFactoryImplementor factory = session.getFactory();
        Object previousVersion = this.previousVersion;
        if (persister.isVersionPropertyGenerated()) {
            previousVersion = persister.getVersion(instance);
        }
        if (persister.canWriteToCache()) {
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            ck = cache.generateCacheKey(id, persister, factory, session.getTenantIdentifier());
            this.lock = cache.lockItem(session, ck, previousVersion);
        } else {
            ck = null;
        }
        persister.update(id, this.state, this.dirtyFields, this.hasDirtyCollection, this.previousState, previousVersion, instance, this.rowId, session);
        EntityEntry entry = session.getPersistenceContextInternal().getEntry(instance);
        if (entry == null) {
            throw new AssertionFailure("possible non thread safe access to session");
        }
        if (entry.getStatus() == Status.MANAGED || persister.isVersionPropertyGenerated()) {
            TypeHelper.deepCopy(this.state, persister.getPropertyTypes(), persister.getPropertyCheckability(), this.state, session);
            if (persister.hasUpdateGeneratedProperties()) {
                persister.processUpdateGeneratedProperties(id, instance, this.state, session);
                if (persister.isVersionPropertyGenerated()) {
                    this.nextVersion = Versioning.getVersion(this.state, persister);
                }
            }
            entry.postUpdate(instance, this.state, this.nextVersion);
        }
        if (entry.getStatus() == Status.DELETED) {
            boolean isImpliedOptimisticLocking;
            EntityMetamodel entityMetamodel = persister.getEntityMetamodel();
            boolean bl = isImpliedOptimisticLocking = !entityMetamodel.isVersioned() && entityMetamodel.getOptimisticLockStyle().isAllOrDirty();
            if (isImpliedOptimisticLocking && entry.getLoadedState() != null) {
                entry.postUpdate(instance, this.state, this.nextVersion);
            }
        }
        StatisticsImplementor statistics = factory.getStatistics();
        if (persister.canWriteToCache()) {
            if (persister.isCacheInvalidationRequired() || entry.getStatus() != Status.MANAGED) {
                persister.getCacheAccessStrategy().remove(session, ck);
            } else if (session.getCacheMode().isPutEnabled()) {
                CacheEntry ce = persister.buildCacheEntry(instance, this.state, this.nextVersion, this.getSession());
                this.cacheEntry = persister.getCacheEntryStructure().structure(ce);
                boolean put = this.cacheUpdate(persister, previousVersion, ck);
                if (put && statistics.isStatisticsEnabled()) {
                    statistics.entityCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), this.getPersister().getCacheAccessStrategy().getRegion().getName());
                }
            }
        }
        session.getPersistenceContextInternal().getNaturalIdHelper().manageSharedNaturalIdCrossReference(persister, id, this.state, this.previousNaturalIdValues, CachedNaturalIdValueSource.UPDATE);
        this.postUpdate();
        if (statistics.isStatisticsEnabled()) {
            statistics.updateEntity(this.getPersister().getEntityName());
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean cacheUpdate(EntityPersister persister, Object previousVersion, Object ck) {
        SharedSessionContractImplementor session = this.getSession();
        try {
            session.getEventListenerManager().cachePutStart();
            boolean bl = persister.getCacheAccessStrategy().update(session, ck, this.cacheEntry, this.nextVersion, previousVersion);
            return bl;
        }
        finally {
            session.getEventListenerManager().cachePutEnd();
        }
    }

    protected boolean preUpdate() {
        boolean veto = false;
        EventListenerGroup<PreUpdateEventListener> listenerGroup = this.getFastSessionServices().eventListenerGroup_PRE_UPDATE;
        if (listenerGroup.isEmpty()) {
            return veto;
        }
        PreUpdateEvent event = new PreUpdateEvent(this.getInstance(), this.getId(), this.state, this.previousState, this.getPersister(), this.eventSource());
        for (PreUpdateEventListener listener : listenerGroup.listeners()) {
            veto |= listener.onPreUpdate(event);
        }
        return veto;
    }

    protected void postUpdate() {
        this.getFastSessionServices().eventListenerGroup_POST_UPDATE.fireLazyEventOnEachListener(this::newPostUpdateEvent, PostUpdateEventListener::onPostUpdate);
    }

    private PostUpdateEvent newPostUpdateEvent() {
        return new PostUpdateEvent(this.getInstance(), this.getId(), this.state, this.previousState, this.dirtyFields, this.getPersister(), this.eventSource());
    }

    protected void postCommitUpdate(boolean success) {
        this.getFastSessionServices().eventListenerGroup_POST_COMMIT_UPDATE.fireLazyEventOnEachListener(this::newPostUpdateEvent, success ? PostUpdateEventListener::onPostUpdate : this::onPostCommitFailure);
    }

    private void onPostCommitFailure(PostUpdateEventListener listener, PostUpdateEvent event) {
        if (listener instanceof PostCommitUpdateEventListener) {
            ((PostCommitUpdateEventListener)listener).onPostUpdateCommitFailed(event);
        } else {
            listener.onPostUpdate(event);
        }
    }

    @Override
    protected boolean hasPostCommitEventListeners() {
        EventListenerGroup<PostUpdateEventListener> group = this.getFastSessionServices().eventListenerGroup_POST_COMMIT_UPDATE;
        for (PostUpdateEventListener listener : group.listeners()) {
            if (!listener.requiresPostCommitHandling(this.getPersister())) continue;
            return true;
        }
        return false;
    }

    @Override
    public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) throws CacheException {
        EntityPersister persister = this.getPersister();
        if (persister.canWriteToCache()) {
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            SessionFactoryImplementor factory = session.getFactory();
            Object ck = cache.generateCacheKey(this.getId(), persister, factory, session.getTenantIdentifier());
            if (success && this.cacheEntry != null && !persister.isCacheInvalidationRequired() && session.getCacheMode().isPutEnabled()) {
                boolean put = this.cacheAfterUpdate(cache, ck);
                StatisticsImplementor statistics = factory.getStatistics();
                if (put && statistics.isStatisticsEnabled()) {
                    statistics.entityCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
                }
            } else {
                cache.unlockItem(session, ck, this.lock);
            }
        }
        this.postCommitUpdate(success);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected boolean cacheAfterUpdate(EntityDataAccess cache, Object ck) {
        SharedSessionContractImplementor session = this.getSession();
        SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
        try {
            eventListenerManager.cachePutStart();
            boolean bl = cache.afterUpdate(session, ck, this.cacheEntry, this.nextVersion, this.previousVersion, this.lock);
            return bl;
        }
        finally {
            eventListenerManager.cachePutEnd();
        }
    }
}

