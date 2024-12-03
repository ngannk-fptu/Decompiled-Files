/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 */
package org.springframework.transaction.support;

import org.springframework.core.Ordered;
import org.springframework.transaction.support.TransactionSynchronization;

@Deprecated
public abstract class TransactionSynchronizationAdapter
implements TransactionSynchronization,
Ordered {
    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }

    @Override
    public void suspend() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void flush() {
    }

    @Override
    public void beforeCommit(boolean readOnly) {
    }

    @Override
    public void beforeCompletion() {
    }

    @Override
    public void afterCommit() {
    }

    @Override
    public void afterCompletion(int status) {
    }
}

