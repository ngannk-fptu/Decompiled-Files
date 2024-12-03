/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa.commands;

import net.sf.ehcache.store.ElementValueComparator;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.xa.XidTransactionID;

public interface Command {
    public boolean isPut(Object var1);

    public boolean isRemove(Object var1);

    public boolean prepare(Store var1, SoftLockManager var2, XidTransactionID var3, ElementValueComparator var4);

    public void rollback(Store var1, SoftLockManager var2);

    public Object getObjectKey();
}

