/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.terracotta;

import java.util.concurrent.Callable;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.cluster.CacheCluster;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.event.CacheEventListener;
import net.sf.ehcache.management.event.ManagementEventSink;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.store.TerracottaStore;
import net.sf.ehcache.terracotta.ClusteredInstanceFactory;
import net.sf.ehcache.terracotta.TerracottaClient;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public class ClusteredInstanceFactoryWrapper
implements ClusteredInstanceFactory {
    private final TerracottaClient client;
    private final ClusteredInstanceFactory delegate;

    public ClusteredInstanceFactoryWrapper(TerracottaClient client, ClusteredInstanceFactory delegate) {
        this.client = client;
        this.delegate = delegate;
    }

    protected ClusteredInstanceFactory getActualFactory() {
        return this.delegate;
    }

    @Override
    public CacheCluster getTopology() {
        return this.client.getCacheCluster();
    }

    @Override
    public String getUUID() {
        return this.delegate.getUUID();
    }

    @Override
    public void enableNonStopForCurrentThread(boolean enable) {
        this.delegate.enableNonStopForCurrentThread(enable);
    }

    @Override
    public CacheEventListener createEventReplicator(Ehcache cache) {
        return this.delegate.createEventReplicator(cache);
    }

    @Override
    public Store createStore(Ehcache cache) {
        return this.delegate.createStore(cache);
    }

    @Override
    public TransactionIDFactory createTransactionIDFactory(String uuid, String cacheManagerName) {
        return this.delegate.createTransactionIDFactory(uuid, cacheManagerName);
    }

    @Override
    public WriteBehind createWriteBehind(Ehcache cache) {
        return this.delegate.createWriteBehind(cache);
    }

    @Override
    public SoftLockManager getOrCreateSoftLockManager(Ehcache cache) {
        return this.delegate.getOrCreateSoftLockManager(cache);
    }

    @Override
    public void shutdown() {
        this.delegate.shutdown();
    }

    @Override
    public TerracottaStore createNonStopStore(Callable<TerracottaStore> store, Ehcache cache) {
        return this.delegate.createNonStopStore(store, cache);
    }

    @Override
    public boolean destroyCache(String cacheManagerName, String cacheName) {
        return this.delegate.destroyCache(cacheManagerName, cacheName);
    }

    @Override
    public void linkClusteredCacheManager(String cacheManagerName, Configuration configuration) {
        this.delegate.linkClusteredCacheManager(cacheManagerName, configuration);
    }

    @Override
    public void unlinkCache(String cacheName) {
        this.delegate.unlinkCache(cacheName);
    }

    @Override
    public ManagementEventSink createEventSink() {
        return this.delegate.createEventSink();
    }

    @Override
    public void waitForOrchestrator(String cacheManagerName) {
        this.delegate.waitForOrchestrator(cacheManagerName);
    }
}

