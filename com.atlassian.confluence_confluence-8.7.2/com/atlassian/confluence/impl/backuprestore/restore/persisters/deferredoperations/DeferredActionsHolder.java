/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations;

import com.atlassian.confluence.impl.backuprestore.restore.persisters.deferredoperations.DeferredAction;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DeferredActionsHolder {
    private final Queue<DeferredAction> deferredActions = new ConcurrentLinkedQueue<DeferredAction>();

    public void addAction(DeferredAction deferredAction) {
        this.deferredActions.add(deferredAction);
    }

    public long runDeferredOperations() {
        DeferredAction action;
        int successfulOperationsCounter = 0;
        while ((action = this.deferredActions.poll()) != null) {
            successfulOperationsCounter += action.perform() ? 1 : 0;
        }
        return successfulOperationsCounter;
    }
}

