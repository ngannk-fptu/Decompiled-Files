/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.Ordered
 */
package org.springframework.transaction.support;

import java.io.Flushable;
import org.springframework.core.Ordered;

public interface TransactionSynchronization
extends Ordered,
Flushable {
    public static final int STATUS_COMMITTED = 0;
    public static final int STATUS_ROLLED_BACK = 1;
    public static final int STATUS_UNKNOWN = 2;

    default public int getOrder() {
        return Integer.MAX_VALUE;
    }

    default public void suspend() {
    }

    default public void resume() {
    }

    @Override
    default public void flush() {
    }

    default public void beforeCommit(boolean readOnly) {
    }

    default public void beforeCompletion() {
    }

    default public void afterCommit() {
    }

    default public void afterCompletion(int status) {
    }
}

