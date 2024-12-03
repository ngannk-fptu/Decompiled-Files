/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.loader.entity;

import java.io.Serializable;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.WrongClassException;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.cache.spi.entry.ReferenceCacheEntryImpl;
import org.hibernate.cache.spi.entry.StandardCacheEntryImpl;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.internal.StatefulPersistenceContext;
import org.hibernate.engine.internal.TwoPhaseLoad;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.internal.AbstractLockUpgradeEventListener;
import org.hibernate.event.spi.EventSource;
import org.hibernate.event.spi.LoadEvent;
import org.hibernate.event.spi.LoadEventListener;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;

public class CacheEntityLoaderHelper
extends AbstractLockUpgradeEventListener {
    public static final CacheEntityLoaderHelper INSTANCE = new CacheEntityLoaderHelper();
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(CacheEntityLoaderHelper.class);

    private CacheEntityLoaderHelper() {
    }

    public PersistenceContextEntry loadFromSessionCache(LoadEvent event, EntityKey keyToLoad, LoadEventListener.LoadType options) throws HibernateException {
        EventSource session = event.getSession();
        Object old = session.getEntityUsingInterceptor(keyToLoad);
        if (old != null) {
            EntityPersister persister;
            Status status;
            EntityEntry oldEntry = session.getPersistenceContext().getEntry(old);
            if (options.isCheckDeleted() && ((status = oldEntry.getStatus()) == Status.DELETED || status == Status.GONE)) {
                LOG.debug("Load request found matching entity in context, but it is scheduled for removal; returning null");
                return new PersistenceContextEntry(old, EntityStatus.REMOVED_ENTITY_MARKER);
            }
            if (options.isAllowNulls() && !(persister = event.getSession().getFactory().getEntityPersister(keyToLoad.getEntityName())).isInstance(old)) {
                LOG.debug("Load request found matching entity in context, but the matched entity was of an inconsistent return type; returning null");
                return new PersistenceContextEntry(old, EntityStatus.INCONSISTENT_RTN_CLASS_MARKER);
            }
            this.upgradeLock(old, oldEntry, event.getLockOptions(), event.getSession());
        }
        return new PersistenceContextEntry(old, EntityStatus.MANAGED);
    }

    public Object loadFromSecondLevelCache(LoadEvent event, EntityPersister persister, EntityKey entityKey) {
        boolean useCache;
        EventSource source = event.getSession();
        boolean bl = useCache = persister.canReadFromCache() && source.getCacheMode().isGetEnabled() && event.getLockMode().lessThan(LockMode.READ);
        if (!useCache) {
            return null;
        }
        Object ce = this.getFromSharedCache(event, persister, source);
        if (ce == null) {
            return null;
        }
        return this.processCachedEntry(event, persister, ce, source, entityKey);
    }

    private Object processCachedEntry(LoadEvent event, EntityPersister persister, Object ce, SessionImplementor source, EntityKey entityKey) {
        CacheEntry entry = (CacheEntry)persister.getCacheEntryStructure().destructure(ce, source.getFactory());
        if (entry.isReferenceEntry()) {
            if (event.getInstanceToLoad() != null) {
                throw new HibernateException("Attempt to load entity [%s] from cache using provided object instance, but cache is storing references: " + event.getEntityId());
            }
            return this.convertCacheReferenceEntryToEntity((ReferenceCacheEntryImpl)entry, event.getSession(), entityKey);
        }
        Object entity = this.convertCacheEntryToEntity(entry, event.getEntityId(), persister, event, entityKey);
        if (!persister.isInstance(entity)) {
            throw new WrongClassException("loaded object was of wrong class " + entity.getClass(), event.getEntityId(), persister.getEntityName());
        }
        return entity;
    }

    private Object getFromSharedCache(LoadEvent event, EntityPersister persister, SessionImplementor source) {
        EntityDataAccess cache = persister.getCacheAccessStrategy();
        SessionFactoryImplementor factory = source.getFactory();
        Object ck = cache.generateCacheKey(event.getEntityId(), persister, factory, source.getTenantIdentifier());
        Serializable ce = CacheHelper.fromSharedCache(source, ck, persister.getCacheAccessStrategy());
        StatisticsImplementor statistics = factory.getStatistics();
        if (statistics.isStatisticsEnabled()) {
            if (ce == null) {
                statistics.entityCacheMiss(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
            } else {
                statistics.entityCacheHit(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
            }
        }
        return ce;
    }

    private Object convertCacheReferenceEntryToEntity(ReferenceCacheEntryImpl referenceCacheEntry, EventSource session, EntityKey entityKey) {
        Object entity = referenceCacheEntry.getReference();
        if (entity == null) {
            throw new IllegalStateException("Reference cache entry contained null : " + referenceCacheEntry.toString());
        }
        this.makeEntityCircularReferenceSafe(referenceCacheEntry, session, entity, entityKey);
        return entity;
    }

    private void makeEntityCircularReferenceSafe(ReferenceCacheEntryImpl referenceCacheEntry, EventSource session, Object entity, EntityKey entityKey) {
        StatefulPersistenceContext statefulPersistenceContext = (StatefulPersistenceContext)session.getPersistenceContext();
        if (ManagedTypeHelper.isManagedEntity(entity)) {
            statefulPersistenceContext.addReferenceEntry(entity, Status.READ_ONLY);
        } else {
            TwoPhaseLoad.addUninitializedCachedEntity(entityKey, entity, referenceCacheEntry.getSubclassPersister(), LockMode.NONE, referenceCacheEntry.getVersion(), session);
        }
        statefulPersistenceContext.initializeNonLazyCollections();
    }

    private Object convertCacheEntryToEntity(CacheEntry entry, Serializable entityId, EntityPersister persister, LoadEvent event, EntityKey entityKey) {
        EventSource session = event.getSession();
        SessionFactoryImplementor factory = session.getFactory();
        if (LOG.isTraceEnabled()) {
            LOG.tracef("Converting second-level cache entry [%s] into entity : %s", entry, MessageHelper.infoString(persister, entityId, factory));
        }
        EntityPersister subclassPersister = factory.getEntityPersister(entry.getSubclass());
        Object optionalObject = event.getInstanceToLoad();
        Object entity = optionalObject == null ? session.instantiate(subclassPersister, entityId) : optionalObject;
        TwoPhaseLoad.addUninitializedCachedEntity(entityKey, entity, subclassPersister, LockMode.NONE, entry.getVersion(), session);
        PersistenceContext persistenceContext = session.getPersistenceContext();
        Type[] types = subclassPersister.getPropertyTypes();
        Object[] values = ((StandardCacheEntryImpl)entry).assemble(entity, entityId, subclassPersister, session.getInterceptor(), session);
        if (((StandardCacheEntryImpl)entry).isDeepCopyNeeded()) {
            TypeHelper.deepCopy(values, types, subclassPersister.getPropertyUpdateability(), values, session);
        }
        Object version = Versioning.getVersion(values, subclassPersister);
        LOG.tracef("Cached Version : %s", version);
        Object proxy = persistenceContext.getProxy(entityKey);
        boolean isReadOnly = proxy != null ? ((HibernateProxy)proxy).getHibernateLazyInitializer().isReadOnly() : session.isDefaultReadOnly();
        persistenceContext.addEntry(entity, isReadOnly ? Status.READ_ONLY : Status.MANAGED, values, null, entityId, version, LockMode.NONE, true, subclassPersister, false);
        subclassPersister.afterInitialize(entity, session);
        persistenceContext.initializeNonLazyCollections();
        PostLoadEvent postLoadEvent = event.getPostLoadEvent().setEntity(entity).setId(entityId).setPersister(persister);
        session.getSessionFactory().getFastSessionServices().firePostLoadEvent(postLoadEvent);
        return entity;
    }

    public static class PersistenceContextEntry {
        private final Object entity;
        private EntityStatus status;

        public PersistenceContextEntry(Object entity, EntityStatus status) {
            this.entity = entity;
            this.status = status;
        }

        public Object getEntity() {
            return this.entity;
        }

        public EntityStatus getStatus() {
            return this.status;
        }

        public boolean isManaged() {
            return EntityStatus.MANAGED == this.status;
        }
    }

    public static enum EntityStatus {
        MANAGED,
        REMOVED_ENTITY_MARKER,
        INCONSISTENT_RTN_CLASS_MARKER;

    }
}

