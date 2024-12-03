/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import com.atlassian.instrumentation.operations.SimpleOpTimer;

public class SimpleOpTimerFactory
implements OpTimerFactory {
    @Override
    public OpTimer createOpTimer(String name, boolean captureCPUCost, OpTimer.OnEndCallback endCallback) {
        return new SimpleOpTimer(name, captureCPUCost, endCallback);
    }
}

