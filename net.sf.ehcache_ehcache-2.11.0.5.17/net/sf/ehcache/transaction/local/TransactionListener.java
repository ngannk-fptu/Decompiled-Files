/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction.local;

public interface TransactionListener {
    public void beforeCommit();

    public void afterCommit();

    public void afterRollback();
}

