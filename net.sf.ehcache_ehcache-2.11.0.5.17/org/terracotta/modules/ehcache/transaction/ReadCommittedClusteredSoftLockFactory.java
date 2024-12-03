/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.collections.ToolkitMap
 */
package org.terracotta.modules.ehcache.transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.local.LocalTransactionContext;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.collections.SerializedToolkitCache;
import org.terracotta.modules.ehcache.transaction.ClusteredSoftLockIDKey;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLock;
import org.terracotta.modules.ehcache.transaction.SerializedReadCommittedClusteredSoftLock;
import org.terracotta.toolkit.collections.ToolkitMap;

public class ReadCommittedClusteredSoftLockFactory
implements SoftLockManager {
    private static final Integer DEFAULT_DUMMY_VALUE = 0;
    private final String cacheName;
    private final String cacheManagerName;
    private final ToolkitInstanceFactory toolkitInstanceFactory;
    private final ToolkitMap<SerializedReadCommittedClusteredSoftLock, Integer> newKeyLocks;
    private final SerializedToolkitCache<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock> allLocks;

    public ReadCommittedClusteredSoftLockFactory(ToolkitInstanceFactory toolkitInstanceFactory, String cacheManagerName, String cacheName) {
        this.toolkitInstanceFactory = toolkitInstanceFactory;
        this.cacheManagerName = cacheManagerName;
        this.cacheName = cacheName;
        this.allLocks = toolkitInstanceFactory.getOrCreateAllSoftLockMap(cacheManagerName, cacheName);
        this.newKeyLocks = toolkitInstanceFactory.getOrCreateNewSoftLocksSet(cacheManagerName, cacheName);
    }

    @Override
    public SoftLockID createSoftLockID(TransactionID transactionID, Object key, Element newElement, Element oldElement) {
        if (newElement != null && newElement.getObjectValue() instanceof SoftLockID) {
            throw new AssertionError((Object)"newElement must not contain a soft lock ID");
        }
        if (oldElement != null && oldElement.getObjectValue() instanceof SoftLockID) {
            throw new AssertionError((Object)"oldElement must not contain a soft lock ID");
        }
        SoftLockID lockId = new SoftLockID(transactionID, key, newElement, oldElement);
        ClusteredSoftLockIDKey clusteredId = new ClusteredSoftLockIDKey(lockId);
        if (this.allLocks.containsKey(clusteredId)) {
            return lockId;
        }
        SerializedReadCommittedClusteredSoftLock softLock = new SerializedReadCommittedClusteredSoftLock(transactionID, key);
        if (this.allLocks.putIfAbsent(clusteredId, softLock) != null) {
            throw new AssertionError();
        }
        if (oldElement == null) {
            this.newKeyLocks.put((Object)softLock, (Object)DEFAULT_DUMMY_VALUE);
        }
        return lockId;
    }

    @Override
    public SoftLock findSoftLockById(SoftLockID softLockId) {
        SerializedReadCommittedClusteredSoftLock serializedSoftLock = (SerializedReadCommittedClusteredSoftLock)this.allLocks.get(new ClusteredSoftLockIDKey(softLockId));
        if (serializedSoftLock == null) {
            return null;
        }
        return serializedSoftLock.getSoftLock(this.toolkitInstanceFactory, this);
    }

    ReadCommittedClusteredSoftLock getLock(TransactionID transactionId, Object key) {
        for (Map.Entry<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock> entry : this.allLocks.entrySet()) {
            SerializedReadCommittedClusteredSoftLock serialized = entry.getValue();
            ReadCommittedClusteredSoftLock readCommittedSoftLock = serialized.getSoftLock(this.toolkitInstanceFactory, this);
            if (!readCommittedSoftLock.getTransactionID().equals(transactionId) || !readCommittedSoftLock.getKey().equals(key)) continue;
            return readCommittedSoftLock;
        }
        return null;
    }

    @Override
    public Set<Object> getKeysInvisibleInContext(LocalTransactionContext currentTransactionContext, Store underlyingStore) {
        HashSet<Object> invisibleKeys = new HashSet<Object>();
        invisibleKeys.addAll(this.getNewKeys());
        List<SoftLock> currentTransactionContextSoftLocks = currentTransactionContext.getSoftLocksForCache(this.cacheName);
        for (SoftLock softLock : currentTransactionContextSoftLocks) {
            SoftLockID softLockId = (SoftLockID)underlyingStore.getQuiet(softLock.getKey()).getObjectValue();
            if (softLock.getElement(currentTransactionContext.getTransactionId(), softLockId) == null) {
                invisibleKeys.add(softLock.getKey());
                continue;
            }
            invisibleKeys.remove(softLock.getKey());
        }
        return invisibleKeys;
    }

    @Override
    public Set<SoftLock> collectAllSoftLocksForTransactionID(TransactionID transactionID) {
        HashSet<SoftLock> result = new HashSet<SoftLock>();
        for (Map.Entry<ClusteredSoftLockIDKey, SerializedReadCommittedClusteredSoftLock> entry : this.allLocks.entrySet()) {
            SerializedReadCommittedClusteredSoftLock serialized = entry.getValue();
            ReadCommittedClusteredSoftLock softLock = serialized.getSoftLock(this.toolkitInstanceFactory, this);
            if (!softLock.getTransactionID().equals(transactionID)) continue;
            result.add(softLock);
        }
        return result;
    }

    @Override
    public void clearSoftLock(SoftLock softLock) {
        SoftLockID softLockId = new SoftLockID(((ReadCommittedClusteredSoftLock)softLock).getTransactionID(), softLock.getKey(), null, null);
        ClusteredSoftLockIDKey clusteredIdKey = new ClusteredSoftLockIDKey(softLockId);
        SerializedReadCommittedClusteredSoftLock serializedClusteredSoftLock = (SerializedReadCommittedClusteredSoftLock)this.allLocks.remove(clusteredIdKey);
        this.newKeyLocks.remove((Object)serializedClusteredSoftLock);
    }

    private Set<Object> getNewKeys() {
        HashSet<Object> result = new HashSet<Object>();
        for (SerializedReadCommittedClusteredSoftLock serialized : this.newKeyLocks.keySet()) {
            result.add(serialized.getSoftLock(this.toolkitInstanceFactory, this).getKey());
        }
        return result;
    }

    String getCacheName() {
        return this.cacheName;
    }

    String getCacheManagerName() {
        return this.cacheManagerName;
    }
}

