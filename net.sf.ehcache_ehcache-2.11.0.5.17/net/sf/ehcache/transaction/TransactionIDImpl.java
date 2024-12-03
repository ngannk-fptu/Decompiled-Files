/*
 * Decompiled with CFR 0.152.
 */
package net.sf.ehcache.transaction;

import java.util.concurrent.atomic.AtomicInteger;
import net.sf.ehcache.transaction.TransactionID;

public class TransactionIDImpl
implements TransactionID {
    private static final AtomicInteger ID_GENERATOR = new AtomicInteger();
    private final int id;

    public TransactionIDImpl() {
        this.id = ID_GENERATOR.getAndIncrement();
    }

    protected TransactionIDImpl(TransactionIDImpl transactionId) {
        TransactionIDImpl txIdImpl = transactionId;
        this.id = txIdImpl.id;
    }

    public final boolean equals(Object obj) {
        if (obj instanceof TransactionIDImpl) {
            TransactionIDImpl otherId = (TransactionIDImpl)obj;
            return this.id == otherId.id;
        }
        return false;
    }

    public final int hashCode() {
        return this.id;
    }

    public String toString() {
        return Integer.toString(this.id);
    }
}

