/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import net.sf.ehcache.transaction.TransactionIDImpl;

public class ExpiredTransactionIDImpl
extends TransactionIDImpl {
    public ExpiredTransactionIDImpl(TransactionIDImpl transactionId) {
        super(transactionId);
    }
}

