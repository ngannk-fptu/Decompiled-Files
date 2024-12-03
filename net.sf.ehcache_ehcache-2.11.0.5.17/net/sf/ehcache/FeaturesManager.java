/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.pool.Pool;
import net.sf.ehcache.store.Store;
import net.sf.ehcache.transaction.SoftLockFactory;
import net.sf.ehcache.transaction.SoftLockManager;
import net.sf.ehcache.transaction.TransactionIDFactory;
import net.sf.ehcache.writer.writebehind.WriteBehind;

public interface FeaturesManager {
    public static final String ENTERPRISE_FM_CLASSNAME = "net.sf.ehcache.EnterpriseFeaturesManager";

    public WriteBehind createWriteBehind(Cache var1);

    public Store createStore(Cache var1, Pool var2, Pool var3);

    public TransactionIDFactory createTransactionIDFactory();

    public SoftLockManager createSoftLockManager(Ehcache var1, SoftLockFactory var2);

    public void startup();

    public void dispose();
}

