/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ehcache.Cache
 *  net.sf.ehcache.CacheException
 *  net.sf.ehcache.CacheManager
 *  net.sf.ehcache.Ehcache
 *  net.sf.ehcache.bootstrap.BootstrapCacheLoader
 *  net.sf.ehcache.config.CacheConfiguration
 *  net.sf.ehcache.constructs.blocking.BlockingCache
 *  net.sf.ehcache.constructs.blocking.CacheEntryFactory
 *  net.sf.ehcache.constructs.blocking.SelfPopulatingCache
 *  net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory
 *  net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache
 *  net.sf.ehcache.event.CacheEventListener
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.beans.factory.BeanNameAware
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 */
package org.springframework.cache.ehcache;

import java.util.Set;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.bootstrap.BootstrapCacheLoader;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.constructs.blocking.BlockingCache;
import net.sf.ehcache.constructs.blocking.CacheEntryFactory;
import net.sf.ehcache.constructs.blocking.SelfPopulatingCache;
import net.sf.ehcache.constructs.blocking.UpdatingCacheEntryFactory;
import net.sf.ehcache.constructs.blocking.UpdatingSelfPopulatingCache;
import net.sf.ehcache.event.CacheEventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.Nullable;

public class EhCacheFactoryBean
extends CacheConfiguration
implements FactoryBean<Ehcache>,
BeanNameAware,
InitializingBean {
    protected final Log logger = LogFactory.getLog(((Object)((Object)this)).getClass());
    @Nullable
    private CacheManager cacheManager;
    private boolean blocking = false;
    @Nullable
    private CacheEntryFactory cacheEntryFactory;
    @Nullable
    private BootstrapCacheLoader bootstrapCacheLoader;
    @Nullable
    private Set<CacheEventListener> cacheEventListeners;
    private boolean disabled = false;
    @Nullable
    private String beanName;
    @Nullable
    private Ehcache cache;

    public EhCacheFactoryBean() {
        this.setMaxEntriesLocalHeap(10000L);
        this.setMaxEntriesLocalDisk(10000000L);
        this.setTimeToLiveSeconds(120L);
        this.setTimeToIdleSeconds(120L);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public void setCacheName(String cacheName) {
        this.setName(cacheName);
    }

    public void setTimeToLive(int timeToLive) {
        this.setTimeToLiveSeconds(timeToLive);
    }

    public void setTimeToIdle(int timeToIdle) {
        this.setTimeToIdleSeconds(timeToIdle);
    }

    public void setDiskSpoolBufferSize(int diskSpoolBufferSize) {
        this.setDiskSpoolBufferSizeMB(diskSpoolBufferSize);
    }

    public void setBlocking(boolean blocking) {
        this.blocking = blocking;
    }

    public void setCacheEntryFactory(CacheEntryFactory cacheEntryFactory) {
        this.cacheEntryFactory = cacheEntryFactory;
    }

    public void setBootstrapCacheLoader(BootstrapCacheLoader bootstrapCacheLoader) {
        this.bootstrapCacheLoader = bootstrapCacheLoader;
    }

    public void setCacheEventListeners(Set<CacheEventListener> cacheEventListeners) {
        this.cacheEventListeners = cacheEventListeners;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public void setBeanName(String name) {
        this.beanName = name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void afterPropertiesSet() throws CacheException {
        String cacheName = this.getName();
        if (cacheName == null && (cacheName = this.beanName) != null) {
            this.setName(cacheName);
        }
        if (this.cacheManager == null) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Using default EhCache CacheManager for cache region '" + cacheName + "'"));
            }
            this.cacheManager = CacheManager.getInstance();
        }
        CacheManager cacheManager = this.cacheManager;
        synchronized (cacheManager) {
            Ehcache decoratedCache;
            Cache rawCache;
            boolean cacheExists = this.cacheManager.cacheExists(cacheName);
            if (cacheExists) {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Using existing EhCache cache region '" + cacheName + "'"));
                }
                rawCache = this.cacheManager.getEhcache(cacheName);
            } else {
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Creating new EhCache cache region '" + cacheName + "'"));
                }
                rawCache = this.createCache();
                rawCache.setBootstrapCacheLoader(this.bootstrapCacheLoader);
            }
            if (this.cacheEventListeners != null) {
                for (CacheEventListener listener : this.cacheEventListeners) {
                    rawCache.getCacheEventNotificationService().registerListener(listener);
                }
            }
            if (!cacheExists) {
                this.cacheManager.addCache((Ehcache)rawCache);
            }
            if (this.disabled) {
                rawCache.setDisabled(true);
            }
            if ((decoratedCache = this.decorateCache((Ehcache)rawCache)) != rawCache) {
                this.cacheManager.replaceCacheWithDecoratedCache((Ehcache)rawCache, decoratedCache);
            }
            this.cache = decoratedCache;
        }
    }

    protected Cache createCache() {
        return new Cache((CacheConfiguration)this);
    }

    protected Ehcache decorateCache(Ehcache cache) {
        if (this.cacheEntryFactory != null) {
            if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
                return new UpdatingSelfPopulatingCache(cache, (UpdatingCacheEntryFactory)this.cacheEntryFactory);
            }
            return new SelfPopulatingCache(cache, this.cacheEntryFactory);
        }
        if (this.blocking) {
            return new BlockingCache(cache);
        }
        return cache;
    }

    @Nullable
    public Ehcache getObject() {
        return this.cache;
    }

    public Class<? extends Ehcache> getObjectType() {
        if (this.cache != null) {
            return this.cache.getClass();
        }
        if (this.cacheEntryFactory != null) {
            if (this.cacheEntryFactory instanceof UpdatingCacheEntryFactory) {
                return UpdatingSelfPopulatingCache.class;
            }
            return SelfPopulatingCache.class;
        }
        if (this.blocking) {
            return BlockingCache.class;
        }
        return Cache.class;
    }

    public boolean isSingleton() {
        return true;
    }
}

