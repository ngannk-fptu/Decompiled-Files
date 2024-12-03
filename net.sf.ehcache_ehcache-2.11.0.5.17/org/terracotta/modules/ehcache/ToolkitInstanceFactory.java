/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.Toolkit
 *  org.terracotta.toolkit.collections.ToolkitMap
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 *  org.terracotta.toolkit.events.ToolkitNotifier
 *  org.terracotta.toolkit.internal.cache.ToolkitCacheInternal
 *  org.terracotta.toolkit.internal.collections.ToolkitListInternal
 */
package org.terracotta.modules.ehcache;

import java.io.Serializable;
import java.util.Set;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.CacheConfiguration;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.search.attribute.AttributeExtractor;
import net.sf.ehcache.transaction.Decision;
import net.sf.ehcache.transaction.TransactionID;
import org.terracotta.modules.ehcache.WanAwareToolkitCache;
import org.terracotta.modules.ehcache.async.AsyncConfig;
import org.terracotta.modules.ehcache.collections.SerializedToolkitCache;
import org.terracotta.modules.ehcache.event.CacheDisposalNotification;
import org.terracotta.modules.ehcache.event.CacheEventNotificationMsg;
import org.terracotta.modules.ehcache.store.CacheConfigChangeNotificationMsg;
import org.terracotta.modules.ehcache.transaction.ClusteredSoftLockIDKey;
import org.terracotta.modules.ehcache.transaction.SerializedReadCommittedClusteredSoftLock;
import org.terracotta.toolkit.Toolkit;
import org.terracotta.toolkit.collections.ToolkitMap;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;
import org.terracotta.toolkit.events.ToolkitNotifier;
import org.terracotta.toolkit.internal.cache.ToolkitCacheInternal;
import org.terracotta.toolkit.internal.collections.ToolkitListInternal;

public interface ToolkitInstanceFactory {
    public Toolkit getToolkit();

    public String getFullyQualifiedCacheName(Ehcache var1);

    public ToolkitCacheInternal<String, Serializable> getOrCreateToolkitCache(Ehcache var1);

    public ToolkitNotifier<CacheConfigChangeNotificationMsg> getOrCreateConfigChangeNotifier(Ehcache var1);

    public ToolkitLock getOrCreateStoreLock(Ehcache var1);

    public ToolkitMap<String, AsyncConfig> getOrCreateAsyncConfigMap();

    public ToolkitMap<String, Set<String>> getOrCreateAsyncListNamesMap(String var1, String var2);

    public ToolkitNotifier<CacheEventNotificationMsg> getOrCreateCacheEventNotifier(Ehcache var1);

    public ToolkitMap<String, AttributeExtractor> getOrCreateExtractorsMap(String var1, String var2);

    public ToolkitMap<String, String> getOrCreateAttributeMap(String var1, String var2);

    public boolean destroy(String var1, String var2);

    public void shutdown();

    public SerializedToolkitCache<TransactionID, Decision> getOrCreateTransactionCommitStateMap(String var1);

    public SerializedToolkitCache<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock> getOrCreateAllSoftLockMap(String var1, String var2);

    public ToolkitMap<SerializedReadCommittedClusteredSoftLock, Integer> getOrCreateNewSoftLocksSet(String var1, String var2);

    public ToolkitMap<String, Serializable> getOrCreateClusteredStoreConfigMap(String var1, String var2);

    public ToolkitMap<String, Serializable> getOrCreateCacheManagerMetaInfoMap(String var1);

    public ToolkitLock getSoftLockWriteLock(String var1, String var2, TransactionID var3, Object var4);

    public ToolkitReadWriteLock getSoftLockFreezeLock(String var1, String var2, TransactionID var3, Object var4);

    public ToolkitReadWriteLock getSoftLockNotifierLock(String var1, String var2, TransactionID var3, Object var4);

    public void removeNonStopConfigforCache(Ehcache var1);

    public ToolkitLock getLockForCache(Ehcache var1, String var2);

    public ToolkitNotifier<CacheDisposalNotification> getOrCreateCacheDisposalNotifier(Ehcache var1);

    public WanAwareToolkitCache<String, Serializable> getOrCreateWanAwareToolkitCache(String var1, String var2, CacheConfiguration var3, boolean var4, boolean var5);

    public void waitForOrchestrator(String var1);

    public void markCacheWanDisabled(String var1, String var2);

    public void linkClusteredCacheManager(String var1, Configuration var2);

    public void unlinkCache(String var1);

    public ToolkitListInternal getAsyncProcessingBucket(String var1, String var2);

    public void clusterRejoined();
}

