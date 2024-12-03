/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.PersistenceException
 */
package org.hibernate.cache.internal;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.persistence.PersistenceException;
import org.hibernate.Cache;
import org.hibernate.HibernateException;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.internal.QueryResultsCacheImpl;
import org.hibernate.cache.internal.TimestampsCacheDisabledImpl;
import org.hibernate.cache.spi.CacheImplementor;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.QueryResultsCache;
import org.hibernate.cache.spi.QueryResultsRegion;
import org.hibernate.cache.spi.Region;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.TimestampsCache;
import org.hibernate.cache.spi.TimestampsRegion;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;
import org.hibernate.internal.util.StringHelper;
import org.hibernate.internal.util.collections.ArrayHelper;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.hibernate.persister.collection.CollectionPersister;
import org.hibernate.persister.entity.EntityPersister;
import org.hibernate.pretty.MessageHelper;

public class EnabledCaching
implements CacheImplementor,
DomainDataRegionBuildingContext {
    private static final CoreMessageLogger LOG = CoreLogging.messageLogger(EnabledCaching.class);
    private final SessionFactoryImplementor sessionFactory;
    private final RegionFactory regionFactory;
    private final Map<String, Region> regionsByName = new ConcurrentHashMap<String, Region>();
    private final Map<String, QueryResultsRegion> queryResultsRegionsByDuplicateName = new ConcurrentHashMap<String, QueryResultsRegion>();
    private final Map<NavigableRole, EntityDataAccess> entityAccessMap = new ConcurrentHashMap<NavigableRole, EntityDataAccess>();
    private final Map<NavigableRole, NaturalIdDataAccess> naturalIdAccessMap = new ConcurrentHashMap<NavigableRole, NaturalIdDataAccess>();
    private final Map<NavigableRole, CollectionDataAccess> collectionAccessMap = new ConcurrentHashMap<NavigableRole, CollectionDataAccess>();
    private final TimestampsCache timestampsCache;
    private final QueryResultsCache defaultQueryResultsCache;
    private final Map<String, QueryResultsCache> namedQueryResultsCacheMap = new ConcurrentHashMap<String, QueryResultsCache>();
    private final Set<String> legacySecondLevelCacheNames = new LinkedHashSet<String>();
    private final Map<String, Set<NaturalIdDataAccess>> legacyNaturalIdAccessesForRegion = new ConcurrentHashMap<String, Set<NaturalIdDataAccess>>();

    public EnabledCaching(SessionFactoryImplementor sessionFactory) {
        this.sessionFactory = sessionFactory;
        this.regionFactory = this.getSessionFactory().getSessionFactoryOptions().getServiceRegistry().getService(RegionFactory.class);
        this.regionFactory.start(sessionFactory.getSessionFactoryOptions(), sessionFactory.getProperties());
        if (this.getSessionFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
            TimestampsRegion timestampsRegion = this.regionFactory.buildTimestampsRegion("default-update-timestamps-region", sessionFactory);
            this.timestampsCache = sessionFactory.getSessionFactoryOptions().getTimestampsCacheFactory().buildTimestampsCache(this, timestampsRegion);
            this.legacySecondLevelCacheNames.add(timestampsRegion.getName());
            QueryResultsRegion queryResultsRegion = this.regionFactory.buildQueryResultsRegion("default-query-results-region", sessionFactory);
            this.regionsByName.put(queryResultsRegion.getName(), queryResultsRegion);
            this.defaultQueryResultsCache = new QueryResultsCacheImpl(queryResultsRegion, this.timestampsCache);
        } else {
            this.timestampsCache = new TimestampsCacheDisabledImpl();
            this.defaultQueryResultsCache = null;
        }
    }

    @Override
    public void prime(Set<DomainDataRegionConfig> cacheRegionConfigs) {
        for (DomainDataRegionConfig regionConfig : cacheRegionConfigs) {
            DomainDataRegion region = this.getRegionFactory().buildDomainDataRegion(regionConfig, this);
            this.regionsByName.put(region.getName(), region);
            if (!Objects.equals(region.getName(), regionConfig.getRegionName())) {
                throw new HibernateException(String.format(Locale.ROOT, "Region [%s] returned from RegionFactory [%s] was named differently than requested name.  Expecting `%s`, but found `%s`", region, this.getRegionFactory().getClass().getName(), regionConfig.getRegionName(), region.getName()));
            }
            for (EntityDataCachingConfig entityDataCachingConfig : regionConfig.getEntityCaching()) {
                EntityDataAccess entityDataAccess = this.entityAccessMap.put(entityDataCachingConfig.getNavigableRole(), region.getEntityDataAccess(entityDataCachingConfig.getNavigableRole()));
                this.legacySecondLevelCacheNames.add(StringHelper.qualifyConditionally(this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix(), region.getName()));
            }
            if (regionConfig.getNaturalIdCaching().isEmpty()) {
                this.legacyNaturalIdAccessesForRegion.put(region.getName(), Collections.emptySet());
            } else {
                HashSet<NaturalIdDataAccess> accesses = new HashSet<NaturalIdDataAccess>();
                for (NaturalIdDataCachingConfig naturalIdAccessConfig : regionConfig.getNaturalIdCaching()) {
                    NaturalIdDataAccess naturalIdDataAccess = this.naturalIdAccessMap.put(naturalIdAccessConfig.getNavigableRole(), region.getNaturalIdDataAccess(naturalIdAccessConfig.getNavigableRole()));
                    accesses.add(naturalIdDataAccess);
                }
                this.legacyNaturalIdAccessesForRegion.put(region.getName(), accesses);
            }
            for (CollectionDataCachingConfig collectionDataCachingConfig : regionConfig.getCollectionCaching()) {
                CollectionDataAccess collectionDataAccess = this.collectionAccessMap.put(collectionDataCachingConfig.getNavigableRole(), region.getCollectionDataAccess(collectionDataCachingConfig.getNavigableRole()));
                this.legacySecondLevelCacheNames.add(StringHelper.qualifyConditionally(this.getSessionFactory().getSessionFactoryOptions().getCacheRegionPrefix(), region.getName()));
            }
        }
    }

    @Override
    public CacheKeysFactory getEnforcedCacheKeysFactory() {
        return null;
    }

    @Override
    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    @Override
    public RegionFactory getRegionFactory() {
        return this.regionFactory;
    }

    @Override
    public TimestampsCache getTimestampsCache() {
        return this.timestampsCache;
    }

    @Override
    public Region getRegion(String regionName) {
        return this.regionsByName.get(regionName);
    }

    @Override
    public boolean containsEntity(Class entityClass, Serializable identifier) {
        return this.containsEntity(entityClass.getName(), identifier);
    }

    @Override
    public boolean containsEntity(String entityName, Serializable identifier) {
        EntityPersister entityDescriptor = this.sessionFactory.getMetamodel().entityPersister(entityName);
        EntityDataAccess cacheAccess = entityDescriptor.getCacheAccessStrategy();
        if (cacheAccess == null) {
            return false;
        }
        Object key = cacheAccess.generateCacheKey(identifier, entityDescriptor, this.sessionFactory, null);
        return cacheAccess.contains(key);
    }

    @Override
    public void evictEntityData(Class entityClass, Serializable identifier) {
        this.evictEntityData(entityClass.getName(), identifier);
    }

    @Override
    public void evictEntityData(String entityName, Serializable identifier) {
        EntityPersister entityDescriptor = this.sessionFactory.getMetamodel().entityPersister(entityName);
        EntityDataAccess cacheAccess = entityDescriptor.getCacheAccessStrategy();
        if (cacheAccess == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting second-level cache: %s", MessageHelper.infoString(entityDescriptor, identifier, this.sessionFactory));
        }
        Object key = cacheAccess.generateCacheKey(identifier, entityDescriptor, this.sessionFactory, null);
        cacheAccess.evict(key);
    }

    @Override
    public void evictEntityData(Class entityClass) {
        this.evictEntityData(entityClass.getName());
    }

    @Override
    public void evictEntityData(String entityName) {
        this.evictEntityData(this.getSessionFactory().getMetamodel().entityPersister(entityName));
    }

    protected void evictEntityData(EntityPersister entityDescriptor) {
        EntityPersister rootEntityDescriptor = entityDescriptor;
        if (entityDescriptor.isInherited() && !entityDescriptor.getEntityName().equals(entityDescriptor.getRootEntityName())) {
            rootEntityDescriptor = this.getSessionFactory().getMetamodel().entityPersister(entityDescriptor.getRootEntityName());
        }
        this.evictEntityData(rootEntityDescriptor.getNavigableRole(), rootEntityDescriptor.getCacheAccessStrategy());
    }

    private void evictEntityData(NavigableRole navigableRole, EntityDataAccess cacheAccess) {
        if (cacheAccess == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting entity cache: %s", navigableRole.getFullPath());
        }
        cacheAccess.evictAll();
    }

    @Override
    public void evictEntityData() {
        this.sessionFactory.getMetamodel().entityPersisters().values().forEach(this::evictEntityData);
    }

    @Override
    public void evictNaturalIdData(Class entityClass) {
        this.evictNaturalIdData(entityClass.getName());
    }

    @Override
    public void evictNaturalIdData(String entityName) {
        this.evictNaturalIdData(this.sessionFactory.getMetamodel().entityPersister(entityName));
    }

    private void evictNaturalIdData(EntityPersister rootEntityDescriptor) {
        this.evictNaturalIdData(rootEntityDescriptor.getNavigableRole(), rootEntityDescriptor.getNaturalIdCacheAccessStrategy());
    }

    @Override
    public void evictNaturalIdData() {
        this.naturalIdAccessMap.forEach(this::evictNaturalIdData);
    }

    private void evictNaturalIdData(NavigableRole rootEntityRole, NaturalIdDataAccess cacheAccess) {
        if (cacheAccess == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting natural-id cache: %s", rootEntityRole.getFullPath());
        }
        cacheAccess.evictAll();
    }

    @Override
    public boolean containsCollection(String role, Serializable ownerIdentifier) {
        CollectionPersister collectionDescriptor = this.sessionFactory.getMetamodel().collectionPersister(role);
        CollectionDataAccess cacheAccess = collectionDescriptor.getCacheAccessStrategy();
        if (cacheAccess == null) {
            return false;
        }
        Object key = cacheAccess.generateCacheKey(ownerIdentifier, collectionDescriptor, this.sessionFactory, null);
        return cacheAccess.contains(key);
    }

    @Override
    public void evictCollectionData(String role, Serializable ownerIdentifier) {
        CollectionPersister collectionDescriptor = this.sessionFactory.getMetamodel().collectionPersister(role);
        CollectionDataAccess cacheAccess = collectionDescriptor.getCacheAccessStrategy();
        if (cacheAccess == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting second-level cache: %s", MessageHelper.collectionInfoString(collectionDescriptor, ownerIdentifier, this.sessionFactory));
        }
        Object key = cacheAccess.generateCacheKey(ownerIdentifier, collectionDescriptor, this.sessionFactory, null);
        cacheAccess.evict(key);
    }

    @Override
    public void evictCollectionData(String role) {
        CollectionPersister collectionDescriptor = this.sessionFactory.getMetamodel().collectionPersister(role);
        this.evictCollectionData(collectionDescriptor);
    }

    private void evictCollectionData(CollectionPersister collectionDescriptor) {
        this.evictCollectionData(collectionDescriptor.getNavigableRole(), collectionDescriptor.getCacheAccessStrategy());
    }

    private void evictCollectionData(NavigableRole navigableRole, CollectionDataAccess cacheAccess) {
        if (cacheAccess == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting second-level cache: %s", navigableRole.getFullPath());
        }
        cacheAccess.evictAll();
    }

    @Override
    public void evictCollectionData() {
        this.collectionAccessMap.forEach(this::evictCollectionData);
    }

    @Override
    public boolean containsQuery(String regionName) {
        QueryResultsCache cache = this.getQueryResultsCacheStrictly(regionName);
        return cache != null;
    }

    @Override
    public void evictDefaultQueryRegion() {
        this.evictQueryResultRegion(this.defaultQueryResultsCache);
    }

    @Override
    public void evictQueryRegion(String regionName) {
        QueryResultsCache cache = this.getQueryResultsCache(regionName);
        if (cache == null) {
            return;
        }
        this.evictQueryResultRegion(cache);
    }

    private void evictQueryResultRegion(QueryResultsCache cache) {
        if (cache == null) {
            return;
        }
        if (LOG.isDebugEnabled()) {
            LOG.debugf("Evicting query cache, region: %s", cache.getRegion().getName());
        }
        cache.clear();
    }

    @Override
    public void evictQueryRegions() {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Evicting cache of all query regions.");
        }
        this.evictQueryResultRegion(this.defaultQueryResultsCache);
        for (QueryResultsCache cache : this.namedQueryResultsCacheMap.values()) {
            this.evictQueryResultRegion(cache);
        }
    }

    @Override
    public QueryResultsCache getDefaultQueryResultsCache() {
        return this.defaultQueryResultsCache;
    }

    @Override
    public QueryResultsCache getQueryResultsCache(String regionName) throws HibernateException {
        if (!this.getSessionFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
            return null;
        }
        if (regionName == null || regionName.equals(this.getDefaultQueryResultsCache().getRegion().getName())) {
            return this.getDefaultQueryResultsCache();
        }
        QueryResultsCache existing = this.namedQueryResultsCacheMap.get(regionName);
        if (existing != null) {
            return existing;
        }
        return this.makeQueryResultsRegionAccess(regionName);
    }

    @Override
    public QueryResultsCache getQueryResultsCacheStrictly(String regionName) {
        if (!this.getSessionFactory().getSessionFactoryOptions().isQueryCacheEnabled()) {
            return null;
        }
        if (regionName == null || regionName.equals(this.getDefaultQueryResultsCache().getRegion().getName())) {
            return this.getDefaultQueryResultsCache();
        }
        return this.namedQueryResultsCacheMap.get(regionName);
    }

    protected QueryResultsCache makeQueryResultsRegionAccess(String regionName) {
        Region region = this.regionsByName.computeIfAbsent(regionName, this::makeQueryResultsRegion);
        QueryResultsRegion queryResultsRegion = QueryResultsRegion.class.isInstance(region) ? (QueryResultsRegion)region : this.queryResultsRegionsByDuplicateName.computeIfAbsent(regionName, this::makeQueryResultsRegion);
        QueryResultsCacheImpl regionAccess = new QueryResultsCacheImpl(queryResultsRegion, this.timestampsCache);
        this.namedQueryResultsCacheMap.put(regionName, regionAccess);
        this.legacySecondLevelCacheNames.add(regionName);
        return regionAccess;
    }

    protected QueryResultsRegion makeQueryResultsRegion(String regionName) {
        return this.regionFactory.buildQueryResultsRegion(regionName, this.getSessionFactory());
    }

    @Override
    public Set<String> getCacheRegionNames() {
        return this.regionsByName.keySet();
    }

    @Override
    public void evictRegion(String regionName) {
        this.getRegion(regionName).clear();
        QueryResultsRegion queryResultsRegionWithDuplicateName = this.queryResultsRegionsByDuplicateName.get(regionName);
        if (queryResultsRegionWithDuplicateName != null) {
            queryResultsRegionWithDuplicateName.clear();
        }
    }

    public <T> T unwrap(Class<T> cls) {
        if (Cache.class.isAssignableFrom(cls)) {
            return (T)this;
        }
        if (RegionFactory.class.isAssignableFrom(cls)) {
            return (T)this.regionFactory;
        }
        throw new PersistenceException("Hibernate cannot unwrap Cache as " + cls.getName());
    }

    @Override
    public void close() {
        for (Region region : this.regionsByName.values()) {
            region.destroy();
        }
        for (Region region : this.queryResultsRegionsByDuplicateName.values()) {
            region.destroy();
        }
    }

    public boolean contains(Class cls, Object primaryKey) {
        return this.containsEntity(cls, (Serializable)primaryKey);
    }

    public void evict(Class cls, Object primaryKey) {
        this.evictEntityData(cls, (Serializable)primaryKey);
    }

    public void evict(Class cls) {
        this.evictEntityData(cls);
    }

    @Override
    public EntityDataAccess getEntityRegionAccess(NavigableRole rootEntityName) {
        return this.entityAccessMap.get(rootEntityName);
    }

    @Override
    public NaturalIdDataAccess getNaturalIdCacheRegionAccessStrategy(NavigableRole rootEntityName) {
        return this.naturalIdAccessMap.get(rootEntityName);
    }

    @Override
    public CollectionDataAccess getCollectionRegionAccess(NavigableRole collectionRole) {
        return this.collectionAccessMap.get(collectionRole);
    }

    @Override
    public String[] getSecondLevelCacheRegionNames() {
        return ArrayHelper.toStringArray(this.legacySecondLevelCacheNames);
    }

    @Override
    public Set<NaturalIdDataAccess> getNaturalIdAccessesInRegion(String regionName) {
        return this.legacyNaturalIdAccessesForRegion.get(regionName);
    }
}

