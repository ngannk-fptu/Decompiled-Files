/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpTimer;

public interface OpTimerFactory {
    public OpTimer createOpTimer(String var1, boolean var2, OpTimer.OnEndCallback var3);
}

