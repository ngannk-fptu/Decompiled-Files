/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.cache.Cache
 *  javax.cache.CacheManager
 *  javax.cache.Caching
 *  javax.cache.configuration.Configuration
 *  javax.cache.configuration.MutableConfiguration
 *  javax.cache.spi.CachingProvider
 *  org.hibernate.boot.registry.classloading.spi.ClassLoaderService
 *  org.hibernate.boot.spi.SessionFactoryOptions
 *  org.hibernate.cache.CacheException
 *  org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext
 *  org.hibernate.cache.cfg.spi.DomainDataRegionConfig
 *  org.hibernate.cache.internal.DefaultCacheKeysFactory
 *  org.hibernate.cache.spi.CacheKeysFactory
 *  org.hibernate.cache.spi.DomainDataRegion
 *  org.hibernate.cache.spi.SecondLevelCacheLogger
 *  org.hibernate.cache.spi.support.DomainDataStorageAccess
 *  org.hibernate.cache.spi.support.RegionFactoryTemplate
 *  org.hibernate.cache.spi.support.RegionNameQualifier
 *  org.hibernate.cache.spi.support.StorageAccess
 *  org.hibernate.engine.spi.SessionFactoryImplementor
 */
package org.hibernate.cache.jcache.internal;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.spi.CachingProvider;
import org.hibernate.boot.registry.classloading.spi.ClassLoaderService;
import org.hibernate.boot.spi.SessionFactoryOptions;
import org.hibernate.cache.CacheException;
import org.hibernate.cache.cfg.spi.DomainDataRegionBuildingContext;
import org.hibernate.cache.cfg.spi.DomainDataRegionConfig;
import org.hibernate.cache.internal.DefaultCacheKeysFactory;
import org.hibernate.cache.jcache.MissingCacheStrategy;
import org.hibernate.cache.jcache.internal.JCacheAccessImpl;
import org.hibernate.cache.jcache.internal.JCacheDomainDataRegionImpl;
import org.hibernate.cache.spi.CacheKeysFactory;
import org.hibernate.cache.spi.DomainDataRegion;
import org.hibernate.cache.spi.SecondLevelCacheLogger;
import org.hibernate.cache.spi.support.DomainDataStorageAccess;
import org.hibernate.cache.spi.support.RegionFactoryTemplate;
import org.hibernate.cache.spi.support.RegionNameQualifier;
import org.hibernate.cache.spi.support.StorageAccess;
import org.hibernate.engine.spi.SessionFactoryImplementor;

