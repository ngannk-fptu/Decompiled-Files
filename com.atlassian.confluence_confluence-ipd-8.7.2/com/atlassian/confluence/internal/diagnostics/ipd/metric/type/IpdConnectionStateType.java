/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.internal.diagnostics.ipd.metric.type;

import com.atlassian.confluence.internal.diagnostics.ipd.metric.type.IpdConnectionStateMxBean;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class IpdConnectionStateType
implements IpdConnectionStateMxBean {
    private final AtomicBoolean connected = new AtomicBoolean(true);
    private final AtomicLong totalFailures = new AtomicLong(0L);

    public void setConnected(boolean connected) {
        this.connected.set(connected);
        if (!connected) {
            this.totalFailures.incrementAndGet();
        }
    }

    @Override
    public boolean isConnected() {
        return this.connected.get();
    }

    @Override
    public long getTotalFailures() {
        return this.totalFailures.get();
    }
}

