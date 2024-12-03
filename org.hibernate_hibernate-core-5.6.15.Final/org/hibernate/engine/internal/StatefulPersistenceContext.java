/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.engine.internal;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import org.hibernate.AssertionFailure;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.LockMode;
import org.hibernate.MappingException;
import org.hibernate.NonUniqueObjectException;
import org.hibernate.PersistentObjectException;
import org.hibernate.TransientObjectException;
import org.hibernate.action.spi.AfterTransactionCompletionProcess;
import org.hibernate.bytecode.enhance.spi.interceptor.BytecodeLazyAttributeInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.EnhancementAsProxyLazinessInterceptor;
import org.hibernate.bytecode.enhance.spi.interceptor.LazyAttributeLoadingInterceptor;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.access.SoftLock;
import org.hibernate.collection.spi.LazyInitializable;
import org.hibernate.collection.spi.PersistentCollection;
import org.hibernate.engine.internal.CacheHelper;
import org.hibernate.engine.internal.EntityEntryContext;
import org.hibernate.engine.internal.ImmutableEntityEntryFactory;
import org.hibernate.engine.internal.ManagedTypeHelper;
import org.hibernate.engine.internal.MutableEntityEntryFactory;
import org.hibernate.engine.internal.NaturalIdXrefDelegate;
import org.hibernate.engine.loading.internal.LoadContexts;
import org.hibernate.engine.spi.AssociationKey;
import org.hibernate.engine.spi.BatchFetchQueue;
import org.hibernate.engine.spi.CachedNaturalIdValueSource;
import org.hibernate.engine.spi.CollectionEntry;
import org.hibernate.engine.spi.CollectionKey;
import org.hibernate.engine.spi.EntityEntry;
import org.hibernate.engine.spi.EntityKey;
import org.hibernate.engine.spi.EntityUniqueKey;
import org.hibernate.engine.spi.ManagedEntity;
import org.hibernate.engine.spi.PersistenceContext;
import org.hibernate.engine.spi.PersistentAttributeInterceptable;
import org.hibernate.engine.spi.PersistentAttributeInterceptor;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.engine.spi.Status;
import org.hibernate.event.spi.EventSource;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.collections.ConcurrentReferenceHashMap;
import org.hibernate.internal.util.collections.IdentityMap;
import org.hibernate.metamodel.spi.MetamodelImplementor;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.LazyInitializer;
import org.hibernate.stat.internal.StatsHelper;
import org.hibernate.stat.spi.StatisticsImplementor;
import org.hibernate.type.CollectionType;
import org.jboss.logging.Logger;

