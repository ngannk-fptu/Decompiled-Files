/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.Set;
import net.sf.ehcache.Element;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLock;
import net.sf.ehcache.transaction.SoftLockID;
import net.sf.ehcache.transaction.TransactionID;
import net.sf.ehcache.transaction.local.LocalTransactionContext;

public interface SoftLockManager {
    public SoftLockID createSoftLockID(TransactionID var1, Object var2, Element var3, Element var4);

    public void clearSoftLock(SoftLock var1);

    public SoftLock findSoftLockById(SoftLockID var1);

    public Set<Object> getKeysInvisibleInContext(LocalTransactionContext var1, Store var2);

    public Set<SoftLock> collectAllSoftLocksForTransactionID(TransactionID var1);
}

