/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockManager;

public interface SoftLockFactory {
    public SoftLock newSoftLock(SoftLockManager var1, Object var2);
}

