/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.transaction.impl.xa;

import com.hazelcast.transaction.TransactionContext;
import com.hazelcast.transaction.impl.Transaction;
import com.hazelcast.transaction.impl.xa.XATransactionContextImpl;

public final class TransactionAccessor {
    private TransactionAccessor() {
    }

    public static Transaction getTransaction(TransactionContext ctx) {
        if (ctx instanceof XATransactionContextImpl) {
            XATransactionContextImpl ctxImp = (XATransactionContextImpl)ctx;
            return ctxImp.getTransaction();
        }
        throw new IllegalArgumentException();
    }
}

