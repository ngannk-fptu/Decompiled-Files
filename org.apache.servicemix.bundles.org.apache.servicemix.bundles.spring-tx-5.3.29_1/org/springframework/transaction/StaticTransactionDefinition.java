/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction;

import org.springframework.transaction.TransactionDefinition;

final class StaticTransactionDefinition
implements TransactionDefinition {
    static final StaticTransactionDefinition INSTANCE = new StaticTransactionDefinition();

    private StaticTransactionDefinition() {
    }
}

