/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 */
package net.sf.ehcache.transaction.manager.selector;

import javax.transaction.TransactionManager;
import net.sf.ehcache.transaction.xa.EhcacheXAResource;

public abstract class Selector {
    private final String vendor;
    private volatile TransactionManager transactionManager;

    protected Selector(String vendor) {
        this.vendor = vendor;
    }

    public String getVendor() {
        return this.vendor;
    }

    public TransactionManager getTransactionManager() {
        if (this.transactionManager == null) {
            this.transactionManager = this.doLookup();
        }
        return this.transactionManager;
    }

    public void registerResource(EhcacheXAResource ehcacheXAResource, boolean forRecovery) {
    }

    public void unregisterResource(EhcacheXAResource ehcacheXAResource, boolean forRecovery) {
    }

    protected abstract TransactionManager doLookup();
}

