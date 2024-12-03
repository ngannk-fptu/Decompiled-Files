/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.transaction.spi;

import org.hibernate.Transaction;

public interface TransactionImplementor
extends Transaction {
    @Deprecated
    default public void invalidate() {
    }

    public boolean isActive(boolean var1);
}

