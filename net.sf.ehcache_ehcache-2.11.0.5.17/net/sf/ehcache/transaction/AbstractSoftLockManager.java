/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.local.LocalTransactionContext;

public abstract class AbstractSoftLockManager
implements SoftLockManager {
    private final String cacheName;
    private final SoftLockFactory lockFactory;

    public AbstractSoftLockManager(String cacheName, SoftLockFactory lockFactory) {
        this.cacheName = cacheName;
        this.lockFactory = lockFactory;
    }

    protected abstract ConcurrentMap<SoftLockID, SoftLock> getAllLocks();

    protected abstract ConcurrentMap<SoftLockID, Boolean> getNewKeyLocks();

    @Override
    public SoftLockID createSoftLockID(TransactionID transactionID, Object key, Element newElement, Element oldElement) {
        if (newElement != null && newElement.getObjectValue() instanceof SoftLockID) {
            throw new AssertionError((Object)"newElement must not contain a soft lock ID");
        }
        if (oldElement != null && oldElement.getObjectValue() instanceof SoftLockID) {
            throw new AssertionError((Object)"oldElement must not contain a soft lock ID");
        }
        SoftLockID lockId = new SoftLockID(transactionID, key, newElement, oldElement);
        if (this.getAllLocks().containsKey(lockId)) {
            return lockId;
        }
        SoftLock lock = this.lockFactory.newSoftLock(this, key);
        if (this.getAllLocks().putIfAbsent(lockId, lock) != null) {
            throw new AssertionError();
        }
        if (oldElement == null) {
            this.getNewKeyLocks().put(lockId, Boolean.TRUE);
        }
        return lockId;
    }

    @Override
    public SoftLock findSoftLockById(SoftLockID softLockId) {
        return (SoftLock)this.getAllLocks().get(softLockId);
    }

    @Override
    public Set<Object> getKeysInvisibleInContext(LocalTransactionContext currentTransactionContext, Store underlyingStore) {
        HashSet<Object> invisibleKeys = new HashSet<Object>();
        invisibleKeys.addAll(this.getNewKeys());
        List<SoftLock> currentTransactionContextSoftLocks = currentTransactionContext.getSoftLocksForCache(this.cacheName);
        for (SoftLock softLock : currentTransactionContextSoftLocks) {
            Element e = underlyingStore.getQuiet(softLock.getKey());
            if (!(e.getObjectValue() instanceof SoftLockID)) continue;
            SoftLockID softLockId = (SoftLockID)e.getObjectValue();
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
        for (Map.Entry entry : this.getAllLocks().entrySet()) {
            if (!((SoftLockID)entry.getKey()).getTransactionID().equals(transactionID)) continue;
            result.add((SoftLock)entry.getValue());
        }
        return result;
    }

    @Override
    public void clearSoftLock(SoftLock softLock) {
        for (Map.Entry entry : this.getAllLocks().entrySet()) {
            if (entry.getValue() != softLock) continue;
            this.getAllLocks().remove(entry.getKey());
            this.getNewKeyLocks().remove(entry.getKey());
            break;
        }
    }

    private Set<Object> getNewKeys() {
        HashSet<Object> result = new HashSet<Object>();
        for (SoftLockID softLock : this.getNewKeyLocks().keySet()) {
            result.add(softLock.getKey());
        }
        return result;
    }
}

