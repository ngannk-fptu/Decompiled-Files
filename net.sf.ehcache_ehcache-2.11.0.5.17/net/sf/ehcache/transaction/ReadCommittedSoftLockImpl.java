/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionID;

public class ReadCommittedSoftLockImpl
implements SoftLock {
    private static final int PRIME = 31;
    private final SoftLockManager manager;
    private final ReentrantLock lock;
    private final ReentrantReadWriteLock freezeLock;
    private final Object key;
    private volatile boolean expired;

    ReadCommittedSoftLockImpl(SoftLockManager manager, Object key) {
        this.manager = manager;
        this.key = key;
        this.lock = new ReentrantLock();
        this.freezeLock = new ReentrantReadWriteLock();
    }

    @Override
    public Object getKey() {
        return this.key;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Element getElement(TransactionID currentTransactionId, SoftLockID softLockId) {
        this.freezeLock.readLock().lock();
        try {
            if (softLockId.getTransactionID().equals(currentTransactionId)) {
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

    @Override
    public void lock() {
        this.lock.lock();
    }

    @Override
    public boolean tryLock(long ms) throws InterruptedException {
        return this.lock.tryLock(ms, TimeUnit.MILLISECONDS);
    }

    @Override
    public void clearTryLock() {
        this.lock.unlock();
    }

    @Override
    public void unlock() {
        this.lock.unlock();
        this.clear();
    }

    private boolean isLocked() {
        return this.lock.isLocked();
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

    private boolean isFrozen() {
        return this.freezeLock.isWriteLocked();
    }

    @Override
    public boolean isExpired() {
        if (!this.expired) {
            this.expired = !this.isFrozen() && !this.isLocked();
        }
        return this.expired;
    }

    private void clear() {
        this.manager.clearSoftLock(this);
    }

    public String toString() {
        return "Soft Lock [clustered: false, isolation: rc, key: " + this.key + "]";
    }
}

