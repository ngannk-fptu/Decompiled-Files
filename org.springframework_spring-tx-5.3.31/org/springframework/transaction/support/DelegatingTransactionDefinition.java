/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 */
package org.springframework.transaction.support;

import java.io.Serializable;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.util.Assert;

public abstract class DelegatingTransactionDefinition
implements TransactionDefinition,
Serializable {
    private final TransactionDefinition targetDefinition;

    public DelegatingTransactionDefinition(TransactionDefinition targetDefinition) {
        Assert.notNull((Object)targetDefinition, (String)"Target definition must not be null");
        this.targetDefinition = targetDefinition;
    }

    @Override
    public int getPropagationBehavior() {
        return this.targetDefinition.getPropagationBehavior();
    }

    @Override
    public int getIsolationLevel() {
        return this.targetDefinition.getIsolationLevel();
    }

    @Override
    public int getTimeout() {
        return this.targetDefinition.getTimeout();
    }

    @Override
    public boolean isReadOnly() {
        return this.targetDefinition.isReadOnly();
    }

    @Override
    @Nullable
    public String getName() {
        return this.targetDefinition.getName();
    }

    public boolean equals(@Nullable Object other) {
        return this.targetDefinition.equals(other);
    }

    public int hashCode() {
        return this.targetDefinition.hashCode();
    }

    public String toString() {
        return this.targetDefinition.toString();
    }
}

