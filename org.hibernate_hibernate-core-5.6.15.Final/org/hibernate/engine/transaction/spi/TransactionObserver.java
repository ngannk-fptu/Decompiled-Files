/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.spi;

public interface TransactionObserver {
    public void afterBegin();

    public void beforeCompletion();

    public void afterCompletion(boolean var1, boolean var2);
}

