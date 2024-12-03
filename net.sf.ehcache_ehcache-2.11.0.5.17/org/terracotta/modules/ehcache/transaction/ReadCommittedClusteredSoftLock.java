/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.terracotta.toolkit.concurrent.locks.ToolkitLock
 *  org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock
 */
package org.terracotta.modules.ehcache.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.TransactionID;
import org.terracotta.modules.ehcache.ToolkitInstanceFactory;
import org.terracotta.modules.ehcache.transaction.ReadCommittedClusteredSoftLockFactory;
import org.terracotta.toolkit.concurrent.locks.ToolkitLock;
import org.terracotta.toolkit.concurrent.locks.ToolkitReadWriteLock;

public class ReadCommittedClusteredSoftLock
implements SoftLock {
    private final TransactionID transactionID;
    private final Object deserializedKey;
    private final ReadCommittedClusteredSoftLockFactory factory;
    private final ToolkitLock lock;
    private final ToolkitReadWriteLock freezeLock;
    private final ToolkitReadWriteLock notificationLock;
    private final Condition notifier;
    private boolean expired;

    ReadCommittedClusteredSoftLock(ToolkitInstanceFactory toolkitInstanceFactory, ReadCommittedClusteredSoftLockFactory factory, TransactionID transactionID, Object key) {
        this.deserializedKey = key;
        this.transactionID = transactionID;
        this.factory = factory;
        String cacheManagerName = factory.getCacheManagerName();
        String cacheName = factory.getCacheName();
        this.lock = toolkitInstanceFactory.getSoftLockWriteLock(cacheManagerName, cacheName, transactionID, this.deserializedKey);
        this.freezeLock = toolkitInstanceFactory.getSoftLockFreezeLock(cacheManagerName, cacheName, transactionID, this.deserializedKey);
        this.notificationLock = toolkitInstanceFactory.getSoftLockNotifierLock(cacheManagerName, cacheName, transactionID, this.deserializedKey);
        this.notifier = this.notificationLock.writeLock().getCondition();
    }

    @Override
    public Object getKey() {
        return this.deserializedKey;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getElement(TransactionID currentTransactionId, SoftLockID softLockId) {
        this.freezeLock.readLock().lock();
        try {
            if (this.transactionID.equals(currentTransactionId)) {
                Element element = softLockId.getNewElement();
                return element;
            }
            Element element = softLockId.getOldElement();
            return element;
        }
        finally {
            this.freezeLock.readLock().unlock();
        }
    }

    public TransactionID getTransactionID() {
        return this.transactionID;
    }

    @Override
    public void lock() {
        this.lock.lock();
        if (this.isExpired()) {
            this.notificationLock.writeLock().lock();
            try {
                this.notifier.signalAll();
            }
            finally {
                this.notificationLock.writeLock().unlock();
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean tryLock(long ms) throws InterruptedException {
        if (this.isExpired() && this.factory.getLock(this.transactionID, this.getKey()) != null) {
            this.notificationLock.writeLock().lock();
            try {
                while (!this.isLocked()) {
                    boolean canLock = this.notifier.await(ms, TimeUnit.MILLISECONDS);
                    if (canLock) continue;
                    boolean bl = false;
                    return bl;
                }
            }
            finally {
                this.notificationLock.writeLock().unlock();
            }
        }
        return this.lock.tryLock(ms, TimeUnit.MILLISECONDS);
    }

    @Override
    public void clearTryLock() {
        this.lock.unlock();
    }

    @Override
    public void unlock() {
        this.clear();
        this.lock.unlock();
    }

    boolean isLocked() {
        return ReadCommittedClusteredSoftLock.isLocked(this.lock);
    }

    @Override
    public void freeze() {
        if (!this.isLocked()) {
            throw new IllegalStateException("cannot freeze an unlocked soft lock");
        }
        this.freezeLock.writeLock().lock();
    }

    @Override
    public void unfreeze() {
        this.freezeLock.writeLock().unlock();
    }

    @Override
    public synchronized boolean isExpired() {
        if (!this.expired) {
            this.expired = !this.isFrozen() && !this.isLocked();
        }
        return this.expired;
    }

    public void clear() {
        this.factory.clearSoftLock(this);
    }

    private boolean isFrozen() {
        return ReadCommittedClusteredSoftLock.isLocked(this.freezeLock.writeLock());
    }

    private static boolean isLocked(ToolkitLock lock) {
        if (lock.isHeldByCurrentThread()) {
            return true;
        }
        boolean gotLock = lock.tryLock();
        if (gotLock) {
            lock.unlock();
            return false;
        }
        return true;
    }

    public boolean equals(Object object) {
        if (object instanceof ReadCommittedClusteredSoftLock) {
            ReadCommittedClusteredSoftLock other = (ReadCommittedClusteredSoftLock)object;
            if (!this.transactionID.equals(other.transactionID)) {
                return false;
            }
            return this.getKey().equals(other.getKey());
        }
        return false;
    }

    public int hashCode() {
        int hashCode = 31;
        hashCode *= this.transactionID.hashCode();
        return hashCode *= this.getKey().hashCode();
    }

    public String toString() {
        return "Soft Lock [clustered: true, isolation: rc, transactionID: " + this.transactionID + ", key: " + this.getKey() + "]";
    }
}

