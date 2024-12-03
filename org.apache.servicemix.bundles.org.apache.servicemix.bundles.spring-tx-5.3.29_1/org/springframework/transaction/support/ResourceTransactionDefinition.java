/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.transaction.support;

import org.springframework.transaction.TransactionDefinition;

public interface ResourceTransactionDefinition
extends TransactionDefinition {
    public boolean isLocalResource();
}

