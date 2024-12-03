/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.operations;

import com.atlassian.instrumentation.operations.OpSnapshot;
import com.atlassian.instrumentation.operations.OpTimer;
import com.atlassian.instrumentation.operations.OpTimerFactory;
import com.atlassian.instrumentation.operations.registry.OpRegistry;
import java.util.List;

public interface ThreadOpTimerFactory
extends OpTimerFactory {
    @Override
    public OpTimer createOpTimer(String var1, boolean var2, OpTimer.OnEndCallback var3);

    public OpRegistry getOpRegistry();

    public List<OpSnapshot> snapshotAndClear();
}

