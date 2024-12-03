/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.io.Serializable;
import org.hibernate.AssertionFailure;
import org.hibernate.CacheMode;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.bytecode.enhance.spi.LazyPropertyInitializer;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.entry.CacheEntry;
import org.hibernate.engine.internal.Versioning;
import org.hibernate.engine.profile.Fetch;
import org.hibernate.engine.profile.FetchProfile;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.LoadQueryInfluencers;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.SessionEventListenerManager;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.PostLoadEvent;
import org.hibernate.event.spi.PostLoadEventListener;
import org.hibernate.event.spi.PreLoadEvent;
import org.hibernate.event.spi.PreLoadEventListener;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.property.access.internal.PropertyAccessStrategyBackRefImpl;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.hibernate.type.TypeHelper;
import org.jboss.logging.Logger;

public final class TwoPhaseLoad {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)TwoPhaseLoad.class.getName());

    private TwoPhaseLoad() {
    }

    public static void postHydrate(EntityPersister persister, Serializable id, Object[] values, Object rowId, Object object, LockMode lockMode, SharedSessionContractImplementor session) {
        Object version = Versioning.getVersion(values, persister);
        session.getPersistenceContextInternal().addEntry(object, Status.LOADING, values, rowId, id, version, lockMode, true, persister, false);
        if (version != null && LOG.isTraceEnabled()) {
            String versionStr = persister.isVersioned() ? persister.getVersionType().toLoggableString(version, session.getFactory()) : "null";
            LOG.tracef("Version: %s", versionStr);
        }
    }

    public static void initializeEntity(Object entity, boolean readOnly, SharedSessionContractImplementor session, PreLoadEvent preLoadEvent) {
        TwoPhaseLoad.initializeEntity(entity, readOnly, session, preLoadEvent, EntityResolver.DEFAULT);
    }

    public static void initializeEntity(Object entity, boolean readOnly, SharedSessionContractImplementor session, PreLoadEvent preLoadEvent, EntityResolver entityResolver) {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        EntityEntry entityEntry = persistenceContext.getEntry(entity);
        if (entityEntry == null) {
            throw new AssertionFailure("possible non-threadsafe access to the session");
        }
        TwoPhaseLoad.initializeEntityEntryLoadedState(entity, entityEntry, session, entityResolver);
        TwoPhaseLoad.initializeEntityFromEntityEntryLoadedState(entity, entityEntry, readOnly, session, preLoadEvent);
    }

    public static void initializeEntityEntryLoadedState(Object entity, EntityEntry entityEntry, SharedSessionContractImplementor session, EntityResolver entityResolver) throws HibernateException {
        EntityPersister persister = entityEntry.getPersister();
        Serializable id = entityEntry.getId();
        Object[] hydratedState = entityEntry.getLoadedState();
        boolean debugEnabled = LOG.isDebugEnabled();
        if (debugEnabled) {
            LOG.debugf("Resolving attributes for %s", MessageHelper.infoString(persister, id, session.getFactory()));
        }
        String entityName = persister.getEntityName();
        String[] propertyNames = persister.getPropertyNames();
        Type[] types = persister.getPropertyTypes();
        for (int i = 0; i < hydratedState.length; ++i) {
            Boolean overridingEager;
            Object value = hydratedState[i];
            if (debugEnabled) {
                LOG.debugf("Processing attribute `%s` : value = %s", propertyNames[i], value == LazyPropertyInitializer.UNFETCHED_PROPERTY ? "<un-fetched>" : (value == PropertyAccessStrategyBackRefImpl.UNKNOWN ? "<unknown>" : value));
            }
            if (value == LazyPropertyInitializer.UNFETCHED_PROPERTY) {
                if (debugEnabled) {
                    LOG.debugf("Resolving <un-fetched> attribute : `%s`", propertyNames[i]);
                }
                if (!types[i].isCollectionType()) continue;
                overridingEager = TwoPhaseLoad.getOverridingEager(session, entityName, propertyNames[i], types[i], debugEnabled);
                types[i].resolve(value, session, entity, overridingEager);
                continue;
            }
            if (value != PropertyAccessStrategyBackRefImpl.UNKNOWN) {
                if (debugEnabled) {
                    boolean isLazyEnhanced = persister.getBytecodeEnhancementMetadata().getLazyAttributesMetadata().getLazyAttributeNames().contains(propertyNames[i]);
                    LOG.debugf("Attribute (`%s`)  - enhanced for lazy-loading? - %s", propertyNames[i], isLazyEnhanced);
                }
                overridingEager = TwoPhaseLoad.getOverridingEager(session, entityName, propertyNames[i], types[i], debugEnabled);
                hydratedState[i] = types[i].isEntityType() ? entityResolver.resolve((EntityType)types[i], value, session, entity, overridingEager) : types[i].resolve(value, session, entity, overridingEager);
                continue;
            }
            if (!debugEnabled) continue;
            LOG.debugf("Skipping <unknown> attribute : `%s`", propertyNames[i]);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void initializeEntityFromEntityEntryLoadedState(Object entity, EntityEntry entityEntry, boolean readOnly, SharedSessionContractImplementor session, PreLoadEvent preLoadEvent) throws HibernateException {
        PersistenceContext persistenceContext = session.getPersistenceContextInternal();
        EntityPersister persister = entityEntry.getPersister();
        Serializable id = entityEntry.getId();
        Object[] hydratedState = entityEntry.getLoadedState();
        boolean debugEnabled = LOG.isDebugEnabled();
        if (session.isEventSource()) {
            preLoadEvent.setEntity(entity).setState(hydratedState).setId(id).setPersister(persister);
            session.getFactory().getFastSessionServices().eventListenerGroup_PRE_LOAD.fireEventOnEachListener(preLoadEvent, PreLoadEventListener::onPreLoad);
        }
        persister.setPropertyValues(entity, hydratedState);
        SessionFactoryImplementor factory = session.getFactory();
        StatisticsImplementor statistics = factory.getStatistics();
        if (persister.canWriteToCache() && session.getCacheMode().isPutEnabled()) {
            if (debugEnabled) {
                LOG.debugf("Adding entity to second-level cache: %s", MessageHelper.infoString(persister, id, session.getFactory()));
            }
            Object version = Versioning.getVersion(hydratedState, persister);
            CacheEntry entry = persister.buildCacheEntry(entity, hydratedState, version, session);
            EntityDataAccess cache = persister.getCacheAccessStrategy();
            Object cacheKey = cache.generateCacheKey(id, persister, factory, session.getTenantIdentifier());
            if (session.getPersistenceContextInternal().wasInsertedDuringTransaction(persister, id)) {
                cache.update(session, cacheKey, persister.getCacheEntryStructure().structure(entry), version, version);
            } else {
                SessionEventListenerManager eventListenerManager = session.getEventListenerManager();
                try {
                    eventListenerManager.cachePutStart();
                    boolean put = cache.putFromLoad(session, cacheKey, persister.getCacheEntryStructure().structure(entry), version, TwoPhaseLoad.useMinimalPuts(session, entityEntry));
                    if (put && statistics.isStatisticsEnabled()) {
                        statistics.entityCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), cache.getRegion().getName());
                    }
                }
                finally {
                    eventListenerManager.cachePutEnd();
                }
            }
        }
        if (persister.hasNaturalIdentifier()) {
            persistenceContext.getNaturalIdHelper().cacheNaturalIdCrossReferenceFromLoad(persister, id, persistenceContext.getNaturalIdHelper().extractNaturalIdValues(hydratedState, persister));
        }
        boolean isReallyReadOnly = readOnly;
        if (!persister.isMutable()) {
            isReallyReadOnly = true;
        } else {
            Object proxy = persistenceContext.getProxy(entityEntry.getEntityKey());
            if (proxy != null) {
                isReallyReadOnly = ((HibernateProxy)proxy).getHibernateLazyInitializer().isReadOnly();
            }
        }
        if (isReallyReadOnly) {
            persistenceContext.setEntryStatus(entityEntry, Status.READ_ONLY);
        } else {
            TypeHelper.deepCopy(hydratedState, persister.getPropertyTypes(), persister.getPropertyUpdateability(), hydratedState, session);
            persistenceContext.setEntryStatus(entityEntry, Status.MANAGED);
        }
        if (debugEnabled) {
            LOG.debugf("Done materializing entity %s", MessageHelper.infoString(persister, id, session.getFactory()));
        }
        if (statistics.isStatisticsEnabled()) {
            statistics.loadEntity(persister.getEntityName());
        }
    }

    public static void afterInitialize(Object entity, SharedSessionContractImplementor session) {
        PersistenceContext persistenceContext = session.getPersistenceContext();
        EntityEntry entityEntry = persistenceContext.getEntry(entity);
        entityEntry.getPersister().afterInitialize(entity, session);
    }

    private static Boolean getOverridingEager(SharedSessionContractImplementor session, String entityName, String associationName, Type associationType, boolean isDebugEnabled) {
        if (associationType.isCollectionType() || associationType.isAssociationType()) {
            if (session.isEnforcingFetchGraph()) {
                return false;
            }
            Boolean overridingEager = TwoPhaseLoad.isEagerFetchProfile(session, entityName, associationName);
            if (overridingEager != null) {
                if (isDebugEnabled) {
                    LOG.debugf("Overriding eager fetching using active fetch profile. EntityName: %s, associationName: %s, eager fetching: %s", entityName, associationName, overridingEager);
                }
                return overridingEager;
            }
        }
        return null;
    }

    private static Boolean isEagerFetchProfile(SharedSessionContractImplementor session, String entityName, String associationName) {
        LoadQueryInfluencers loadQueryInfluencers = session.getLoadQueryInfluencers();
        if (loadQueryInfluencers.hasEnabledFetchProfiles()) {
            String role = entityName + '.' + associationName;
            SessionFactoryImplementor factory = session.getFactory();
            for (String fetchProfileName : loadQueryInfluencers.getEnabledFetchProfileNames()) {
                FetchProfile fp = factory.getFetchProfile(fetchProfileName);
                Fetch fetch = fp.getFetchByRole(role);
                if (fetch == null || Fetch.Style.JOIN != fetch.getStyle()) continue;
                return true;
            }
        }
        return null;
    }

    @Deprecated
    public static void postLoad(Object entity, SharedSessionContractImplementor session, PostLoadEvent postLoadEvent, Iterable<PostLoadEventListener> postLoadEventListeners) {
        TwoPhaseLoad.postLoad(entity, session, postLoadEvent);
    }

    public static void postLoad(Object entity, SharedSessionContractImplementor session, PostLoadEvent postLoadEvent) {
        if (session.isEventSource()) {
            EntityEntry entityEntry = session.getPersistenceContextInternal().getEntry(entity);
            postLoadEvent.setEntity(entity).setId(entityEntry.getId()).setPersister(entityEntry.getPersister());
            session.getFactory().getFastSessionServices().firePostLoadEvent(postLoadEvent);
        }
    }

    private static boolean useMinimalPuts(SharedSessionContractImplementor session, EntityEntry entityEntry) {
        if (session.getFactory().getSessionFactoryOptions().isMinimalPutsEnabled()) {
            return session.getCacheMode() != CacheMode.REFRESH;
        }
        EntityPersister persister = entityEntry.getPersister();
        return persister.hasLazyProperties() && persister.isLazyPropertiesCacheable();
    }

    public static void addUninitializedEntity(EntityKey key, Object object, EntityPersister persister, LockMode lockMode, SharedSessionContractImplementor session) {
        session.getPersistenceContextInternal().addEntity(object, Status.LOADING, null, key, null, lockMode, true, persister, false);
    }

    public static void addUninitializedCachedEntity(EntityKey key, Object object, EntityPersister persister, LockMode lockMode, Object version, SharedSessionContractImplementor session) {
        session.getPersistenceContextInternal().addEntity(object, Status.LOADING, null, key, version, lockMode, true, persister, false);
    }

    public static interface EntityResolver {
        public static final EntityResolver DEFAULT = (entityType, value, session, owner, overridingEager) -> entityType.resolve(value, session, owner, overridingEager);

        public Object resolve(EntityType var1, Object var2, SharedSessionContractImplementor var3, Object var4, Boolean var5);
    }
}

