/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package net.sf.ehcache.management.sampled;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Status;
import net.sf.ehcache.event.CacheManagerEventListener;
import net.sf.ehcache.hibernate.management.impl.BaseEmitterBean;
import net.sf.ehcache.management.provider.MBeanRegistrationProvider;
import net.sf.ehcache.management.provider.MBeanRegistrationProviderException;
import net.sf.ehcache.management.sampled.SampledCache;
import net.sf.ehcache.management.sampled.SampledCacheManager;
import net.sf.ehcache.management.sampled.SampledEhcacheMBeans;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SampledMBeanRegistrationProvider
implements MBeanRegistrationProvider,
CacheManagerEventListener {
    private static final Logger LOG = LoggerFactory.getLogger((String)SampledMBeanRegistrationProvider.class.getName());
    private static final int MAX_MBEAN_REGISTRATION_RETRIES = 50;
    private Status status = Status.STATUS_UNINITIALISED;
    private CacheManager cacheManager;
    private String clientUUID;
    private final MBeanServer mBeanServer;
    private final Map<ObjectName, Object> mbeans = new ConcurrentHashMap<ObjectName, Object>();
    private volatile String registeredCacheManagerName;
    private SampledCacheManager cacheManagerMBean;

    public SampledMBeanRegistrationProvider() {
        this.mBeanServer = ManagementFactory.getPlatformMBeanServer();
    }

    @Override
    public synchronized void initialize(CacheManager cacheManagerParam, ClusteredInstanceFactory clusteredInstanceFactory) {
        if (this.isAlive()) {
            return;
        }
        this.status = Status.STATUS_ALIVE;
        this.cacheManager = cacheManagerParam;
        this.clientUUID = clusteredInstanceFactory == null ? "" : clusteredInstanceFactory.getUUID();
        try {
            this.cacheManagerMBean = new SampledCacheManager(this.cacheManager);
            this.registerCacheManagerMBean(this.cacheManagerMBean);
        }
        catch (Exception e) {
            this.status = Status.STATUS_UNINITIALISED;
            throw new CacheException(e);
        }
        this.cacheManager.getCacheManagerEventListenerRegistry().registerListener(this);
    }

    private void registerCacheManagerMBean(SampledCacheManager cacheManagerMBean) throws Exception {
        String[] caches;
        int tries = 0;
        boolean success = false;
        InstanceAlreadyExistsException exception = null;
        while (true) {
            this.registeredCacheManagerName = this.cacheManager.getName();
            if (tries != 0) {
                this.registeredCacheManagerName = this.registeredCacheManagerName + "_" + tries;
            }
            try {
                ObjectName cacheManagerObjectName = SampledEhcacheMBeans.getCacheManagerObjectName(this.clientUUID, this.registeredCacheManagerName);
                this.mBeanServer.registerMBean(cacheManagerMBean, cacheManagerObjectName);
                this.mbeans.put(cacheManagerObjectName, cacheManagerMBean);
                success = true;
                cacheManagerMBean.setMBeanRegisteredName(this.registeredCacheManagerName);
            }
            catch (InstanceAlreadyExistsException e) {
                success = false;
                exception = e;
                if (++tries < 50) continue;
            }
            break;
        }
        if (!success) {
            throw new Exception("Cannot register mbean for CacheManager with name" + this.cacheManager.getName() + " after 50 retries. Last tried name=" + this.registeredCacheManagerName, exception);
        }
        for (String cacheName : caches = this.cacheManager.getCacheNames()) {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            this.registerCacheMBean(cache);
            this.registerStoreMBean(cache);
        }
    }

    @Override
    public synchronized void reinitialize(ClusteredInstanceFactory clusteredInstanceFactory) throws MBeanRegistrationProviderException {
        this.dispose();
        this.initialize(this.cacheManager, clusteredInstanceFactory);
    }

    @Override
    public synchronized boolean isInitialized() {
        return this.status == Status.STATUS_ALIVE;
    }

    @Override
    public void init() throws CacheException {
    }

    private void registerCacheMBean(Ehcache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        SampledCache terracottaCacheMBean = new SampledCache(cache);
        try {
            ObjectName cacheObjectName = SampledEhcacheMBeans.getCacheObjectName(this.clientUUID, this.registeredCacheManagerName, terracottaCacheMBean.getImmutableCacheName());
            this.mBeanServer.registerMBean(terracottaCacheMBean, cacheObjectName);
            this.mbeans.put(cacheObjectName, terracottaCacheMBean);
        }
        catch (MalformedObjectNameException e) {
            throw new MBeanRegistrationException(e);
        }
    }

    private void registerStoreMBean(Ehcache cache) throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        Object bean;
        if (cache instanceof Cache && (bean = ((Cache)cache).getStoreMBean()) != null) {
            try {
                ObjectName storeObjectName = SampledEhcacheMBeans.getStoreObjectName(this.clientUUID, this.registeredCacheManagerName, cache.getName());
                this.mBeanServer.registerMBean(bean, storeObjectName);
                this.mbeans.put(storeObjectName, bean);
            }
            catch (MalformedObjectNameException e) {
                throw new MBeanRegistrationException(e);
            }
        }
    }

    @Override
    public synchronized Status getStatus() {
        return this.status;
    }

    @Override
    public synchronized void dispose() throws CacheException {
        if (!this.isAlive()) {
            return;
        }
        Set<ObjectName> registeredObjectNames = this.mbeans.keySet();
        for (ObjectName objectName : registeredObjectNames) {
            try {
                Object o;
                if (this.mBeanServer.isRegistered(objectName)) {
                    this.mBeanServer.unregisterMBean(objectName);
                }
                if (!((o = this.mbeans.get(objectName)) instanceof BaseEmitterBean)) continue;
                BaseEmitterBean mbean = (BaseEmitterBean)o;
                mbean.dispose();
            }
            catch (Exception e) {
                LOG.warn("Error unregistering object instance " + objectName + " . Error was " + e.getMessage(), (Throwable)e);
            }
        }
        this.mbeans.clear();
        this.cacheManager.getCacheManagerEventListenerRegistry().unregisterListener(this);
        this.status = Status.STATUS_SHUTDOWN;
    }

    public synchronized boolean isAlive() {
        return this.status == Status.STATUS_ALIVE;
    }

    @Override
    public void notifyCacheAdded(String cacheName) {
        if (!this.isAlive()) {
            return;
        }
        try {
            Ehcache cache = this.cacheManager.getEhcache(cacheName);
            this.registerCacheMBean(cache);
            this.registerStoreMBean(cache);
        }
        catch (Exception e) {
            LOG.warn("Error registering cache for management for " + cacheName + " . Error was " + e.getMessage(), (Throwable)e);
        }
    }

    @Override
    public void notifyCacheRemoved(String cacheName) {
        if (!this.isAlive()) {
            return;
        }
        ObjectName objectName = null;
        try {
            objectName = SampledEhcacheMBeans.getCacheObjectName(this.clientUUID, this.registeredCacheManagerName, cacheName);
            if (this.mBeanServer.isRegistered(objectName)) {
                this.mBeanServer.unregisterMBean(objectName);
            }
        }
        catch (Exception e) {
            LOG.warn("Error unregistering cache for management for " + objectName + " . Error was " + e.getMessage(), (Throwable)e);
        }
        try {
            ObjectName storeObjectName = SampledEhcacheMBeans.getStoreObjectName(this.clientUUID, this.registeredCacheManagerName, cacheName);
            if (this.mBeanServer.isRegistered(storeObjectName)) {
                this.mBeanServer.unregisterMBean(storeObjectName);
            }
        }
        catch (Exception e) {
            LOG.warn("Error unregistering cache for management for " + objectName + " . Error was " + e.getMessage(), (Throwable)e);
        }
    }
}

