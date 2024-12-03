/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management;

import java.util.List;
import java.util.Set;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.hibernate.management.impl.EhcacheHibernateMbeanNames;
import net.sf.ehcache.management.Cache;
import net.sf.ehcache.management.CacheConfiguration;
import net.sf.ehcache.management.CacheManager;
import net.sf.ehcache.management.CacheStatistics;
import net.sf.ehcache.management.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ManagementService
implements CacheManagerEventListener {
    private static final Logger LOG = LoggerFactory.getLogger((String)ManagementService.class.getName());
    private final MBeanServer mBeanServer;
    private final net.sf.ehcache.CacheManager backingCacheManager;
    private final boolean registerCacheManager;
    private final boolean registerCaches;
    private final boolean registerCacheConfigurations;
    private final boolean registerCacheStatistics;
    private final boolean registerCacheStores;
    private Status status = Status.STATUS_UNINITIALISED;

    public ManagementService(net.sf.ehcache.CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches, boolean registerCacheConfigurations, boolean registerCacheStatistics, boolean registerCacheStores) throws CacheException {
        this.backingCacheManager = cacheManager;
        this.mBeanServer = mBeanServer;
        this.registerCacheManager = registerCacheManager;
        this.registerCaches = registerCaches;
        this.registerCacheConfigurations = registerCacheConfigurations;
        this.registerCacheStatistics = registerCacheStatistics;
        this.registerCacheStores = registerCacheStores;
    }

    public ManagementService(net.sf.ehcache.CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches, boolean registerCacheConfigurations, boolean registerCacheStatistics) throws CacheException {
        this(cacheManager, mBeanServer, registerCacheManager, registerCaches, registerCacheConfigurations, registerCacheStatistics, false);
    }

    public static void registerMBeans(net.sf.ehcache.CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches, boolean registerCacheConfigurations, boolean registerCacheStatistics, boolean registerCacheStores) throws CacheException {
        ManagementService registry = new ManagementService(cacheManager, mBeanServer, registerCacheManager, registerCaches, registerCacheConfigurations, registerCacheStatistics, registerCacheStores);
        registry.init();
    }

    public static void registerMBeans(net.sf.ehcache.CacheManager cacheManager, MBeanServer mBeanServer, boolean registerCacheManager, boolean registerCaches, boolean registerCacheConfigurations, boolean registerCacheStatistics) throws CacheException {
        ManagementService.registerMBeans(cacheManager, mBeanServer, registerCacheManager, registerCaches, registerCacheConfigurations, registerCacheStatistics, false);
    }

    @Override
    public void init() throws CacheException {
        CacheManager cacheManager = new CacheManager(this.backingCacheManager);
        try {
            this.registerCacheManager(cacheManager);
            List caches = cacheManager.getCaches();
            for (int i = 0; i < caches.size(); ++i) {
                Cache cache = (Cache)caches.get(i);
                this.registerCachesIfRequired(cache);
                this.registerCacheStatisticsIfRequired(cache);
                this.registerCacheConfigurationIfRequired(cache);
                this.registerCacheStoreIfRequired(cache);
            }
        }
        catch (Exception e) {
            throw new CacheException(e);
        }
        this.status = Status.STATUS_ALIVE;
        this.backingCacheManager.getCacheManagerEventListenerRegistry().registerListener(this);
    }

    private void registerCacheManager(CacheManager cacheManager) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (this.registerCacheManager) {
            this.mBeanServer.registerMBean(cacheManager, cacheManager.getObjectName());
        }
    }

    private void registerCacheConfigurationIfRequired(Cache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (this.registerCacheConfigurations) {
            CacheConfiguration cacheConfiguration = cache.getCacheConfiguration();
            this.mBeanServer.registerMBean(cacheConfiguration, cacheConfiguration.getObjectName());
        }
    }

    private void registerCacheStatisticsIfRequired(Cache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (this.registerCacheStatistics) {
            CacheStatistics cacheStatistics = cache.getStatistics();
            this.mBeanServer.registerMBean(cacheStatistics, cacheStatistics.getObjectName());
        }
    }

    private void registerCachesIfRequired(Cache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        if (this.registerCaches) {
            this.mBeanServer.registerMBean(cache, cache.getObjectName());
        }
    }

    private void registerCacheStoreIfRequired(Cache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        Store cacheStore;
        if (this.registerCacheStores && (cacheStore = cache.getStore()) != null) {
            this.mBeanServer.registerMBean(cacheStore, cacheStore.getObjectName());
        }
    }

    @Override
    public Status getStatus() {
        return this.status;
    }

    @Override
    public void dispose() throws CacheException {
        Set<ObjectName> registeredObjectNames = null;
        try {
            registeredObjectNames = this.mBeanServer.queryNames(CacheManager.createObjectName(this.backingCacheManager), null);
            registeredObjectNames.addAll(this.mBeanServer.queryNames(new ObjectName("net.sf.ehcache:*,CacheManager=" + EhcacheHibernateMbeanNames.mbeanSafe(this.backingCacheManager.toString())), null));
        }
        catch (MalformedObjectNameException e) {
            LOG.error("Error querying MBeanServer. Error was " + e.getMessage(), (Throwable)e);
        }
        for (ObjectName objectName : registeredObjectNames) {
            try {
                this.mBeanServer.unregisterMBean(objectName);
            }
            catch (Exception e) {
                LOG.error("Error unregistering object instance " + objectName + " . Error was " + e.getMessage(), (Throwable)e);
            }
        }
        this.status = Status.STATUS_SHUTDOWN;
    }

    @Override
    public void notifyCacheAdded(String cacheName) {
        if (this.registerCaches || this.registerCacheStatistics || this.registerCacheConfigurations) {
            Cache cache = new Cache(this.backingCacheManager.getEhcache(cacheName));
            try {
                this.registerCachesIfRequired(cache);
                this.registerCacheStatisticsIfRequired(cache);
                this.registerCacheConfigurationIfRequired(cache);
                this.registerCacheStoreIfRequired(cache);
            }
            catch (Exception e) {
                LOG.error("Error registering cache for management for " + cache.getObjectName() + " . Error was " + e.getMessage(), (Throwable)e);
            }
        }
    }

    @Override
    public void notifyCacheRemoved(String cacheName) {
        if (this.registerCaches) {
            this.unregisterMBean(Cache.createObjectName(this.backingCacheManager.toString(), cacheName));
        }
        if (this.registerCacheConfigurations) {
            this.unregisterMBean(CacheConfiguration.createObjectName(this.backingCacheManager.toString(), cacheName));
        }
        if (this.registerCacheStatistics) {
            this.unregisterMBean(CacheStatistics.createObjectName(this.backingCacheManager.toString(), cacheName));
        }
        if (this.registerCacheStores) {
            this.unregisterMBean(Store.createObjectName(this.backingCacheManager.toString(), cacheName));
        }
    }

    private void unregisterMBean(ObjectName objectName) {
        try {
            if (this.mBeanServer.isRegistered(objectName)) {
                this.mBeanServer.unregisterMBean(objectName);
            }
        }
        catch (Exception e) {
            LOG.error("Error unregistering cache for management for " + objectName + " . Error was " + e.getMessage(), (Throwable)e);
        }
    }
}

