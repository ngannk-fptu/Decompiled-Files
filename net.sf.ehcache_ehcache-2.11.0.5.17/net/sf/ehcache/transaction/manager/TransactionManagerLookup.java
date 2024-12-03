/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 */
package net.sf.ehcache.transaction.manager;

import java.util.Properties;
import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;

public interface TransactionManagerLookup {
    public void init();

    public TransactionManager getTransactionManager();

    public void register(EhcacheXAResource var1, boolean var2);

    public void unregister(EhcacheXAResource var1, boolean var2);

    public void setProperties(Properties var1);
}

