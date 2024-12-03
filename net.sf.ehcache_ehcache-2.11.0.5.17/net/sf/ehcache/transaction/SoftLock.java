/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.Element;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.TransactionID;

public interface SoftLock {
    public Object getKey();

    public Element getElement(TransactionID var1, SoftLockID var2);

    public void lock();

    public boolean tryLock(long var1) throws InterruptedException;

    public void clearTryLock();

    public void unlock();

    public void freeze();

    public void unfreeze();

    public boolean isExpired();
}

