/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.webresources;

import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;
import org.apache.catalina.WebResource;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.webresources.CachedResource;
import org.apache.catalina.webresources.StandardRoot;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.util.res.StringManager;

public class Cache {
    private static final Log log = LogFactory.getLog(Cache.class);
    protected static final StringManager sm = StringManager.getManager(Cache.class);
    private static final long TARGET_FREE_PERCENT_GET = 5L;
    private static final long TARGET_FREE_PERCENT_BACKGROUND = 10L;
    private static final int OBJECT_MAX_SIZE_FACTOR = 20;
    private final StandardRoot root;
    private final AtomicLong size = new AtomicLong(0L);
    private long ttl = 5000L;
    private long maxSize = 0xA00000L;
    private int objectMaxSize = (int)this.maxSize / 20;
    private WebResourceRoot.CacheStrategy cacheStrategy;
    private LongAdder lookupCount = new LongAdder();
    private LongAdder hitCount = new LongAdder();
    private final ConcurrentMap<String, CachedResource> resourceCache = new ConcurrentHashMap<String, CachedResource>();

    public Cache(StandardRoot root) {
        this.root = root;
    }

    protected WebResource getResource(String path, boolean useClassLoaderResources) {
        if (this.noCache(path)) {
            return this.root.getResourceInternal(path, useClassLoaderResources);
        }
        WebResourceRoot.CacheStrategy strategy = this.getCacheStrategy();
        if (strategy != null && strategy.noCache(path)) {
            return this.root.getResourceInternal(path, useClassLoaderResources);
        }
        this.lookupCount.increment();
        CachedResource cacheEntry = (CachedResource)this.resourceCache.get(path);
        if (cacheEntry != null && !cacheEntry.validateResource(useClassLoaderResources)) {
            this.removeCacheEntry(path);
            cacheEntry = null;
        }
        if (cacheEntry == null) {
            int objectMaxSizeBytes = this.getObjectMaxSizeBytes();
            CachedResource newCacheEntry = new CachedResource(this, this.root, path, this.getTtl(), objectMaxSizeBytes, useClassLoaderResources);
            cacheEntry = this.resourceCache.putIfAbsent(path, newCacheEntry);
            if (cacheEntry == null) {
                long targetSize;
                long newSize;
                cacheEntry = newCacheEntry;
                cacheEntry.validateResource(useClassLoaderResources);
                long delta = cacheEntry.getSize();
                this.size.addAndGet(delta);
                if (this.size.get() > this.maxSize && (newSize = this.evict(targetSize = this.maxSize * 95L / 100L, this.resourceCache.values().iterator())) > this.maxSize) {
                    this.removeCacheEntry(path);
                    log.warn((Object)sm.getString("cache.addFail", new Object[]{path, this.root.getContext().getName()}));
                }
            } else {
                if (cacheEntry.usesClassLoaderResources() != useClassLoaderResources) {
                    cacheEntry = newCacheEntry;
                }
                cacheEntry.validateResource(useClassLoaderResources);
            }
        } else {
            this.hitCount.increment();
        }
        return cacheEntry;
    }

    protected WebResource[] getResources(String path, boolean useClassLoaderResources) {
        this.lookupCount.increment();
        CachedResource cacheEntry = (CachedResource)this.resourceCache.get(path);
        if (cacheEntry != null && !cacheEntry.validateResources(useClassLoaderResources)) {
            this.removeCacheEntry(path);
            cacheEntry = null;
        }
        if (cacheEntry == null) {
            int objectMaxSizeBytes = this.getObjectMaxSizeBytes();
            CachedResource newCacheEntry = new CachedResource(this, this.root, path, this.getTtl(), objectMaxSizeBytes, useClassLoaderResources);
            cacheEntry = this.resourceCache.putIfAbsent(path, newCacheEntry);
            if (cacheEntry == null) {
                long targetSize;
                long newSize;
                cacheEntry = newCacheEntry;
                cacheEntry.validateResources(useClassLoaderResources);
                long delta = cacheEntry.getSize();
                this.size.addAndGet(delta);
                if (this.size.get() > this.maxSize && (newSize = this.evict(targetSize = this.maxSize * 95L / 100L, this.resourceCache.values().iterator())) > this.maxSize) {
                    this.removeCacheEntry(path);
                    log.warn((Object)sm.getString("cache.addFail", new Object[]{path}));
                }
            } else {
                cacheEntry.validateResources(useClassLoaderResources);
            }
        } else {
            this.hitCount.increment();
        }
        return cacheEntry.getWebResources();
    }

