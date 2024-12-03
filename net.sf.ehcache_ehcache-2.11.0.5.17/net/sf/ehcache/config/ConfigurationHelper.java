/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;
import net.sf.ehcache.config.FactoryConfiguration;
import net.sf.ehcache.config.InvalidConfigurationException;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.SearchAttribute;
import net.sf.ehcache.constructs.CacheDecoratorFactory;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.event.CacheManagerEventListenerFactory;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandler;
import net.sf.ehcache.exceptionhandler.CacheExceptionHandlerFactory;
import net.sf.ehcache.exceptionhandler.ExceptionHandlingDynamicCacheProxy;
import net.sf.ehcache.util.ClassLoaderUtil;
import net.sf.ehcache.util.PropertyUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ConfigurationHelper {
    private static final Logger LOG = LoggerFactory.getLogger((String)ConfigurationHelper.class.getName());
    private final Configuration configuration;
    private final ClassLoader loader;

    public ConfigurationHelper(CacheManager cacheManager, Configuration configuration) {
        if (cacheManager == null || configuration == null) {
            throw new IllegalArgumentException("Cannot have null parameters");
        }
        this.configuration = configuration;
        this.loader = configuration.getClassLoader();
    }

    public static CacheExceptionHandler createCacheExceptionHandler(CacheConfiguration.CacheExceptionHandlerFactoryConfiguration factoryConfiguration, ClassLoader loader) throws CacheException {
        String className = null;
        CacheExceptionHandler cacheExceptionHandler = null;
        if (factoryConfiguration != null) {
            className = factoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className != null && className.length() != 0) {
            CacheExceptionHandlerFactory factory = (CacheExceptionHandlerFactory)ClassLoaderUtil.createNewInstance(loader, className);
            Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
            return factory.createExceptionHandler(properties);
        }
        LOG.debug("No CacheExceptionHandlerFactory class specified. Skipping...");
        return cacheExceptionHandler;
    }

    public final CacheManagerEventListener createCacheManagerEventListener(CacheManager cacheManager) throws CacheException {
        String className = null;
        FactoryConfiguration cacheManagerEventListenerFactoryConfiguration = this.configuration.getCacheManagerEventListenerFactoryConfiguration();
        if (cacheManagerEventListenerFactoryConfiguration != null) {
            className = cacheManagerEventListenerFactoryConfiguration.getFullyQualifiedClassPath();
        }
        if (className == null || className.length() == 0) {
            LOG.debug("No CacheManagerEventListenerFactory class specified. Skipping...");
            return null;
        }
        CacheManagerEventListenerFactory factory = (CacheManagerEventListenerFactory)ClassLoaderUtil.createNewInstance(this.loader, className);
        Properties properties = PropertyUtil.parseProperties(cacheManagerEventListenerFactoryConfiguration.properties, cacheManagerEventListenerFactoryConfiguration.getPropertySeparator());
        return factory.createCacheManagerEventListener(cacheManager, properties);
    }

    public final String getDiskStorePath() {
        DiskStoreConfiguration diskStoreConfiguration = this.configuration.getDiskStoreConfiguration();
        if (diskStoreConfiguration == null) {
            return null;
        }
        return diskStoreConfiguration.getPath();
    }

    public final Ehcache createDefaultCache() throws CacheException {
        CacheConfiguration cacheConfiguration = this.configuration.getDefaultCacheConfiguration();
        if (cacheConfiguration == null) {
            return null;
        }
        cacheConfiguration.name = "default";
        return this.createCache(cacheConfiguration);
    }

    public final Set createCaches() {
        HashSet<Ehcache> caches = new HashSet<Ehcache>();
        Set<Map.Entry<String, CacheConfiguration>> cacheConfigurations = this.configuration.getCacheConfigurations().entrySet();
        for (Map.Entry<String, CacheConfiguration> entry : cacheConfigurations) {
            CacheConfiguration cacheConfiguration = entry.getValue();
            Ehcache cache = this.createCache(cacheConfiguration);
            caches.add(cache);
        }
        return caches;
    }

    public final int numberOfCachesThatUseDiskStorage() {
        int count = 0;
        Set<Map.Entry<String, CacheConfiguration>> cacheConfigurations = this.configuration.getCacheConfigurations().entrySet();
        for (CacheConfiguration cacheConfig : this.configuration.getCacheConfigurations().values()) {
            if (cacheConfig.isOverflowToDisk() || cacheConfig.isDiskPersistent() || cacheConfig.isOverflowToOffHeap() && cacheConfig.isSearchable()) {
                ++count;
                continue;
            }
            PersistenceConfiguration persistence = cacheConfig.getPersistenceConfiguration();
            if (persistence == null) continue;
            switch (persistence.getStrategy()) {
                case LOCALTEMPSWAP: 
                case LOCALRESTARTABLE: {
                    ++count;
                    break;
                }
            }
        }
        return count;
    }

    final Ehcache createCacheFromName(String name) {
        CacheConfiguration cacheConfiguration = null;
        Set<Map.Entry<String, CacheConfiguration>> cacheConfigurations = this.configuration.getCacheConfigurations().entrySet();
        for (Map.Entry<String, CacheConfiguration> entry : cacheConfigurations) {
            CacheConfiguration cacheConfigurationCandidate = entry.getValue();
            if (!cacheConfigurationCandidate.name.equals(name)) continue;
            cacheConfiguration = cacheConfigurationCandidate;
            break;
        }
        if (cacheConfiguration == null) {
            return null;
        }
        return this.createCache(cacheConfiguration);
    }

    final Ehcache createCache(CacheConfiguration cacheConfiguration) {
        CacheConfiguration configClone = cacheConfiguration.clone();
        configClone.setClassLoader(this.configuration.getClassLoader());
        Ehcache cache = new Cache(configClone, null, null);
        cache = this.applyCacheExceptionHandler(configClone, cache);
        return cache;
    }

    private Ehcache applyCacheExceptionHandler(CacheConfiguration cacheConfiguration, Ehcache cache) {
        CacheExceptionHandler cacheExceptionHandler = ConfigurationHelper.createCacheExceptionHandler(cacheConfiguration.getCacheExceptionHandlerFactoryConfiguration(), this.loader);
        cache.setCacheExceptionHandler(cacheExceptionHandler);
        if (cache.getCacheExceptionHandler() != null) {
            return ExceptionHandlingDynamicCacheProxy.createProxy(cache);
        }
        return cache;
    }

    public List<Ehcache> createCacheDecorators(Ehcache cache) {
        CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
        if (cacheConfiguration == null) {
            return ConfigurationHelper.createDefaultCacheDecorators(cache, this.configuration.getDefaultCacheConfiguration(), this.loader);
        }
        List<CacheConfiguration.CacheDecoratorFactoryConfiguration> cacheDecoratorConfigurations = cacheConfiguration.getCacheDecoratorConfigurations();
        if (cacheDecoratorConfigurations == null || cacheDecoratorConfigurations.size() == 0) {
            LOG.debug("CacheDecoratorFactory not configured. Skipping for '" + cache.getName() + "'.");
            return ConfigurationHelper.createDefaultCacheDecorators(cache, this.configuration.getDefaultCacheConfiguration(), this.loader);
        }
        ArrayList<Ehcache> result = new ArrayList<Ehcache>();
        for (CacheConfiguration.CacheDecoratorFactoryConfiguration factoryConfiguration : cacheDecoratorConfigurations) {
            Ehcache decoratedCache = ConfigurationHelper.createDecoratedCache(cache, factoryConfiguration, false, this.loader);
            if (decoratedCache == null) continue;
            result.add(decoratedCache);
        }
        for (Ehcache defaultDecoratedCache : ConfigurationHelper.createDefaultCacheDecorators(cache, this.configuration.getDefaultCacheConfiguration(), this.loader)) {
            result.add(defaultDecoratedCache);
        }
        return result;
    }

    public static List<Ehcache> createDefaultCacheDecorators(Ehcache cache, CacheConfiguration defaultCacheConfiguration, ClassLoader loader) {
        List<CacheConfiguration.CacheDecoratorFactoryConfiguration> defaultCacheDecoratorConfigurations;
        if (cache == null) {
            throw new CacheException("Underlying cache cannot be null when creating decorated caches.");
        }
        List<CacheConfiguration.CacheDecoratorFactoryConfiguration> list = defaultCacheDecoratorConfigurations = defaultCacheConfiguration == null ? null : defaultCacheConfiguration.getCacheDecoratorConfigurations();
        if (defaultCacheDecoratorConfigurations == null || defaultCacheDecoratorConfigurations.size() == 0) {
            LOG.debug("CacheDecoratorFactory not configured for defaultCache. Skipping for '" + cache.getName() + "'.");
            return Collections.emptyList();
        }
        ArrayList<Ehcache> result = new ArrayList<Ehcache>();
        HashSet<String> newCacheNames = new HashSet<String>();
        for (CacheConfiguration.CacheDecoratorFactoryConfiguration factoryConfiguration : defaultCacheDecoratorConfigurations) {
            Ehcache decoratedCache = ConfigurationHelper.createDecoratedCache(cache, factoryConfiguration, true, loader);
            if (decoratedCache == null) continue;
            if (newCacheNames.contains(decoratedCache.getName())) {
                throw new InvalidConfigurationException("Looks like the defaultCache is configured with multiple CacheDecoratorFactory's that does not set unique names for newly created caches. Please fix the CacheDecoratorFactory and/or the config to set unique names for newly created caches.");
            }
            newCacheNames.add(decoratedCache.getName());
            result.add(decoratedCache);
        }
        return result;
    }

    private static Ehcache createDecoratedCache(Ehcache cache, CacheConfiguration.CacheDecoratorFactoryConfiguration factoryConfiguration, boolean forDefaultCache, ClassLoader loader) {
        if (factoryConfiguration == null) {
            return null;
        }
        String className = factoryConfiguration.getFullyQualifiedClassPath();
        if (className == null) {
            LOG.debug("CacheDecoratorFactory was specified without the name of the factory. Skipping...");
            return null;
        }
        CacheDecoratorFactory factory = (CacheDecoratorFactory)ClassLoaderUtil.createNewInstance(loader, className);
        Properties properties = PropertyUtil.parseProperties(factoryConfiguration.getProperties(), factoryConfiguration.getPropertySeparator());
        if (forDefaultCache) {
            return factory.createDefaultDecoratedEhcache(cache, properties);
        }
        return factory.createDecoratedEhcache(cache, properties);
    }

    public static Class<?> getSearchAttributeType(SearchAttribute sa, ClassLoader loader) {
        return sa.getType(loader);
    }

    public final Configuration getConfigurationBean() {
        return this.configuration;
    }
}

