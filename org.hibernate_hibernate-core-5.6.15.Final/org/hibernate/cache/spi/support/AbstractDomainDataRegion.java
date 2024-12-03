/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.cache.spi.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.CollectionDataCachingConfig;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.cfg.spi.EntityDataCachingConfig;
import org.hibernate.cache.cfg.spi.NaturalIdDataCachingConfig;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.access.CollectionDataAccess;
import org.hibernate.cache.spi.access.EntityDataAccess;
import org.hibernate.cache.spi.access.NaturalIdDataAccess;
import org.hibernate.cache.spi.support.AbstractRegion;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.metamodel.model.domain.NavigableRole;
import org.jboss.logging.Logger;

public abstract class AbstractDomainDataRegion
extends AbstractRegion
implements DomainDataRegion {
    private static final Logger log = Logger.getLogger(AbstractDomainDataRegion.class);
    private final SessionFactoryImplementor sessionFactory;
    private final CacheKeysFactory effectiveKeysFactory;
    private Map<NavigableRole, EntityDataAccess> entityDataAccessMap;
    private Map<NavigableRole, NaturalIdDataAccess> naturalIdDataAccessMap;
    private Map<NavigableRole, CollectionDataAccess> collectionDataAccessMap;

    public AbstractDomainDataRegion(DomainDataRegionConfig regionConfig, RegionFactory regionFactory, CacheKeysFactory defaultKeysFactory, DomainDataRegionBuildingContext buildingContext) {
        super(regionConfig.getRegionName(), regionFactory);
        this.sessionFactory = buildingContext.getSessionFactory();
        if (defaultKeysFactory == null) {
            defaultKeysFactory = DefaultCacheKeysFactory.INSTANCE;
        }
        this.effectiveKeysFactory = buildingContext.getEnforcedCacheKeysFactory() != null ? buildingContext.getEnforcedCacheKeysFactory() : defaultKeysFactory;
    }

    protected void completeInstantiation(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        log.tracef("DomainDataRegion created [%s]; key-factory = %s", (Object)regionConfig.getRegionName(), (Object)this.effectiveKeysFactory);
        this.entityDataAccessMap = this.generateEntityDataAccessMap(regionConfig);
        this.naturalIdDataAccessMap = this.generateNaturalIdDataAccessMap(regionConfig);
        this.collectionDataAccessMap = this.generateCollectionDataAccessMap(regionConfig);
    }

    public SessionFactoryImplementor getSessionFactory() {
        return this.sessionFactory;
    }

    public CacheKeysFactory getEffectiveKeysFactory() {
        return this.effectiveKeysFactory;
    }

    @Override
    public EntityDataAccess getEntityDataAccess(NavigableRole rootEntityRole) {
        EntityDataAccess access = this.entityDataAccessMap.get(rootEntityRole);
        if (access == null) {
            throw new IllegalArgumentException("Caching was not configured for entity : " + rootEntityRole.getFullPath());
        }
        return access;
    }

    @Override
    public NaturalIdDataAccess getNaturalIdDataAccess(NavigableRole rootEntityRole) {
        NaturalIdDataAccess access = this.naturalIdDataAccessMap.get(rootEntityRole);
        if (access == null) {
            throw new IllegalArgumentException("Caching was not configured for entity natural-id : " + rootEntityRole.getFullPath());
        }
        return access;
    }

    @Override
    public CollectionDataAccess getCollectionDataAccess(NavigableRole collectionRole) {
        CollectionDataAccess access = this.collectionDataAccessMap.get(collectionRole);
        if (access == null) {
            throw new IllegalArgumentException("Caching was not configured for collection : " + collectionRole.getFullPath());
        }
        return access;
    }

    protected abstract EntityDataAccess generateEntityAccess(EntityDataCachingConfig var1);

    protected abstract CollectionDataAccess generateCollectionAccess(CollectionDataCachingConfig var1);

    protected abstract NaturalIdDataAccess generateNaturalIdAccess(NaturalIdDataCachingConfig var1);

    private Map<NavigableRole, EntityDataAccess> generateEntityDataAccessMap(DomainDataRegionConfig regionConfig) {
        List<EntityDataCachingConfig> entityCaching = regionConfig.getEntityCaching();
        if (entityCaching.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<NavigableRole, EntityDataAccess> accessMap = new HashMap<NavigableRole, EntityDataAccess>(entityCaching.size());
        for (EntityDataCachingConfig entityAccessConfig : entityCaching) {
            accessMap.put(entityAccessConfig.getNavigableRole(), this.generateEntityAccess(entityAccessConfig));
        }
        return Collections.unmodifiableMap(accessMap);
    }

    private Map<NavigableRole, NaturalIdDataAccess> generateNaturalIdDataAccessMap(DomainDataRegionConfig regionConfig) {
        List<NaturalIdDataCachingConfig> naturalIdCaching = regionConfig.getNaturalIdCaching();
        if (naturalIdCaching.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<NavigableRole, NaturalIdDataAccess> accessMap = new HashMap<NavigableRole, NaturalIdDataAccess>(naturalIdCaching.size());
        for (NaturalIdDataCachingConfig naturalIdAccessConfig : naturalIdCaching) {
            accessMap.put(naturalIdAccessConfig.getNavigableRole(), this.generateNaturalIdAccess(naturalIdAccessConfig));
        }
        return Collections.unmodifiableMap(accessMap);
    }

    private Map<NavigableRole, CollectionDataAccess> generateCollectionDataAccessMap(DomainDataRegionConfig regionConfig) {
        List<CollectionDataCachingConfig> collectionCaching = regionConfig.getCollectionCaching();
        if (collectionCaching.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<NavigableRole, CollectionDataAccess> accessMap = new HashMap<NavigableRole, CollectionDataAccess>(collectionCaching.size());
        for (CollectionDataCachingConfig cachingConfig : collectionCaching) {
            accessMap.put(cachingConfig.getNavigableRole(), this.generateCollectionAccess(cachingConfig));
        }
        return Collections.unmodifiableMap(accessMap);
    }

    @Override
    public void clear() {
        for (EntityDataAccess entityDataAccess : this.entityDataAccessMap.values()) {
            entityDataAccess.evictAll();
        }
        for (NaturalIdDataAccess naturalIdDataAccess : this.naturalIdDataAccessMap.values()) {
            naturalIdDataAccess.evictAll();
        }
        for (CollectionDataAccess collectionDataAccess : this.collectionDataAccessMap.values()) {
            collectionDataAccess.evictAll();
        }
    }

    protected void releaseDataAccess(EntityDataAccess cacheAccess) {
        if (Destructible.class.isInstance(cacheAccess)) {
            ((Destructible)((Object)cacheAccess)).destroy();
        }
    }

    protected void releaseDataAccess(NaturalIdDataAccess cacheAccess) {
        if (Destructible.class.isInstance(cacheAccess)) {
            ((Destructible)((Object)cacheAccess)).destroy();
        }
    }

    protected void releaseDataAccess(CollectionDataAccess cacheAccess) {
        if (Destructible.class.isInstance(cacheAccess)) {
            ((Destructible)((Object)cacheAccess)).destroy();
        }
    }

    @Override
    public void destroy() throws CacheException {
        for (EntityDataAccess entityDataAccess : this.entityDataAccessMap.values()) {
            this.releaseDataAccess(entityDataAccess);
        }
        for (NaturalIdDataAccess naturalIdDataAccess : this.naturalIdDataAccessMap.values()) {
            this.releaseDataAccess(naturalIdDataAccess);
        }
        for (CollectionDataAccess collectionDataAccess : this.collectionDataAccessMap.values()) {
            this.releaseDataAccess(collectionDataAccess);
        }
    }

    public static interface Destructible {
        public void destroy();
    }
}

