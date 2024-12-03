/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.ReadCommittedSoftLockImpl;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.SoftLockManager;

public class ReadCommittedSoftLockFactory
implements SoftLockFactory {
    @Override
    public ReadCommittedSoftLockImpl newSoftLock(SoftLockManager manager, Object key) {
        return new ReadCommittedSoftLockImpl(manager, key);
    }
}