public class StatefulPersistenceContext
implements PersistenceContext {
    private static final CoreMessageLogger LOG = (CoreMessageLogger)Logger.getMessageLogger(CoreMessageLogger.class, (String)StatefulPersistenceContext.class.getName());
    private static final int INIT_COLL_SIZE = 8;
    private SharedSessionContractImplementor session;
    private EntityEntryContext entityEntryContext;
    private HashMap<EntityKey, Object> entitiesByKey;
    private HashMap<EntityUniqueKey, Object> entitiesByUniqueKey;
    private ConcurrentReferenceHashMap<EntityKey, Object> proxiesByKey;
    private HashMap<EntityKey, Object> entitySnapshotsByKey;
    private IdentityHashMap<Object, PersistentCollection> arrayHolders;
    private IdentityMap<PersistentCollection, CollectionEntry> collectionEntries;
    private HashMap<CollectionKey, PersistentCollection> collectionsByKey;
    private HashSet<EntityKey> nullifiableEntityKeys;
    private HashSet<AssociationKey> nullAssociations;
    private ArrayList<PersistentCollection> nonlazyCollections;
    private HashMap<CollectionKey, PersistentCollection> unownedCollections;
    private IdentityHashMap<Object, Object> parentsByChild;
    private int cascading;
    private int loadCounter;
    private int removeOrphanBeforeUpdatesCounter;
    private boolean flushing;
    private boolean defaultReadOnly;
    private boolean hasNonReadOnlyEntities;
    private LoadContexts loadContexts;
    private BatchFetchQueue batchFetchQueue;
    private HashMap<String, HashSet<Serializable>> insertedKeysMap;
    private NaturalIdXrefDelegate naturalIdXrefDelegate;
    private final PersistenceContext.NaturalIdHelper naturalIdHelper = new PersistenceContext.NaturalIdHelper(){

        @Override
        public void cacheNaturalIdCrossReferenceFromLoad(EntityPersister persister, Serializable id, Object[] naturalIdValues) {
            if (!persister.hasNaturalIdentifier()) {
                return;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            boolean justAddedLocally = StatefulPersistenceContext.this.getNaturalIdXrefDelegate().cacheNaturalIdCrossReference(persister, id, naturalIdValues);
            if (justAddedLocally && persister.hasNaturalIdCache()) {
                this.managedSharedCacheEntries(persister, id, naturalIdValues, null, CachedNaturalIdValueSource.LOAD);
            }
        }

        @Override
        public void manageLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state, Object[] previousState, CachedNaturalIdValueSource source) {
            if (!persister.hasNaturalIdentifier()) {
                return;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            Object[] naturalIdValues = this.extractNaturalIdValues(state, persister);
            StatefulPersistenceContext.this.getNaturalIdXrefDelegate().cacheNaturalIdCrossReference(persister, id, naturalIdValues);
        }

        @Override
        public void manageSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state, Object[] previousState, CachedNaturalIdValueSource source) {
            if (!persister.hasNaturalIdentifier()) {
                return;
            }
            if (!persister.hasNaturalIdCache()) {
                return;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            Object[] naturalIdValues = this.extractNaturalIdValues(state, persister);
            Object[] previousNaturalIdValues = previousState == null ? null : this.extractNaturalIdValues(previousState, persister);
            this.managedSharedCacheEntries(persister, id, naturalIdValues, previousNaturalIdValues, source);
        }

        private void managedSharedCacheEntries(final EntityPersister persister, final Serializable id, Object[] naturalIdValues, Object[] previousNaturalIdValues, CachedNaturalIdValueSource source) {
            final NaturalIdDataAccess naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
            final Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(naturalIdValues, persister, StatefulPersistenceContext.this.session);
            SessionFactoryImplementor factory = StatefulPersistenceContext.this.session.getFactory();
            final StatisticsImplementor statistics = factory.getStatistics();
            switch (source) {
                case LOAD: {
                    if (CacheHelper.fromSharedCache(StatefulPersistenceContext.this.session, naturalIdCacheKey, naturalIdCacheAccessStrategy) != null) {
                        return;
                    }
                    boolean put = naturalIdCacheAccessStrategy.putFromLoad(StatefulPersistenceContext.this.session, naturalIdCacheKey, id, null);
                    if (!put || !statistics.isStatisticsEnabled()) break;
                    statistics.naturalIdCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
                    break;
                }
                case INSERT: {
                    boolean put = naturalIdCacheAccessStrategy.insert(StatefulPersistenceContext.this.session, naturalIdCacheKey, id);
                    if (put && statistics.isStatisticsEnabled()) {
                        statistics.naturalIdCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
                    }
                    ((EventSource)StatefulPersistenceContext.this.session).getActionQueue().registerProcess(new AfterTransactionCompletionProcess(){

                        @Override
                        public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
                            if (success) {
                                boolean put = naturalIdCacheAccessStrategy.afterInsert(session, naturalIdCacheKey, id);
                                if (put && statistics.isStatisticsEnabled()) {
                                    statistics.naturalIdCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
                                }
                            } else {
                                naturalIdCacheAccessStrategy.evict(naturalIdCacheKey);
                            }
                        }
                    });
                    break;
                }
                case UPDATE: {
                    final Object previousCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(previousNaturalIdValues, persister, StatefulPersistenceContext.this.session);
                    if (naturalIdCacheKey.equals(previousCacheKey)) {
                        return;
                    }
                    final SoftLock removalLock = naturalIdCacheAccessStrategy.lockItem(StatefulPersistenceContext.this.session, previousCacheKey, null);
                    naturalIdCacheAccessStrategy.remove(StatefulPersistenceContext.this.session, previousCacheKey);
                    final SoftLock lock = naturalIdCacheAccessStrategy.lockItem(StatefulPersistenceContext.this.session, naturalIdCacheKey, null);
                    boolean put = naturalIdCacheAccessStrategy.update(StatefulPersistenceContext.this.session, naturalIdCacheKey, id);
                    if (put && statistics.isStatisticsEnabled()) {
                        statistics.naturalIdCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
                    }
                    ((EventSource)StatefulPersistenceContext.this.session).getActionQueue().registerProcess(new AfterTransactionCompletionProcess(){

                        @Override
                        public void doAfterTransactionCompletion(boolean success, SharedSessionContractImplementor session) {
                            naturalIdCacheAccessStrategy.unlockItem(session, previousCacheKey, removalLock);
                            if (success) {
                                boolean put = naturalIdCacheAccessStrategy.afterUpdate(session, naturalIdCacheKey, id, lock);
                                if (put && statistics.isStatisticsEnabled()) {
                                    statistics.naturalIdCachePut(StatsHelper.INSTANCE.getRootEntityRole(persister), naturalIdCacheAccessStrategy.getRegion().getName());
                                }
                            } else {
                                naturalIdCacheAccessStrategy.unlockItem(session, naturalIdCacheKey, lock);
                            }
                        }
                    });
                    break;
                }
                default: {
                    if (!LOG.isDebugEnabled()) break;
                    LOG.debug("Unexpected CachedNaturalIdValueSource [" + (Object)((Object)source) + "]");
                }
            }
        }

        @Override
        public Object[] removeLocalNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] state) {
            if (!persister.hasNaturalIdentifier()) {
                return null;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            Object[] naturalIdValues = StatefulPersistenceContext.this.getNaturalIdValues(state, persister);
            Object[] localNaturalIdValues = StatefulPersistenceContext.this.getNaturalIdXrefDelegate().removeNaturalIdCrossReference(persister, id, naturalIdValues);
            return localNaturalIdValues != null ? localNaturalIdValues : naturalIdValues;
        }

        @Override
        public void removeSharedNaturalIdCrossReference(EntityPersister persister, Serializable id, Object[] naturalIdValues) {
            if (!persister.hasNaturalIdentifier()) {
                return;
            }
            if (!persister.hasNaturalIdCache()) {
                return;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            NaturalIdDataAccess naturalIdCacheAccessStrategy = persister.getNaturalIdCacheAccessStrategy();
            Object naturalIdCacheKey = naturalIdCacheAccessStrategy.generateCacheKey(naturalIdValues, persister, StatefulPersistenceContext.this.session);
            naturalIdCacheAccessStrategy.evict(naturalIdCacheKey);
        }

        @Override
        public Object[] findCachedNaturalId(EntityPersister persister, Serializable pk) {
            return StatefulPersistenceContext.this.getNaturalIdXrefDelegate().findCachedNaturalId(StatefulPersistenceContext.this.locateProperPersister(persister), pk);
        }

        @Override
        public Serializable findCachedNaturalIdResolution(EntityPersister persister, Object[] naturalIdValues) {
            return StatefulPersistenceContext.this.getNaturalIdXrefDelegate().findCachedNaturalIdResolution(StatefulPersistenceContext.this.locateProperPersister(persister), naturalIdValues);
        }

        @Override
        public Object[] extractNaturalIdValues(Object[] state, EntityPersister persister) {
            int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
            if (state.length == naturalIdPropertyIndexes.length) {
                return state;
            }
            Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
            for (int i = 0; i < naturalIdPropertyIndexes.length; ++i) {
                naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
            }
            return naturalIdValues;
        }

        @Override
        public Object[] extractNaturalIdValues(Object entity, EntityPersister persister) {
            if (entity == null) {
                throw new AssertionFailure("Entity from which to extract natural id value(s) cannot be null");
            }
            if (persister == null) {
                throw new AssertionFailure("Persister to use in extracting natural id value(s) cannot be null");
            }
            int[] naturalIdentifierProperties = persister.getNaturalIdentifierProperties();
            Object[] naturalIdValues = new Object[naturalIdentifierProperties.length];
            for (int i = 0; i < naturalIdentifierProperties.length; ++i) {
                naturalIdValues[i] = persister.getPropertyValue(entity, naturalIdentifierProperties[i]);
            }
            return naturalIdValues;
        }

        @Override
        public Collection<Serializable> getCachedPkResolutions(EntityPersister entityPersister) {
            return StatefulPersistenceContext.this.getNaturalIdXrefDelegate().getCachedPkResolutions(entityPersister);
        }

        @Override
        public void handleSynchronization(EntityPersister persister, Serializable pk, Object entity) {
            boolean changed;
            if (!persister.hasNaturalIdentifier()) {
                return;
            }
            persister = StatefulPersistenceContext.this.locateProperPersister(persister);
            Object[] naturalIdValuesFromCurrentObjectState = this.extractNaturalIdValues(entity, persister);
            NaturalIdXrefDelegate naturalIdXrefDelegate = StatefulPersistenceContext.this.getNaturalIdXrefDelegate();
            boolean bl = changed = !naturalIdXrefDelegate.sameAsCached(persister, pk, naturalIdValuesFromCurrentObjectState);
            if (changed) {
                Object[] cachedNaturalIdValues = naturalIdXrefDelegate.findCachedNaturalId(persister, pk);
                naturalIdXrefDelegate.cacheNaturalIdCrossReference(persister, pk, naturalIdValuesFromCurrentObjectState);
                naturalIdXrefDelegate.stashInvalidNaturalIdReference(persister, cachedNaturalIdValues);
                this.removeSharedNaturalIdCrossReference(persister, pk, cachedNaturalIdValues);
            }
        }

        @Override
        public void cleanupFromSynchronizations() {
            StatefulPersistenceContext.this.getNaturalIdXrefDelegate().unStashInvalidNaturalIdReferences();
        }

        @Override
        public void handleEviction(Object object, EntityPersister persister, Serializable identifier) {
            StatefulPersistenceContext.this.getNaturalIdXrefDelegate().removeNaturalIdCrossReference(persister, identifier, this.findCachedNaturalId(persister, identifier));
        }
    };

    public StatefulPersistenceContext(SharedSessionContractImplementor session) {
        this.session = session;
        this.entityEntryContext = new EntityEntryContext(this);
    }

    private ConcurrentMap<EntityKey, Object> getOrInitializeProxiesByKey() {
        if (this.proxiesByKey == null) {
            this.proxiesByKey = new ConcurrentReferenceHashMap(8, 0.75f, 1, ConcurrentReferenceHashMap.ReferenceType.STRONG, ConcurrentReferenceHashMap.ReferenceType.WEAK, null);
        }
        return this.proxiesByKey;
    }

    @Override
    public boolean isStateless() {
        return false;
    }

    @Override
    public SharedSessionContractImplementor getSession() {
        return this.session;
    }

    @Override
    public LoadContexts getLoadContexts() {
        if (this.loadContexts == null) {
            this.loadContexts = new LoadContexts(this);
        }
        return this.loadContexts;
    }

    @Override
    public void addUnownedCollection(CollectionKey key, PersistentCollection collection) {
        if (this.unownedCollections == null) {
            this.unownedCollections = new HashMap(8);
        }
        this.unownedCollections.put(key, collection);
    }

    @Override
    public PersistentCollection useUnownedCollection(CollectionKey key) {
        return this.unownedCollections == null ? null : this.unownedCollections.remove(key);
    }

    @Override
    public BatchFetchQueue getBatchFetchQueue() {
        if (this.batchFetchQueue == null) {
            this.batchFetchQueue = new BatchFetchQueue(this);
        }
        return this.batchFetchQueue;
    }

    @Override
    public void clear() {
        if (this.proxiesByKey != null) {
            this.proxiesByKey.forEach((k, o) -> {
                if (o != null) {
                    ((HibernateProxy)o).getHibernateLazyInitializer().unsetSession();
                }
            });
        }
        for (Map.Entry<Object, EntityEntry> objectEntityEntryEntry : this.entityEntryContext.reentrantSafeEntityEntries()) {
            ManagedTypeHelper.processIfPersistentAttributeInterceptable(objectEntityEntryEntry.getKey(), StatefulPersistenceContext::unsetSession, null);
        }
        SharedSessionContractImplementor session = this.getSession();
        if (this.collectionEntries != null) {
            IdentityMap.onEachKey(this.collectionEntries, k -> k.unsetSession(session));
        }
        this.arrayHolders = null;
        this.entitiesByKey = null;
        this.entitiesByUniqueKey = null;
        this.entityEntryContext.clear();
        this.parentsByChild = null;
        this.entitySnapshotsByKey = null;
        this.collectionsByKey = null;
        this.nonlazyCollections = null;
        this.collectionEntries = null;
        this.unownedCollections = null;
        this.proxiesByKey = null;
        this.nullifiableEntityKeys = null;
        if (this.batchFetchQueue != null) {
            this.batchFetchQueue.clear();
        }
        this.hasNonReadOnlyEntities = false;
        if (this.loadContexts != null) {
            this.loadContexts.cleanup();
        }
        this.naturalIdXrefDelegate = null;
    }

    private static void unsetSession(PersistentAttributeInterceptable persistentAttributeInterceptable, Object ignoredParam) {
        PersistentAttributeInterceptor interceptor = persistentAttributeInterceptable.$$_hibernate_getInterceptor();
        if (interceptor instanceof LazyAttributeLoadingInterceptor) {
            ((LazyAttributeLoadingInterceptor)interceptor).unsetSession();
        }
    }

    @Override
    public boolean isDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    @Override
    public void setDefaultReadOnly(boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    @Override
    public boolean hasNonReadOnlyEntities() {
        return this.hasNonReadOnlyEntities;
    }

    @Override
    public void setEntryStatus(EntityEntry entry, Status status) {
        entry.setStatus(status);
        this.setHasNonReadOnlyEnties(status);
    }

    private void setHasNonReadOnlyEnties(Status status) {
        if (status == Status.DELETED || status == Status.MANAGED || status == Status.SAVING) {
            this.hasNonReadOnlyEntities = true;
        }
    }

    @Override
    public void afterTransactionCompletion() {
        this.cleanUpInsertedKeysAfterTransaction();
        this.entityEntryContext.downgradeLocks();
    }

    @Override
    public Object[] getDatabaseSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
        Object cached;
        EntityKey key = this.session.generateEntityKey(id, persister);
        Object object = cached = this.entitySnapshotsByKey == null ? null : this.entitySnapshotsByKey.get(key);
        if (cached != null) {
            return cached == NO_ROW ? null : (Object[])cached;
        }
        Object[] snapshot = persister.getDatabaseSnapshot(id, this.session);
        if (this.entitySnapshotsByKey == null) {
            this.entitySnapshotsByKey = new HashMap(8);
        }
        this.entitySnapshotsByKey.put(key, snapshot == null ? NO_ROW : snapshot);
        return snapshot;
    }

    @Override
    public Object[] getNaturalIdSnapshot(Serializable id, EntityPersister persister) throws HibernateException {
        if (!persister.hasNaturalIdentifier()) {
            return null;
        }
        Object[] cachedValue = this.naturalIdHelper.findCachedNaturalId(persister = this.locateProperPersister(persister), id);
        if (cachedValue != null) {
            return cachedValue;
        }
        if (persister.getEntityMetamodel().hasImmutableNaturalId()) {
            Object[] dbValue = persister.getNaturalIdentifierSnapshot(id, this.session);
            this.naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(persister, id, dbValue);
            return dbValue;
        }
        int[] props = persister.getNaturalIdentifierProperties();
        Object[] entitySnapshot = this.getDatabaseSnapshot(id, persister);
        if (entitySnapshot == NO_ROW || entitySnapshot == null) {
            return null;
        }
        Object[] naturalIdSnapshotSubSet = new Object[props.length];
        for (int i = 0; i < props.length; ++i) {
            naturalIdSnapshotSubSet[i] = entitySnapshot[props[i]];
        }
        this.naturalIdHelper.cacheNaturalIdCrossReferenceFromLoad(persister, id, naturalIdSnapshotSubSet);
        return naturalIdSnapshotSubSet;
    }

    private EntityPersister locateProperPersister(EntityPersister persister) {
        return this.session.getFactory().getMetamodel().entityPersister(persister.getRootEntityName());
    }

    @Override
    public Object[] getCachedDatabaseSnapshot(EntityKey key) {
        Object snapshot;
        Object object = snapshot = this.entitySnapshotsByKey == null ? null : this.entitySnapshotsByKey.get(key);
        if (snapshot == NO_ROW) {
            throw new IllegalStateException("persistence context reported no row snapshot for " + MessageHelper.infoString(key.getEntityName(), key.getIdentifier()));
        }
        return (Object[])snapshot;
    }

    @Override
    public void addEntity(EntityKey key, Object entity) {
        if (this.entitiesByKey == null) {
            this.entitiesByKey = new HashMap(8);
        }
        this.entitiesByKey.put(key, entity);
        BatchFetchQueue fetchQueue = this.batchFetchQueue;
        if (fetchQueue != null) {
            fetchQueue.removeBatchLoadableEntityKey(key);
        }
    }

    @Override
    public Object getEntity(EntityKey key) {
        return this.entitiesByKey == null ? null : this.entitiesByKey.get(key);
    }

    @Override
    public boolean containsEntity(EntityKey key) {
        return this.entitiesByKey == null ? false : this.entitiesByKey.containsKey(key);
    }

    @Override
    public Object removeEntity(EntityKey key) {
        BatchFetchQueue fetchQueue;
        Object entity;
        if (this.entitiesByKey != null) {
            entity = this.entitiesByKey.remove(key);
            if (this.entitiesByUniqueKey != null) {
                Iterator<Object> itr = this.entitiesByUniqueKey.values().iterator();
                while (itr.hasNext()) {
                    if (itr.next() != entity) continue;
                    itr.remove();
                }
            }
        } else {
            entity = null;
        }
        this.parentsByChild = null;
        if (this.entitySnapshotsByKey != null) {
            this.entitySnapshotsByKey.remove(key);
        }
        if (this.nullifiableEntityKeys != null) {
            this.nullifiableEntityKeys.remove(key);
        }
        if ((fetchQueue = this.batchFetchQueue) != null) {
            fetchQueue.removeBatchLoadableEntityKey(key);
            fetchQueue.removeSubselect(key);
        }
        return entity;
    }

    @Override
    public Object getEntity(EntityUniqueKey euk) {
        return this.entitiesByUniqueKey == null ? null : this.entitiesByUniqueKey.get(euk);
    }

    @Override
    public void addEntity(EntityUniqueKey euk, Object entity) {
        if (this.entitiesByUniqueKey == null) {
            this.entitiesByUniqueKey = new HashMap(8);
        }
        this.entitiesByUniqueKey.put(euk, entity);
    }

    @Override
    public EntityEntry getEntry(Object entity) {
        return this.entityEntryContext.getEntityEntry(entity);
    }

    @Override
    public EntityEntry removeEntry(Object entity) {
        return this.entityEntryContext.removeEntityEntry(entity);
    }

    @Override
    public boolean isEntryFor(Object entity) {
        return this.entityEntryContext.hasEntityEntry(entity);
    }

    @Override
    public CollectionEntry getCollectionEntry(PersistentCollection coll) {
        return this.collectionEntries == null ? null : this.collectionEntries.get(coll);
    }

    @Override
    public EntityEntry addEntity(Object entity, Status status, Object[] loadedState, EntityKey entityKey, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement) {
        this.addEntity(entityKey, entity);
        return this.addEntry(entity, status, loadedState, null, entityKey.getIdentifier(), version, lockMode, existsInDatabase, persister, disableVersionIncrement);
    }

    @Override
    public EntityEntry addEntry(Object entity, Status status, Object[] loadedState, Object rowId, Serializable id, Object version, LockMode lockMode, boolean existsInDatabase, EntityPersister persister, boolean disableVersionIncrement) {
        assert (lockMode != null);
        EntityEntry e = persister.getEntityEntryFactory() instanceof MutableEntityEntryFactory ? ((MutableEntityEntryFactory)persister.getEntityEntryFactory()).createEntityEntry(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, this) : ((ImmutableEntityEntryFactory)persister.getEntityEntryFactory()).createEntityEntry(status, loadedState, rowId, id, version, lockMode, existsInDatabase, persister, disableVersionIncrement, this);
        this.entityEntryContext.addEntityEntry(entity, e);
        this.setHasNonReadOnlyEnties(status);
        return e;
    }

    public EntityEntry addReferenceEntry(Object entity, Status status) {
        ((ManagedEntity)entity).$$_hibernate_getEntityEntry().setStatus(status);
        this.entityEntryContext.addEntityEntry(entity, ((ManagedEntity)entity).$$_hibernate_getEntityEntry());
        this.setHasNonReadOnlyEnties(status);
        return ((ManagedEntity)entity).$$_hibernate_getEntityEntry();
    }

    @Override
    public boolean containsCollection(PersistentCollection collection) {
        return this.collectionEntries != null && this.collectionEntries.containsKey(collection);
    }

    @Override
    public boolean containsProxy(Object entity) {
        return this.proxiesByKey != null && this.proxiesByKey.containsValue(entity);
    }

    @Override
    public boolean reassociateIfUninitializedProxy(Object value) throws MappingException {
        if (!Hibernate.isInitialized(value)) {
            if (value instanceof HibernateProxy) {
                HibernateProxy proxy = (HibernateProxy)value;
                LazyInitializer li = proxy.getHibernateLazyInitializer();
                this.reassociateProxy(li, proxy);
                return true;
            }
            if (ManagedTypeHelper.isPersistentAttributeInterceptable(value)) {
                BytecodeLazyAttributeInterceptor interceptor = (BytecodeLazyAttributeInterceptor)ManagedTypeHelper.asPersistentAttributeInterceptable(value).$$_hibernate_getInterceptor();
                if (interceptor != null) {
                    interceptor.setSession(this.getSession());
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public void reassociateProxy(Object value, Serializable id) throws MappingException {
        if (value instanceof HibernateProxy) {
            LOG.debugf("Setting proxy identifier: %s", id);
            HibernateProxy proxy = (HibernateProxy)value;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            li.setIdentifier(id);
            this.reassociateProxy(li, proxy);
        }
    }

    private void reassociateProxy(LazyInitializer li, HibernateProxy proxy) {
        if (li.getSession() != this.getSession()) {
            EntityPersister persister = this.session.getFactory().getMetamodel().entityPersister(li.getEntityName());
            EntityKey key = this.session.generateEntityKey(li.getInternalIdentifier(), persister);
            this.getOrInitializeProxiesByKey().putIfAbsent(key, proxy);
            proxy.getHibernateLazyInitializer().setSession(this.session);
        }
    }

    @Override
    public Object unproxy(Object maybeProxy) throws HibernateException {
        if (maybeProxy instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)maybeProxy;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            if (li.isUninitialized()) {
                throw new PersistentObjectException("object was an uninitialized proxy for " + li.getEntityName());
            }
            return li.getImplementation();
        }
        return maybeProxy;
    }

    @Override
    public Object unproxyAndReassociate(Object maybeProxy) throws HibernateException {
        if (maybeProxy instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)maybeProxy;
            LazyInitializer li = proxy.getHibernateLazyInitializer();
            this.reassociateProxy(li, proxy);
            return li.getImplementation();
        }
        if (ManagedTypeHelper.isPersistentAttributeInterceptable(maybeProxy)) {
            PersistentAttributeInterceptor interceptor = ManagedTypeHelper.asPersistentAttributeInterceptable(maybeProxy).$$_hibernate_getInterceptor();
            if (interceptor instanceof EnhancementAsProxyLazinessInterceptor) {
                ((EnhancementAsProxyLazinessInterceptor)interceptor).forceInitialize(maybeProxy, null);
            }
            return maybeProxy;
        }
        return maybeProxy;
    }

    @Override
    public void checkUniqueness(EntityKey key, Object object) throws HibernateException {
        Object entity = this.getEntity(key);
        if (entity == object) {
            throw new AssertionFailure("object already associated, but no entry was found");
        }
        if (entity != null) {
            throw new NonUniqueObjectException(key.getIdentifier(), key.getEntityName());
        }
    }

    @Override
    public Object narrowProxy(Object proxy, EntityPersister persister, EntityKey key, Object object) throws HibernateException {
        Class concreteProxyClass = persister.getConcreteProxyClass();
        boolean alreadyNarrow = concreteProxyClass.isInstance(proxy);
        if (!alreadyNarrow) {
            Object impl;
            LOG.narrowingProxy(concreteProxyClass);
            if (object != null) {
                this.removeProxyByKey(key);
                return object;
            }
            HibernateProxy originalHibernateProxy = (HibernateProxy)proxy;
            if (!originalHibernateProxy.getHibernateLazyInitializer().isUninitialized() && concreteProxyClass.isInstance(impl = originalHibernateProxy.getHibernateLazyInitializer().getImplementation())) {
                this.removeProxyByKey(key);
                return impl;
            }
            HibernateProxy narrowedProxy = (HibernateProxy)persister.createProxy(key.getIdentifier(), this.session);
            boolean readOnlyOrig = originalHibernateProxy.getHibernateLazyInitializer().isReadOnly();
            narrowedProxy.getHibernateLazyInitializer().setReadOnly(readOnlyOrig);
            return narrowedProxy;
        }
        if (object != null) {
            LazyInitializer li = ((HibernateProxy)proxy).getHibernateLazyInitializer();
            li.setImplementation(object);
        }
        return proxy;
    }

    private Object removeProxyByKey(EntityKey key) {
        if (this.proxiesByKey != null) {
            return this.proxiesByKey.remove(key);
        }
        return null;
    }

    @Override
    public Object proxyFor(EntityPersister persister, EntityKey key, Object impl) throws HibernateException {
        if (!persister.hasProxy()) {
            return impl;
        }
        Object proxy = this.getProxy(key);
        return proxy != null ? this.narrowProxy(proxy, persister, key, impl) : impl;
    }

    @Override
    public Object proxyFor(Object impl) throws HibernateException {
        EntityEntry e = this.getEntry(impl);
        if (e == null) {
            return impl;
        }
        return this.proxyFor(e.getPersister(), e.getEntityKey(), impl);
    }

    @Override
    public void addEnhancedProxy(EntityKey key, PersistentAttributeInterceptable entity) {
        if (this.entitiesByKey == null) {
            this.entitiesByKey = new HashMap(8);
        }
        this.entitiesByKey.put(key, entity);
    }

    @Override
    public Object getCollectionOwner(Serializable key, CollectionPersister collectionPersister) throws MappingException {
        EntityPersister ownerPersister = collectionPersister.getOwnerEntityPersister();
        if (ownerPersister.getIdentifierType().getReturnedClass().isInstance(key)) {
            return this.getEntity(this.session.generateEntityKey(key, collectionPersister.getOwnerEntityPersister()));
        }
        if (ownerPersister.isInstance(key)) {
            Serializable owenerId = ownerPersister.getIdentifier(key, this.session);
            if (owenerId == null) {
                return null;
            }
            return this.getEntity(this.session.generateEntityKey(owenerId, ownerPersister));
        }
        CollectionType collectionType = collectionPersister.getCollectionType();
        if (collectionType.getLHSPropertyName() != null) {
            Object owner = this.getEntity(new EntityUniqueKey(ownerPersister.getEntityName(), collectionType.getLHSPropertyName(), key, collectionPersister.getKeyType(), ownerPersister.getEntityMode(), this.session.getFactory()));
            if (owner != null) {
                return owner;
            }
            Serializable ownerId = ownerPersister.getIdByUniqueKey(key, collectionType.getLHSPropertyName(), this.session);
            return this.getEntity(this.session.generateEntityKey(ownerId, ownerPersister));
        }
        return this.getEntity(this.session.generateEntityKey(key, collectionPersister.getOwnerEntityPersister()));
    }

    @Override
    public Object getLoadedCollectionOwnerOrNull(PersistentCollection collection) {
        CollectionEntry ce = this.getCollectionEntry(collection);
        if (ce == null || ce.getLoadedPersister() == null) {
            return null;
        }
        Object loadedOwner = null;
        Serializable entityId = this.getLoadedCollectionOwnerIdOrNull(ce);
        if (entityId != null) {
            loadedOwner = this.getCollectionOwner(entityId, ce.getLoadedPersister());
        }
        return loadedOwner;
    }

    @Override
    public Serializable getLoadedCollectionOwnerIdOrNull(PersistentCollection collection) {
        return this.getLoadedCollectionOwnerIdOrNull(this.getCollectionEntry(collection));
    }

    private Serializable getLoadedCollectionOwnerIdOrNull(CollectionEntry ce) {
        if (ce == null || ce.getLoadedKey() == null || ce.getLoadedPersister() == null) {
            return null;
        }
        return ce.getLoadedPersister().getCollectionType().getIdOfOwnerOrNull(ce.getLoadedKey(), this.session);
    }

    @Override
    public void addUninitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) {
        CollectionEntry ce = new CollectionEntry(collection, persister, id, this.flushing);
        this.addCollection(collection, ce, id);
        if (persister.getBatchSize() > 1) {
            this.getBatchFetchQueue().addBatchLoadableCollection(collection, ce);
        }
    }

    @Override
    public void addUninitializedDetachedCollection(CollectionPersister persister, PersistentCollection collection) {
        CollectionEntry ce = new CollectionEntry(persister, collection.getKey());
        this.addCollection(collection, ce, collection.getKey());
        if (persister.getBatchSize() > 1) {
            this.getBatchFetchQueue().addBatchLoadableCollection(collection, ce);
        }
    }

    @Override
    public void addNewCollection(CollectionPersister persister, PersistentCollection collection) throws HibernateException {
        this.addCollection(collection, persister);
    }

    private void addCollection(PersistentCollection coll, CollectionEntry entry, Serializable key) {
        this.getOrInitializeCollectionEntries().put(coll, entry);
        CollectionKey collectionKey = new CollectionKey(entry.getLoadedPersister(), key);
        PersistentCollection old = this.addCollectionByKey(collectionKey, coll);
        if (old != null) {
            if (old == coll) {
                throw new AssertionFailure("bug adding collection twice");
            }
            old.unsetSession(this.session);
            if (this.collectionEntries != null) {
                this.collectionEntries.remove(old);
            }
        }
    }

    private IdentityMap<PersistentCollection, CollectionEntry> getOrInitializeCollectionEntries() {
        if (this.collectionEntries == null) {
            this.collectionEntries = IdentityMap.instantiateSequenced(8);
        }
        return this.collectionEntries;
    }

    private void addCollection(PersistentCollection collection, CollectionPersister persister) {
        CollectionEntry ce = new CollectionEntry(persister, collection);
        this.getOrInitializeCollectionEntries().put(collection, ce);
    }

    @Override
    public void addInitializedDetachedCollection(CollectionPersister collectionPersister, PersistentCollection collection) throws HibernateException {
        if (collection.isUnreferenced()) {
            this.addCollection(collection, collectionPersister);
        } else {
            CollectionEntry ce = new CollectionEntry(collection, this.session.getFactory());
            this.addCollection(collection, ce, collection.getKey());
        }
    }

    @Override
    public CollectionEntry addInitializedCollection(CollectionPersister persister, PersistentCollection collection, Serializable id) throws HibernateException {
        CollectionEntry ce = new CollectionEntry(collection, persister, id, this.flushing);
        ce.postInitialize(collection);
        this.addCollection(collection, ce, id);
        return ce;
    }

    @Override
    public PersistentCollection getCollection(CollectionKey collectionKey) {
        return this.collectionsByKey == null ? null : this.collectionsByKey.get(collectionKey);
    }

    @Override
    public void addNonLazyCollection(PersistentCollection collection) {
        if (this.nonlazyCollections == null) {
            this.nonlazyCollections = new ArrayList(8);
        }
        this.nonlazyCollections.add(collection);
    }

    @Override
    public void initializeNonLazyCollections() throws HibernateException {
        this.initializeNonLazyCollections(LazyInitializable::forceInitialization);
    }

    protected void initializeNonLazyCollections(Consumer<PersistentCollection> initializeAction) {
        if (this.loadCounter == 0) {
            LOG.trace("Initializing non-lazy collections");
            ++this.loadCounter;
            try {
                int size;
                while (this.nonlazyCollections != null && (size = this.nonlazyCollections.size()) > 0) {
                    initializeAction.accept(this.nonlazyCollections.remove(size - 1));
                }
            }
            finally {
                --this.loadCounter;
                this.clearNullProperties();
            }
        }
    }

    @Override
    public PersistentCollection getCollectionHolder(Object array) {
        return this.arrayHolders == null ? null : this.arrayHolders.get(array);
    }

    @Override
    public void addCollectionHolder(PersistentCollection holder) {
        if (this.arrayHolders == null) {
            this.arrayHolders = new IdentityHashMap(8);
        }
        this.arrayHolders.put(holder.getValue(), holder);
    }

    @Override
    public PersistentCollection removeCollectionHolder(Object array) {
        return this.arrayHolders != null ? this.arrayHolders.remove(array) : null;
    }

    @Override
    public Serializable getSnapshot(PersistentCollection coll) {
        return this.getCollectionEntry(coll).getSnapshot();
    }

    @Override
    public CollectionEntry getCollectionEntryOrNull(Object collection) {
        PersistentCollection coll;
        if (collection instanceof PersistentCollection) {
            coll = (PersistentCollection)collection;
        } else {
            coll = this.getCollectionHolder(collection);
            if (coll == null && this.collectionEntries != null) {
                Iterator<PersistentCollection> wrappers = this.collectionEntries.keyIterator();
                while (wrappers.hasNext()) {
                    PersistentCollection pc = wrappers.next();
                    if (!pc.isWrapper(collection)) continue;
                    coll = pc;
                    break;
                }
            }
        }
        return coll == null ? null : this.getCollectionEntry(coll);
    }

    @Override
    public Object getProxy(EntityKey key) {
        return this.proxiesByKey == null ? null : this.proxiesByKey.get(key);
    }

    @Override
    public void addProxy(EntityKey key, Object proxy) {
        this.getOrInitializeProxiesByKey().put(key, proxy);
    }

    @Override
    public Object removeProxy(EntityKey key) {
        BatchFetchQueue fetchQueue = this.batchFetchQueue;
        if (fetchQueue != null) {
            fetchQueue.removeBatchLoadableEntityKey(key);
            fetchQueue.removeSubselect(key);
        }
        return this.removeProxyByKey(key);
    }

    @Override
    public HashSet getNullifiableEntityKeys() {
        if (this.nullifiableEntityKeys == null) {
            this.nullifiableEntityKeys = new HashSet();
        }
        return this.nullifiableEntityKeys;
    }

    @Override
    @Deprecated
    public Map getEntitiesByKey() {
        return this.entitiesByKey == null ? Collections.emptyMap() : this.entitiesByKey;
    }

    @Override
    public Iterator managedEntitiesIterator() {
        if (this.entitiesByKey == null) {
            return Collections.emptyIterator();
        }
        return this.entitiesByKey.values().iterator();
    }

    @Override
    public int getNumberOfManagedEntities() {
        return this.entityEntryContext.getNumberOfManagedEntities();
    }

    @Override
    public Map getEntityEntries() {
        return null;
    }

    @Override
    @Deprecated
    public Map getCollectionEntries() {
        return this.getOrInitializeCollectionEntries();
    }

    @Override
    public void forEachCollectionEntry(BiConsumer<PersistentCollection, CollectionEntry> action, boolean concurrent) {
        if (this.collectionEntries != null) {
            if (concurrent) {
                for (Map.Entry<PersistentCollection, CollectionEntry> entry : IdentityMap.concurrentEntries(this.collectionEntries)) {
                    action.accept(entry.getKey(), entry.getValue());
                }
            } else {
                this.collectionEntries.forEach(action);
            }
        }
    }

    @Override
    public Map getCollectionsByKey() {
        if (this.collectionsByKey == null) {
            return Collections.emptyMap();
        }
        return this.collectionsByKey;
    }

    @Override
    public int getCascadeLevel() {
        return this.cascading;
    }

    @Override
    public int incrementCascadeLevel() {
        return ++this.cascading;
    }

    @Override
    public int decrementCascadeLevel() {
        return --this.cascading;
    }

    @Override
    public boolean isFlushing() {
        return this.flushing;
    }

    @Override
    public void setFlushing(boolean flushing) {
        boolean afterFlush = this.flushing && !flushing;
        this.flushing = flushing;
        if (afterFlush) {
            this.getNaturalIdHelper().cleanupFromSynchronizations();
        }
    }

    public boolean isRemovingOrphanBeforeUpates() {
        return this.removeOrphanBeforeUpdatesCounter > 0;
    }

    public void beginRemoveOrphanBeforeUpdates() {
        if (this.getCascadeLevel() < 1) {
            throw new IllegalStateException("Attempt to remove orphan when not cascading.");
        }
        if (this.removeOrphanBeforeUpdatesCounter >= this.getCascadeLevel()) {
            throw new IllegalStateException(String.format("Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before incrementing removeOrphanBeforeUpdatesCounter", this.getCascadeLevel(), this.removeOrphanBeforeUpdatesCounter));
        }
        ++this.removeOrphanBeforeUpdatesCounter;
    }

    public void endRemoveOrphanBeforeUpdates() {
        if (this.getCascadeLevel() < 1) {
            throw new IllegalStateException("Finished removing orphan when not cascading.");
        }
        if (this.removeOrphanBeforeUpdatesCounter > this.getCascadeLevel()) {
            throw new IllegalStateException(String.format("Cascade level [%d] is out of sync with removeOrphanBeforeUpdatesCounter [%d] before decrementing removeOrphanBeforeUpdatesCounter", this.getCascadeLevel(), this.removeOrphanBeforeUpdatesCounter));
        }
        --this.removeOrphanBeforeUpdatesCounter;
    }

    @Override
    public void beforeLoad() {
        ++this.loadCounter;
    }

    @Override
    public void afterLoad() {
        --this.loadCounter;
    }

    @Override
    public boolean isLoadFinished() {
        return this.loadCounter == 0;
    }

    @Override
    public String toString() {
        String entityKeySet = this.entitiesByKey == null ? "[]" : this.entitiesByKey.keySet().toString();
        String collectionsKeySet = this.collectionsByKey == null ? "[]" : this.collectionsByKey.keySet().toString();
        return "PersistenceContext[entityKeys=" + entityKeySet + ", collectionKeys=" + collectionsKeySet + "]";
    }

    @Override
    public Map.Entry<Object, EntityEntry>[] reentrantSafeEntityEntries() {
        return this.entityEntryContext.reentrantSafeEntityEntries();
    }

    @Override
    public Serializable getOwnerId(String entityName, String propertyName, Object childEntity, Map mergeMap) {
        String collectionRole = entityName + '.' + propertyName;
        EntityPersister persister = this.session.getFactory().getMetamodel().entityPersister(entityName);
        CollectionPersister collectionPersister = this.session.getFactory().getMetamodel().collectionPersister(collectionRole);
        Object parent = this.getParentsByChild(childEntity);
        if (parent != null) {
            EntityEntry entityEntry = this.entityEntryContext.getEntityEntry(parent);
            if (persister.isSubclassEntityName(entityEntry.getEntityName()) && this.isFoundInParent(propertyName, childEntity, persister, collectionPersister, parent)) {
                return this.getEntry(parent).getId();
            }
            this.removeChildParent(childEntity);
        }
        for (Map.Entry<Object, EntityEntry> me : this.reentrantSafeEntityEntries()) {
            EntityEntry entityEntry = me.getValue();
            if (!persister.isSubclassEntityName(entityEntry.getEntityName())) continue;
            Object entityEntryInstance = me.getKey();
            boolean found = this.isFoundInParent(propertyName, childEntity, persister, collectionPersister, entityEntryInstance);
            if (!found && mergeMap != null) {
                Object unmergedInstance = mergeMap.get(entityEntryInstance);
                Object unmergedChild = mergeMap.get(childEntity);
                if (unmergedInstance != null && unmergedChild != null) {
                    found = this.isFoundInParent(propertyName, unmergedChild, persister, collectionPersister, unmergedInstance);
                    LOG.debugf("Detached object being merged (corresponding with a managed entity) has a collection that [%s] the detached child.", found ? "contains" : "does not contain");
                }
            }
            if (!found) continue;
            return entityEntry.getId();
        }
        if (mergeMap != null) {
            for (Object e : mergeMap.entrySet()) {
                HibernateProxy proxy;
                Map.Entry mergeMapEntry = (Map.Entry)e;
                if (!(mergeMapEntry.getKey() instanceof HibernateProxy) || !persister.isSubclassEntityName((proxy = (HibernateProxy)mergeMapEntry.getKey()).getHibernateLazyInitializer().getEntityName())) continue;
                boolean found = this.isFoundInParent(propertyName, childEntity, persister, collectionPersister, mergeMap.get(proxy));
                LOG.debugf("Detached proxy being merged has a collection that [%s] the managed child.", found ? "contains" : "does not contain");
                if (!found) {
                    found = this.isFoundInParent(propertyName, mergeMap.get(childEntity), persister, collectionPersister, mergeMap.get(proxy));
                    LOG.debugf("Detached proxy being merged has a collection that [%s] the detached child being merged..", found ? "contains" : "does not contain");
                }
                if (!found) continue;
                return proxy.getHibernateLazyInitializer().getInternalIdentifier();
            }
        }
        return null;
    }

    private Object getParentsByChild(Object childEntity) {
        if (this.parentsByChild != null) {
            return this.parentsByChild.get(childEntity);
        }
        return null;
    }

    private boolean isFoundInParent(String property, Object childEntity, EntityPersister persister, CollectionPersister collectionPersister, Object potentialParent) {
        Object collection = persister.getPropertyValue(potentialParent, property);
        return collection != null && Hibernate.isInitialized(collection) && collectionPersister.getCollectionType().contains(collection, childEntity, this.session);
    }

    @Override
    public Object getIndexInOwner(String entity, String property, Object childEntity, Map mergeMap) {
        MetamodelImplementor metamodel = this.session.getFactory().getMetamodel();
        EntityPersister persister = metamodel.entityPersister(entity);
        CollectionPersister cp = metamodel.collectionPersister(entity + '.' + property);
        Object parent = this.getParentsByChild(childEntity);
        if (parent != null) {
            EntityEntry entityEntry = this.entityEntryContext.getEntityEntry(parent);
            if (persister.isSubclassEntityName(entityEntry.getEntityName())) {
                Object index = this.getIndexInParent(property, childEntity, persister, cp, parent);
                if (index == null && mergeMap != null) {
                    Object unMergedInstance = mergeMap.get(parent);
                    Object unMergedChild = mergeMap.get(childEntity);
                    if (unMergedInstance != null && unMergedChild != null) {
                        index = this.getIndexInParent(property, unMergedChild, persister, cp, unMergedInstance);
                        LOG.debugf("A detached object being merged (corresponding to a parent in parentsByChild) has an indexed collection that [%s] the detached child being merged. ", index != null ? "contains" : "does not contain");
                    }
                }
                if (index != null) {
                    return index;
                }
            } else {
                this.removeChildParent(childEntity);
            }
        }
        for (Map.Entry<Object, EntityEntry> me : this.reentrantSafeEntityEntries()) {
            EntityEntry ee = me.getValue();
            if (!persister.isSubclassEntityName(ee.getEntityName())) continue;
            Object instance = me.getKey();
            Object index = this.getIndexInParent(property, childEntity, persister, cp, instance);
            if (index == null && mergeMap != null) {
                Object unMergedInstance = mergeMap.get(instance);
                Object unMergedChild = mergeMap.get(childEntity);
                if (unMergedInstance != null && unMergedChild != null) {
                    index = this.getIndexInParent(property, unMergedChild, persister, cp, unMergedInstance);
                    LOG.debugf("A detached object being merged (corresponding to a managed entity) has an indexed collection that [%s] the detached child being merged. ", index != null ? "contains" : "does not contain");
                }
            }
            if (index == null) continue;
            return index;
        }
        return null;
    }

    private Object getIndexInParent(String property, Object childEntity, EntityPersister persister, CollectionPersister collectionPersister, Object potentialParent) {
        Object collection = persister.getPropertyValue(potentialParent, property);
        if (collection != null && Hibernate.isInitialized(collection)) {
            return collectionPersister.getCollectionType().indexOf(collection, childEntity);
        }
        return null;
    }

    @Override
    public void addNullProperty(EntityKey ownerKey, String propertyName) {
        if (this.nullAssociations == null) {
            this.nullAssociations = new HashSet(8);
        }
        this.nullAssociations.add(new AssociationKey(ownerKey, propertyName));
    }

    @Override
    public boolean isPropertyNull(EntityKey ownerKey, String propertyName) {
        return this.nullAssociations != null && this.nullAssociations.contains(new AssociationKey(ownerKey, propertyName));
    }

    private void clearNullProperties() {
        this.nullAssociations = null;
    }

    @Override
    public boolean isReadOnly(Object entityOrProxy) {
        boolean isReadOnly;
        if (entityOrProxy == null) {
            throw new AssertionFailure("object must be non-null.");
        }
        if (entityOrProxy instanceof HibernateProxy) {
            isReadOnly = ((HibernateProxy)entityOrProxy).getHibernateLazyInitializer().isReadOnly();
        } else {
            EntityEntry ee = this.getEntry(entityOrProxy);
            if (ee == null) {
                throw new TransientObjectException("Instance was not associated with this persistence context");
            }
            isReadOnly = ee.isReadOnly();
        }
        return isReadOnly;
    }

    @Override
    public void setReadOnly(Object object, boolean readOnly) {
        if (object == null) {
            throw new AssertionFailure("object must be non-null.");
        }
        if (this.isReadOnly(object) == readOnly) {
            return;
        }
        if (object instanceof HibernateProxy) {
            HibernateProxy proxy = (HibernateProxy)object;
            this.setProxyReadOnly(proxy, readOnly);
            if (Hibernate.isInitialized(proxy)) {
                this.setEntityReadOnly(proxy.getHibernateLazyInitializer().getImplementation(), readOnly);
            }
        } else {
            this.setEntityReadOnly(object, readOnly);
            Object maybeProxy = this.getSession().getPersistenceContextInternal().proxyFor(object);
            if (maybeProxy instanceof HibernateProxy) {
                this.setProxyReadOnly((HibernateProxy)maybeProxy, readOnly);
            }
        }
    }

    private void setProxyReadOnly(HibernateProxy proxy, boolean readOnly) {
        LazyInitializer hibernateLazyInitializer = proxy.getHibernateLazyInitializer();
        if (hibernateLazyInitializer.getSession() != this.getSession()) {
            throw new AssertionFailure("Attempt to set a proxy to read-only that is associated with a different session");
        }
        hibernateLazyInitializer.setReadOnly(readOnly);
    }

    private void setEntityReadOnly(Object entity, boolean readOnly) {
        EntityEntry entry = this.getEntry(entity);
        if (entry == null) {
            throw new TransientObjectException("Instance was not associated with this persistence context");
        }
        entry.setReadOnly(readOnly, entity);
        this.hasNonReadOnlyEntities = this.hasNonReadOnlyEntities || !readOnly;
    }

    @Override
    public void replaceDelayedEntityIdentityInsertKeys(EntityKey oldKey, Serializable generatedId) {
        Object entity = this.entitiesByKey == null ? null : this.entitiesByKey.remove(oldKey);
        EntityEntry oldEntry = this.entityEntryContext.removeEntityEntry(entity);
        this.parentsByChild = null;
        EntityKey newKey = this.session.generateEntityKey(generatedId, oldEntry.getPersister());
        this.addEntity(newKey, entity);
        this.addEntry(entity, oldEntry.getStatus(), oldEntry.getLoadedState(), oldEntry.getRowId(), generatedId, oldEntry.getVersion(), oldEntry.getLockMode(), oldEntry.isExistsInDatabase(), oldEntry.getPersister(), oldEntry.isBeingReplicated());
    }

    public void serialize(ObjectOutputStream oos) throws IOException {
        LOG.trace("Serializing persistence-context");
        oos.writeBoolean(this.defaultReadOnly);
        oos.writeBoolean(this.hasNonReadOnlyEntities);
        Serializer entityKeySerializer = (entry, stream) -> {
            ((EntityKey)entry.getKey()).serialize(stream);
            stream.writeObject(entry.getValue());
        };
        this.writeMapToStream(this.entitiesByKey, oos, "entitiesByKey", entityKeySerializer);
        this.writeMapToStream(this.entitiesByUniqueKey, oos, "entitiesByUniqueKey", (entry, stream) -> {
            ((EntityUniqueKey)entry.getKey()).serialize(stream);
            stream.writeObject(entry.getValue());
        });
        this.writeMapToStream(this.proxiesByKey, oos, "proxiesByKey", entityKeySerializer);
        this.writeMapToStream(this.entitySnapshotsByKey, oos, "entitySnapshotsByKey", entityKeySerializer);
        this.entityEntryContext.serialize(oos);
        this.writeMapToStream(this.collectionsByKey, oos, "collectionsByKey", (entry, stream) -> {
            ((CollectionKey)entry.getKey()).serialize(stream);
            stream.writeObject(entry.getValue());
        });
        this.writeMapToStream(this.collectionEntries, oos, "collectionEntries", (entry, stream) -> {
            stream.writeObject(entry.getKey());
            ((CollectionEntry)entry.getValue()).serialize(stream);
        });
        this.writeMapToStream(this.arrayHolders, oos, "arrayHolders", (entry, stream) -> {
            stream.writeObject(entry.getKey());
            stream.writeObject(entry.getValue());
        });
        this.writeCollectionToStream(this.nullifiableEntityKeys, oos, "nullifiableEntityKey", EntityKey::serialize);
    }

    private <K, V> void writeMapToStream(Map<K, V> map, ObjectOutputStream oos, String keysName, Serializer<Map.Entry<K, V>> serializer) throws IOException {
        if (map == null) {
            oos.writeInt(0);
        } else {
            this.writeCollectionToStream(map.entrySet(), oos, keysName, serializer);
        }
    }

    private <E> void writeCollectionToStream(Collection<E> collection, ObjectOutputStream oos, String keysName, Serializer<E> serializer) throws IOException {
        if (collection == null) {
            oos.writeInt(0);
        } else {
            oos.writeInt(collection.size());
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting serialization of [" + collection.size() + "] " + keysName + " entries");
            }
            for (E entry : collection) {
                serializer.serialize(entry, oos);
            }
        }
    }

    public static StatefulPersistenceContext deserialize(ObjectInputStream ois, SessionImplementor session) throws IOException, ClassNotFoundException {
        LOG.trace("Deserializing persistence-context");
        StatefulPersistenceContext rtn = new StatefulPersistenceContext(session);
        SessionFactoryImplementor sfi = session.getFactory();
        try {
            int i;
            rtn.defaultReadOnly = ois.readBoolean();
            rtn.hasNonReadOnlyEntities = ois.readBoolean();
            int count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] entitiesByKey entries");
            }
            rtn.entitiesByKey = new HashMap(count < 8 ? 8 : count);
            for (i = 0; i < count; ++i) {
                rtn.entitiesByKey.put(EntityKey.deserialize(ois, sfi), ois.readObject());
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] entitiesByUniqueKey entries");
            }
            if (count != 0) {
                rtn.entitiesByUniqueKey = new HashMap(count < 8 ? 8 : count);
                for (i = 0; i < count; ++i) {
                    rtn.entitiesByUniqueKey.put(EntityUniqueKey.deserialize(ois, session), ois.readObject());
                }
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] proxiesByKey entries");
            }
            for (i = 0; i < count; ++i) {
                EntityKey ek = EntityKey.deserialize(ois, sfi);
                Object proxy = ois.readObject();
                if (proxy instanceof HibernateProxy) {
                    ((HibernateProxy)proxy).getHibernateLazyInitializer().setSession(session);
                    rtn.getOrInitializeProxiesByKey().put(ek, proxy);
                    continue;
                }
                if (!LOG.isTraceEnabled()) continue;
                LOG.trace("Encountered pruned proxy");
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] entitySnapshotsByKey entries");
            }
            rtn.entitySnapshotsByKey = new HashMap(count < 8 ? 8 : count);
            for (i = 0; i < count; ++i) {
                rtn.entitySnapshotsByKey.put(EntityKey.deserialize(ois, sfi), ois.readObject());
            }
            rtn.entityEntryContext = EntityEntryContext.deserialize(ois, rtn);
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] collectionsByKey entries");
            }
            rtn.collectionsByKey = new HashMap(count < 8 ? 8 : count);
            for (i = 0; i < count; ++i) {
                rtn.collectionsByKey.put(CollectionKey.deserialize(ois, session), (PersistentCollection)ois.readObject());
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] collectionEntries entries");
            }
            for (i = 0; i < count; ++i) {
                PersistentCollection pc = (PersistentCollection)ois.readObject();
                CollectionEntry ce = CollectionEntry.deserialize(ois, session);
                pc.setCurrentSession(session);
                rtn.getOrInitializeCollectionEntries().put(pc, ce);
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] arrayHolders entries");
            }
            if (count != 0) {
                rtn.arrayHolders = new IdentityHashMap(count < 8 ? 8 : count);
                for (i = 0; i < count; ++i) {
                    rtn.arrayHolders.put(ois.readObject(), (PersistentCollection)ois.readObject());
                }
            }
            count = ois.readInt();
            if (LOG.isTraceEnabled()) {
                LOG.trace("Starting deserialization of [" + count + "] nullifiableEntityKey entries");
            }
            rtn.nullifiableEntityKeys = new HashSet();
            for (i = 0; i < count; ++i) {
                rtn.nullifiableEntityKeys.add(EntityKey.deserialize(ois, sfi));
            }
        }
        catch (HibernateException he) {
            throw new InvalidObjectException(he.getMessage());
        }
        return rtn;
    }

    @Override
    public void addChildParent(Object child, Object parent) {
        if (this.parentsByChild == null) {
            this.parentsByChild = new IdentityHashMap(8);
        }
        this.parentsByChild.put(child, parent);
    }

    @Override
    public void removeChildParent(Object child) {
        if (this.parentsByChild != null) {
            this.parentsByChild.remove(child);
        }
    }

    @Override
    public void registerInsertedKey(EntityPersister persister, Serializable id) {
        if (persister.canWriteToCache()) {
            if (this.insertedKeysMap == null) {
                this.insertedKeysMap = new HashMap();
            }
            String rootEntityName = persister.getRootEntityName();
            HashSet insertedEntityIds = this.insertedKeysMap.computeIfAbsent(rootEntityName, k -> new HashSet());
            insertedEntityIds.add(id);
        }
    }

    @Override
    public boolean wasInsertedDuringTransaction(EntityPersister persister, Serializable id) {
        HashSet<Serializable> insertedEntityIds;
        if (persister.canWriteToCache() && this.insertedKeysMap != null && (insertedEntityIds = this.insertedKeysMap.get(persister.getRootEntityName())) != null) {
            return insertedEntityIds.contains(id);
        }
        return false;
    }

    @Override
    public boolean containsNullifiableEntityKey(Supplier<EntityKey> sek) {
        if (this.nullifiableEntityKeys == null || this.nullifiableEntityKeys.size() == 0) {
            return false;
        }
        EntityKey entityKey = sek.get();
        return this.nullifiableEntityKeys.contains(entityKey);
    }

    @Override
    public void registerNullifiableEntityKey(EntityKey key) {
        if (this.nullifiableEntityKeys == null) {
            this.nullifiableEntityKeys = new HashSet();
        }
        this.nullifiableEntityKeys.add(key);
    }

    @Override
    public boolean isNullifiableEntityKeysEmpty() {
        return this.nullifiableEntityKeys == null || this.nullifiableEntityKeys.size() == 0;
    }

    @Override
    public int getCollectionEntriesSize() {
        return this.collectionEntries == null ? 0 : this.collectionEntries.size();
    }

    @Override
    public CollectionEntry removeCollectionEntry(PersistentCollection collection) {
        if (this.collectionEntries == null) {
            return null;
        }
        return this.collectionEntries.remove(collection);
    }

    @Override
    public void clearCollectionsByKey() {
        if (this.collectionsByKey != null) {
            this.collectionsByKey.clear();
        }
    }

    @Override
    public PersistentCollection addCollectionByKey(CollectionKey collectionKey, PersistentCollection persistentCollection) {
        if (this.collectionsByKey == null) {
            this.collectionsByKey = new HashMap(8);
        }
        PersistentCollection old = this.collectionsByKey.put(collectionKey, persistentCollection);
        return old;
    }

    @Override
    public void removeCollectionByKey(CollectionKey collectionKey) {
        if (this.collectionsByKey != null) {
            this.collectionsByKey.remove(collectionKey);
        }
    }

    private void cleanUpInsertedKeysAfterTransaction() {
        if (this.insertedKeysMap != null) {
            this.insertedKeysMap.clear();
        }
    }

    private NaturalIdXrefDelegate getNaturalIdXrefDelegate() {
        if (this.naturalIdXrefDelegate == null) {
            this.naturalIdXrefDelegate = new NaturalIdXrefDelegate(this);
        }
        return this.naturalIdXrefDelegate;
    }

    @Override
    public PersistenceContext.NaturalIdHelper getNaturalIdHelper() {
        return this.naturalIdHelper;
    }

    private Object[] getNaturalIdValues(Object[] state, EntityPersister persister) {
        int[] naturalIdPropertyIndexes = persister.getNaturalIdentifierProperties();
        Object[] naturalIdValues = new Object[naturalIdPropertyIndexes.length];
        for (int i = 0; i < naturalIdPropertyIndexes.length; ++i) {
            naturalIdValues[i] = state[naturalIdPropertyIndexes[i]];
        }
        return naturalIdValues;
    }

    private static interface Serializer<E> {
        public void serialize(E var1, ObjectOutputStream var2) throws IOException;
    }
}

