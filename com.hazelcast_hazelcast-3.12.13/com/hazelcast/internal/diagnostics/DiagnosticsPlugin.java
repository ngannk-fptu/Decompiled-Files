/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.diagnostics;

import com.hazelcast.internal.diagnostics.DiagnosticsLogWriter;
import com.hazelcast.logging.ILogger;

public abstract class DiagnosticsPlugin {
    static final long STATIC = -1L;
    static final long DISABLED = 0L;
    protected final ILogger logger;

    public DiagnosticsPlugin(ILogger logger) {
        this.logger = logger;
    }

    public abstract long getPeriodMillis();

    public abstract void onStart();

    public abstract void run(DiagnosticsLogWriter var1);
}