public class JCacheRegionFactory
extends RegionFactoryTemplate {
    private final CacheKeysFactory cacheKeysFactory;
    private volatile CacheManager cacheManager;
    private volatile MissingCacheStrategy missingCacheStrategy;

    public JCacheRegionFactory() {
        this((CacheKeysFactory)DefaultCacheKeysFactory.INSTANCE);
    }

    public JCacheRegionFactory(CacheKeysFactory cacheKeysFactory) {
        this.cacheKeysFactory = cacheKeysFactory;
    }

    public CacheManager getCacheManager() {
        return this.cacheManager;
    }

    protected CacheKeysFactory getImplicitCacheKeysFactory() {
        return this.cacheKeysFactory;
    }

    public DomainDataRegion buildDomainDataRegion(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new JCacheDomainDataRegionImpl(regionConfig, this, this.createDomainDataStorageAccess(regionConfig, buildingContext), this.cacheKeysFactory, buildingContext);
    }

    protected DomainDataStorageAccess createDomainDataStorageAccess(DomainDataRegionConfig regionConfig, DomainDataRegionBuildingContext buildingContext) {
        return new JCacheAccessImpl(this.getOrCreateCache(regionConfig.getRegionName(), buildingContext.getSessionFactory()));
    }

    protected Cache<Object, Object> getOrCreateCache(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        this.verifyStarted();
        assert (!RegionNameQualifier.INSTANCE.isQualified(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions()));
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        Cache cache = this.cacheManager.getCache(qualifiedRegionName);
        if (cache == null) {
            return this.createCache(qualifiedRegionName);
        }
        return cache;
    }

    protected Cache<Object, Object> createCache(String regionName) {
        switch (this.missingCacheStrategy) {
            case CREATE_WARN: {
                SecondLevelCacheLogger.INSTANCE.missingCacheCreated(regionName, "hibernate.javax.cache.missing_cache_strategy", MissingCacheStrategy.CREATE.getExternalRepresentation());
                return this.cacheManager.createCache(regionName, (Configuration)new MutableConfiguration());
            }
            case CREATE: {
                return this.cacheManager.createCache(regionName, (Configuration)new MutableConfiguration());
            }
            case FAIL: {
                throw new CacheException("On-the-fly creation of JCache Cache objects is not supported [" + regionName + "]");
            }
        }
        throw new IllegalStateException("Unsupported missing cache strategy: " + (Object)((Object)this.missingCacheStrategy));
    }

    protected boolean cacheExists(String unqualifiedRegionName, SessionFactoryImplementor sessionFactory) {
        String qualifiedRegionName = RegionNameQualifier.INSTANCE.qualify(unqualifiedRegionName, sessionFactory.getSessionFactoryOptions());
        return this.cacheManager.getCache(qualifiedRegionName) != null;
    }

    protected StorageAccess createQueryResultsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        String defaultedRegionName = this.defaultRegionName(regionName, sessionFactory, "default-query-results-region", LEGACY_QUERY_RESULTS_REGION_UNQUALIFIED_NAMES);
        return new JCacheAccessImpl(this.getOrCreateCache(defaultedRegionName, sessionFactory));
    }

    protected StorageAccess createTimestampsRegionStorageAccess(String regionName, SessionFactoryImplementor sessionFactory) {
        String defaultedRegionName = this.defaultRegionName(regionName, sessionFactory, "default-update-timestamps-region", LEGACY_UPDATE_TIMESTAMPS_REGION_UNQUALIFIED_NAMES);
        return new JCacheAccessImpl(this.getOrCreateCache(defaultedRegionName, sessionFactory));
    }

    protected final String defaultRegionName(String regionName, SessionFactoryImplementor sessionFactory, String defaultRegionName, List<String> legacyDefaultRegionNames) {
        if (defaultRegionName.equals(regionName) && !this.cacheExists(regionName, sessionFactory)) {
            for (String legacyDefaultRegionName : legacyDefaultRegionNames) {
                if (!this.cacheExists(legacyDefaultRegionName, sessionFactory)) continue;
                SecondLevelCacheLogger.INSTANCE.usingLegacyCacheName(defaultRegionName, legacyDefaultRegionName);
                return legacyDefaultRegionName;
            }
        }
        return regionName;
    }

    protected boolean isStarted() {
        return super.isStarted() && this.cacheManager != null;
    }

    protected void prepareForUse(SessionFactoryOptions settings, Map configValues) {
        this.cacheManager = this.resolveCacheManager(settings, configValues);
        if (this.cacheManager == null) {
            throw new CacheException("Could not locate/create CacheManager");
        }
        this.missingCacheStrategy = MissingCacheStrategy.interpretSetting(this.getProp(configValues, "hibernate.javax.cache.missing_cache_strategy"));
    }

    protected CacheManager resolveCacheManager(SessionFactoryOptions settings, Map properties) {
        Object explicitCacheManager = properties.get("hibernate.javax.cache.cache_manager");
        if (explicitCacheManager != null) {
            return this.useExplicitCacheManager(settings, explicitCacheManager);
        }
        CachingProvider cachingProvider = this.getCachingProvider(properties);
        URI cacheManagerUri = this.getUri(settings, properties);
        CacheManager cacheManager = cacheManagerUri != null ? cachingProvider.getCacheManager(cacheManagerUri, this.getClassLoader(cachingProvider)) : cachingProvider.getCacheManager(cachingProvider.getDefaultURI(), this.getClassLoader(cachingProvider));
        return cacheManager;
    }

    protected ClassLoader getClassLoader(CachingProvider cachingProvider) {
        return cachingProvider.getDefaultClassLoader();
    }

    protected URI getUri(SessionFactoryOptions settings, Map properties) {
        String cacheManagerUri = this.getProp(properties, "hibernate.javax.cache.uri");
        if (cacheManagerUri == null) {
            return null;
        }
        URL url = ((ClassLoaderService)settings.getServiceRegistry().getService(ClassLoaderService.class)).locateResource(cacheManagerUri);
        if (url == null) {
            throw new CacheException("Couldn't load URI from " + cacheManagerUri);
        }
        try {
            return url.toURI();
        }
        catch (URISyntaxException e) {
            throw new CacheException("Couldn't load URI from " + cacheManagerUri, (Throwable)e);
        }
    }

    private String getProp(Map properties, String prop) {
        return properties != null ? (String)properties.get(prop) : null;
    }

    protected CachingProvider getCachingProvider(Map properties) {
        String provider = this.getProp(properties, "hibernate.javax.cache.provider");
        CachingProvider cachingProvider = provider != null ? Caching.getCachingProvider((String)provider) : Caching.getCachingProvider();
        return cachingProvider;
    }

    private CacheManager useExplicitCacheManager(SessionFactoryOptions settings, Object setting) {
        if (setting instanceof CacheManager) {
            return (CacheManager)setting;
        }
        Class cacheManagerClass = setting instanceof Class ? (Class)setting : ((ClassLoaderService)settings.getServiceRegistry().getService(ClassLoaderService.class)).classForName(setting.toString());
        try {
            return (CacheManager)cacheManagerClass.newInstance();
        }
        catch (IllegalAccessException | InstantiationException e) {
            throw new CacheException("Could not use explicit CacheManager : " + setting);
        }
    }

    protected void releaseFromUse() {
        try {
            this.cacheManager.close();
        }
        finally {
            this.cacheManager = null;
        }
    }
}

