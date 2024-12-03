/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.transaction.support.TransactionCallback
 */
package com.atlassian.confluence.util.transaction;

import java.util.concurrent.Future;
import org.springframework.transaction.support.TransactionCallback;

public interface TransactionExecutor<K> {
    public Future<K> performTransactionAction(TransactionCallback var1);
}

