/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.constructs.refreshahead;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.Status;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.PersistenceConfiguration;
import net.sf.ehcache.config.TerracottaConfiguration;
import net.sf.ehcache.constructs.EhcacheDecoratorAdapter;
import net.sf.ehcache.constructs.refreshahead.RefreshAheadCacheConfiguration;
import net.sf.ehcache.constructs.refreshahead.ThreadedWorkQueue;
import net.sf.ehcache.extension.CacheExtension;
import net.sf.ehcache.loader.CacheLoader;
import net.sf.ehcache.statistics.extended.ExtendedStatistics;
import net.sf.ehcache.store.MemoryStoreEvictionPolicy;
import net.sf.ehcache.util.VmUtils;
import org.terracotta.statistics.Statistic;

public class RefreshAheadCache
extends EhcacheDecoratorAdapter {
    private static final Object REFRESH_VALUE = Boolean.TRUE;
    private static final int DEFAULT_SUPPORT_TTL_SECONDS = (int)TimeUnit.SECONDS.convert(10L, TimeUnit.MINUTES);
    private final AtomicLong refreshSuccessCount = new AtomicLong();
    private final RefreshAheadCacheConfiguration refreshAheadConfig;
    private CacheConfiguration supportConfig;
    private volatile Ehcache supportCache;
    private volatile ThreadedWorkQueue<Object> refreshWorkQueue;

    public RefreshAheadCache(Ehcache adaptedCache, RefreshAheadCacheConfiguration refreshConfig) {
        super(adaptedCache);
        this.refreshAheadConfig = refreshConfig;
        boolean refreshAllowed = !this.underlyingCache.getCacheConfiguration().isXaStrictTransactional();
        refreshAllowed = refreshAllowed && !this.underlyingCache.getCacheConfiguration().isXaTransactional();
        refreshAllowed = refreshAllowed && !this.underlyingCache.getCacheConfiguration().isLocalTransactional();
        boolean bl = refreshAllowed = refreshAllowed && !VmUtils.isInGoogleAppEngine();
        if (!refreshAllowed) {
            throw new UnsupportedOperationException("refresh-ahead not supported under transactions or with GAE");
        }
        this.initSupportCache();
        this.initWorkQueue();
    }

    private void initSupportCache() {
        this.supportConfig = new CacheConfiguration();
        this.supportConfig.name(this.underlyingCache.getName() + "_" + this.getClass().getName() + "_refreshAheadSupport");
        this.supportConfig = this.supportConfig.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.NONE));
        int activeSize = 2 * this.refreshAheadConfig.getBatchSize() * this.refreshAheadConfig.getNumberOfThreads();
        this.supportConfig = this.supportConfig.maxEntriesLocalHeap(activeSize);
        this.supportConfig = this.supportConfig.memoryStoreEvictionPolicy(MemoryStoreEvictionPolicy.LRU);
        this.supportConfig = this.supportConfig.timeToLiveSeconds(DEFAULT_SUPPORT_TTL_SECONDS);
        if (this.underlyingCache.getCacheConfiguration().isTerracottaClustered()) {
            this.supportConfig = this.supportConfig.persistence(new PersistenceConfiguration().strategy(PersistenceConfiguration.Strategy.DISTRIBUTED));
            TerracottaConfiguration newTerracottaConfig = new TerracottaConfiguration().clustered(true);
            newTerracottaConfig.consistency(TerracottaConfiguration.Consistency.STRONG);
            this.supportConfig.addTerracotta(newTerracottaConfig);
        } else {
            this.supportConfig.setMaxElementsOnDisk(activeSize);
        }
        this.supportCache = new Cache(this.supportConfig);
        Ehcache prior = this.underlyingCache.getCacheManager().addCacheIfAbsent(this.supportCache);
        if (prior != this.supportCache) {
            throw new IllegalStateException("Unable to add refresh ahead support cache due to name collision: " + this.refreshAheadConfig.getName());
        }
        prior.removeAll();
        this.underlyingCache.registerCacheExtension(new CacheExtension(){

            @Override
            public void init() {
            }

            @Override
            public Status getStatus() {
                return RefreshAheadCache.this.underlyingCache.getStatus();
            }

            @Override
            public void dispose() throws CacheException {
                RefreshAheadCache.this.localDispose();
            }

            @Override
            public CacheExtension clone(Ehcache cache) throws CloneNotSupportedException {
                throw new CloneNotSupportedException();
            }
        });
    }

    private void initWorkQueue() {
        ThreadedWorkQueue.BatchWorker<Object> batchWorker = new ThreadedWorkQueue.BatchWorker<Object>(){

            /*
             * WARNING - Removed try catching itself - possible behaviour change.
             */
            @Override
            public void process(Collection<? extends Object> collection) {
                long accessTime = System.currentTimeMillis();
                HashSet<Object> keysToProcess = new HashSet<Object>();
                for (Object object : collection) {
                    Element ersatz;
                    Element quickTest = RefreshAheadCache.this.underlyingCache.getQuiet(object);
                    if (quickTest != null && !RefreshAheadCache.this.checkForRefresh(quickTest, accessTime, RefreshAheadCache.this.refreshAheadConfig.getTimeToRefreshMillis()) || RefreshAheadCache.this.supportCache.putIfAbsent(ersatz = new Element(object, REFRESH_VALUE)) != null) continue;
                    keysToProcess.add(object);
                }
                try {
                    for (CacheLoader cacheLoader : RefreshAheadCache.this.underlyingCache.getRegisteredCacheLoaders()) {
                        if (keysToProcess.isEmpty()) break;
                        Map values = cacheLoader.loadAll(keysToProcess);
                        keysToProcess.removeAll(values.keySet());
                        try {
                            for (Map.Entry entry : values.entrySet()) {
                                Element newElement = new Element(entry.getKey(), entry.getValue());
                                RefreshAheadCache.this.underlyingCache.put(newElement);
                                RefreshAheadCache.this.refreshSuccessCount.incrementAndGet();
                            }
                        }
                        finally {
                            RefreshAheadCache.this.supportCache.removeAll(values.keySet());
                        }
                    }
                    if (RefreshAheadCache.this.refreshAheadConfig.isEvictOnLoadMiss() && !keysToProcess.isEmpty()) {
                        RefreshAheadCache.this.underlyingCache.removeAll(keysToProcess);
                    }
                }
                finally {
                    RefreshAheadCache.this.supportCache.removeAll(keysToProcess);
                }
            }
        };
        this.refreshWorkQueue = new ThreadedWorkQueue<Object>(batchWorker, this.refreshAheadConfig.getNumberOfThreads(), new ThreadFactory(){

            @Override
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                return t;
            }
        }, this.refreshAheadConfig.getMaximumRefreshBacklogItems(), this.refreshAheadConfig.getBatchSize());
    }

    private boolean checkForRefresh(Element elem, long accessTime, long timeToRefreshMillis) {
        if (elem == null) {
            return false;
        }
        long minAccessForRefreshTime = elem.getCreationTime() + timeToRefreshMillis;
        return accessTime >= minAccessForRefreshTime;
    }

    private void possiblyTriggerRefresh(Element elem, long timeToRefreshMillis) {
        if (this.checkForRefresh(elem, System.currentTimeMillis(), timeToRefreshMillis)) {
            this.refreshWorkQueue.offer(elem.getObjectKey());
        }
    }

    @Override
    public Element get(Object key) throws IllegalStateException, CacheException {
        Element elem = super.get(key);
        this.possiblyTriggerRefresh(elem, this.refreshAheadConfig.getTimeToRefreshMillis());
        return elem;
    }

    @Override
    public Element get(Serializable key) throws IllegalStateException, CacheException {
        Element elem = super.get(key);
        this.possiblyTriggerRefresh(elem, this.refreshAheadConfig.getTimeToRefreshMillis());
        return elem;
    }

    @Statistic(name="refreshed", tags={"refreshahead"})
    public long getRefreshSuccessCount() {
        return this.refreshSuccessCount.get();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void localDispose() throws IllegalStateException {
        RefreshAheadCache refreshAheadCache = this;
        synchronized (refreshAheadCache) {
            if (this.refreshWorkQueue != null) {
                this.refreshWorkQueue.shutdown();
                this.refreshWorkQueue = null;
            }
            if (this.supportCache != null) {
                try {
                    this.supportCache.getCacheManager().removeCache(this.getName());
                }
                catch (Throwable throwable) {
                    // empty catch block
                }
                this.supportCache = null;
            }
        }
    }

    @Override
    public String getName() {
        if (this.refreshAheadConfig.getName() != null) {
            return this.refreshAheadConfig.getName();
        }
        return super.getName();
    }

    @Statistic(name="offered", tags={"refreshahead"})
    public long getOfferCount() {
        return this.refreshWorkQueue.getOfferedCount();
    }

    @Statistic(name="dropped", tags={"refreshahead"})
    public long getDroppedCount() {
        return this.refreshWorkQueue.getDroppedCount();
    }

    @Statistic(name="processed", tags={"refreshahead"})
    public long getProcessedCount() {
        return this.refreshWorkQueue.getProcessedCount();
    }

    @Statistic(name="backlog", tags={"refreshahead"})
    public long getBacklogCount() {
        return this.refreshWorkQueue.getBacklogCount();
    }

    public static Set<ExtendedStatistics.Statistic<Number>> findRefreshedStatistic(Ehcache cache) {
        return cache.getStatistics().getExtended().passthru("refreshed", Collections.singletonMap("refreshahead", null).keySet());
    }

    public static Set<ExtendedStatistics.Statistic<Number>> findOfferStatistic(Ehcache cache) {
        return cache.getStatistics().getExtended().passthru("offered", Collections.singletonMap("refreshahead", null).keySet());
    }

    public static Set<ExtendedStatistics.Statistic<Number>> findDroppedStatistic(Ehcache cache) {
        return cache.getStatistics().getExtended().passthru("dropped", Collections.singletonMap("refreshahead", null).keySet());
    }

    public static Set<ExtendedStatistics.Statistic<Number>> findProcessedStatistic(Ehcache cache) {
        return cache.getStatistics().getExtended().passthru("processed", Collections.singletonMap("refreshahead", null).keySet());
    }

    public static Set<ExtendedStatistics.Statistic<Number>> findBacklogStatistic(Ehcache cache) {
        return cache.getStatistics().getExtended().passthru("backlog", Collections.singletonMap("refreshahead", null).keySet());
    }
}