    protected void backgroundProcess() {
        TreeSet<CachedResource> orderedResources = new TreeSet<CachedResource>(Comparator.comparingLong(CachedResource::getNextCheck));
        orderedResources.addAll(this.resourceCache.values());
        Iterator<CachedResource> iter = orderedResources.iterator();
        long targetSize = this.maxSize * 90L / 100L;
        long newSize = this.evict(targetSize, iter);
        if (newSize > targetSize) {
            log.info((Object)sm.getString("cache.backgroundEvictFail", new Object[]{10L, this.root.getContext().getName(), newSize / 1024L}));
        }
    }

    private boolean noCache(String path) {
        return path.endsWith(".class") && (path.startsWith("/WEB-INF/classes/") || path.startsWith("/WEB-INF/lib/")) || path.startsWith("/WEB-INF/lib/") && path.endsWith(".jar");
    }

    private long evict(long targetSize, Iterator<CachedResource> iter) {
        long now = System.currentTimeMillis();
        long newSize = this.size.get();
        while (newSize > targetSize && iter.hasNext()) {
            CachedResource resource = iter.next();
            if (resource.getNextCheck() > now) continue;
            this.removeCacheEntry(resource.getWebappPath());
            newSize = this.size.get();
        }
        return newSize;
    }

    void removeCacheEntry(String path) {
        CachedResource cachedResource = (CachedResource)this.resourceCache.remove(path);
        if (cachedResource != null) {
            long delta = cachedResource.getSize();
            this.size.addAndGet(-delta);
        }
    }

    public WebResourceRoot.CacheStrategy getCacheStrategy() {
        return this.cacheStrategy;
    }

    public void setCacheStrategy(WebResourceRoot.CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }

    public long getTtl() {
        return this.ttl;
    }

    public void setTtl(long ttl) {
        this.ttl = ttl;
    }

    public long getMaxSize() {
        return this.maxSize / 1024L;
    }

    public void setMaxSize(long maxSize) {
        this.maxSize = maxSize * 1024L;
    }

    public long getLookupCount() {
        return this.lookupCount.sum();
    }

    public long getHitCount() {
        return this.hitCount.sum();
    }

    public void setObjectMaxSize(int objectMaxSize) {
        if ((long)objectMaxSize * 1024L > Integer.MAX_VALUE) {
            log.warn((Object)sm.getString("cache.objectMaxSizeTooBigBytes", new Object[]{objectMaxSize}));
            this.objectMaxSize = Integer.MAX_VALUE;
        }
        this.objectMaxSize = objectMaxSize * 1024;
    }

    public int getObjectMaxSize() {
        return this.objectMaxSize / 1024;
    }

    public int getObjectMaxSizeBytes() {
        return this.objectMaxSize;
    }

    void enforceObjectMaxSizeLimit() {
        long limit = this.maxSize / 20L;
        if (limit > Integer.MAX_VALUE) {
            return;
        }
        if ((long)this.objectMaxSize > limit) {
            log.warn((Object)sm.getString("cache.objectMaxSizeTooBig", new Object[]{this.objectMaxSize / 1024, (int)limit / 1024}));
            this.objectMaxSize = (int)limit;
        }
    }

    public void clear() {
        this.resourceCache.clear();
        this.size.set(0L);
    }

    public long getSize() {
        return this.size.get() / 1024L;
    }
}

