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
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public interface ClusteredInstanceFactory {
    public Store createStore(Ehcache var1);

    public CacheCluster getTopology();

    public WriteBehind createWriteBehind(Ehcache var1);

    public CacheEventListener createEventReplicator(Ehcache var1);

    public String getUUID();

    public void enableNonStopForCurrentThread(boolean var1);

    public void shutdown();

    public TransactionIDFactory createTransactionIDFactory(String var1, String var2);

    public SoftLockManager getOrCreateSoftLockManager(Ehcache var1);

    public TerracottaStore createNonStopStore(Callable<TerracottaStore> var1, Ehcache var2);

    public boolean destroyCache(String var1, String var2);

    public void waitForOrchestrator(String var1);

    public void linkClusteredCacheManager(String var1, Configuration var2);

    public void unlinkCache(String var1);

    public ManagementEventSink createEventSink();
}

