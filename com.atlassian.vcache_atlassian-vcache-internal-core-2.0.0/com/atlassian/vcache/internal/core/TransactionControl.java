/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.vcache.internal.core;

public interface TransactionControl {
    public void transactionSync();

    public boolean transactionDiscard();
}

