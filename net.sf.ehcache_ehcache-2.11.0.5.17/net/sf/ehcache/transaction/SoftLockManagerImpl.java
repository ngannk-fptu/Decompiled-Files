/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import net.sf.ehcache.transaction.AbstractSoftLockManager;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.SoftLockID;

public class SoftLockManagerImpl
extends AbstractSoftLockManager {
    private final ConcurrentMap<SoftLockID, Boolean> newKeyLocks = new ConcurrentHashMap<SoftLockID, Boolean>();
    private final ConcurrentMap<SoftLockID, SoftLock> allLocks = new ConcurrentHashMap<SoftLockID, SoftLock>();

    public SoftLockManagerImpl(String cacheName, SoftLockFactory lockFactory) {
        super(cacheName, lockFactory);
    }

    @Override
    protected ConcurrentMap<SoftLockID, SoftLock> getAllLocks() {
        return this.allLocks;
    }

    @Override
    protected ConcurrentMap<SoftLockID, Boolean> getNewKeyLocks() {
        return this.newKeyLocks;
    }
}

