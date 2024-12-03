/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hazelcast.core.HazelcastInstance
 *  com.hazelcast.logging.ILogger
 *  com.hazelcast.logging.Logger
 *  org.hibernate.boot.spi.SessionFactoryOptions
 *  org.hibernate.cache.CacheException
 *  org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.internal.DefaultCacheKeysFactory
 *  org.hibernate.cache.spi.CacheKeysFactory
 *  org.hibernate.cache.spi.DomainDataRegion
 *  org.hibernate.cache.spi.RegionFactory
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 *  org.hibernate.cache.spi.support.RegionFactoryTemplate
 *  org.hibernate.cache.spi.support.StorageAccess
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package com.hazelcast.hibernate;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.hibernate.HazelcastDomainDataRegionImpl;
import com.hazelcast.hibernate.HazelcastStorageAccessImpl;
import com.hazelcast.hibernate.RegionCache;
import com.hazelcast.hibernate.instance.DefaultHazelcastInstanceFactory;
import com.hazelcast.hibernate.instance.IHazelcastInstanceFactory;
import com.hazelcast.hibernate.instance.IHazelcastInstanceLoader;
import com.hazelcast.hibernate.local.CleanupService;
import com.hazelcast.hibernate.local.LocalRegionCache;
import com.hazelcast.logging.ILogger;
import com.hazelcast.logging.Logger;
import java.util.Map;
import java.util.Properties;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.RegionFactory;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public abstract class AbstractHazelcastCacheRegionFactory
extends RegionFactoryTemplate {
    protected CleanupService cleanupService;
    protected HazelcastInstance instance;
    private final CacheKeysFactory cacheKeysFactory;
    private final ILogger log = Logger.getLogger(((Object)((Object)this)).getClass());
    private IHazelcastInstanceLoader instanceLoader;

    public AbstractHazelcastCacheRegionFactory() {
        this((CacheKeysFactory)DefaultCacheKeysFactory.INSTANCE);
    }

    public AbstractHazelcastCacheRegionFactory(CacheKeysFactory cacheKeysFactory) {
        this.cacheKeysFactory = cacheKeysFactory;
    }

    public AbstractHazelcastCacheRegionFactory(HazelcastInstance instance) {
        this.instance = instance;
        this.cacheKeysFactory = DefaultCacheKeysFactory.INSTANCE;
    }

    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new HazelcastDomainDataRegionImpl(regionConfig, this, this.createDomainDataStorageAccess(regionConfig, buildingContext), this.cacheKeysFactory, buildingContext);
    }

    public HazelcastInstance getHazelcastInstance() {
        return this.instance;
    }

    protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new HazelcastStorageAccessImpl(this.createRegionCache(regionConfig.getRegionName(), buildingContext.getSessionFactory(), regionConfig));
    }

    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        LocalRegionCache regionCache = new LocalRegionCache((RegionFactory)this, regionName, this.instance, null, false);
        this.cleanupService.registerCache(regionCache);
        return new HazelcastStorageAccessImpl(regionCache);
    }

    protected abstract RegionCache createRegionCache(String var1, SessionFactoryImplementor var2, DomainDataRegionConfig var3);

    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        return new HazelcastStorageAccessImpl(this.createTimestampsRegionCache(regionName, sessionFactory));
    }

    protected abstract RegionCache createTimestampsRegionCache(String var1, SessionFactoryImplementor var2);

    protected CacheKeysFactory getImplicitCacheKeysFactory() {
        return this.cacheKeysFactory;
    }

    protected boolean isStarted() {
        return super.isStarted() && this.instance.getLifecycleService().isRunning();
    }

    protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {
        this.log.info("Starting up " + ((Object)((Object)this)).getClass().getSimpleName());
        if (this.instance == null || !this.instance.getLifecycleService().isRunning()) {
            String defaultFactory = DefaultHazelcastInstanceFactory.class.getName();
            String factoryName = (String)configValues.get("hibernate.cache.hazelcast.factory");
            if (factoryName == null) {
                factoryName = defaultFactory;
            }
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try {
                Class<?> factory = Class.forName(factoryName, true, cl);
                this.instanceLoader = ((IHazelcastInstanceFactory)factory.newInstance()).createInstanceLoader(this.toProperties(configValues));
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                throw new CacheException("Failed to set up hazelcast instance factory", (Throwable)e);
            }
            this.instance = this.instanceLoader.loadInstance();
        }
        this.cleanupService = new CleanupService(this.instance.getName());
    }

    protected void releaseFromUse() {
        if (this.instanceLoader != null) {
            this.log.info("Shutting down " + ((Object)((Object)this)).getClass().getSimpleName());
            this.instanceLoader.unloadInstance();
            this.instance = null;
            this.instanceLoader = null;
        }
        this.cleanupService.stop();
    }

    private Properties toProperties(Map configValues) {
        Properties properties = new Properties();
        properties.putAll((Map<?, ?>)configValues);
        return properties;
    }
}

