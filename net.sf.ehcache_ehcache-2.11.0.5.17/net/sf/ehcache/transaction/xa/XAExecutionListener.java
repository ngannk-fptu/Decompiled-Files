/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.xa;

import net.sf.ehcache.transaction.xa.EhcacheXAResource;

public interface XAExecutionListener {
    public void beforePrepare(EhcacheXAResource var1);

    public void afterCommitOrRollback(EhcacheXAResource var1);
}

