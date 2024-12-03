/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.diagnostics.internal.platform.monitor.db;

import java.time.Duration;

public class DatabaseOperationDiagnostic {
    private final String sql;
    private final Duration executionTime;
    private final String threadName;

    public DatabaseOperationDiagnostic(String sql, Duration executionTime, String threadName) {
        this.sql = sql;
        this.executionTime = executionTime;
        this.threadName = threadName;
    }

    public String getSql() {
        return this.sql;
    }

    public Duration getExecutionTime() {
        return this.executionTime;
    }

    public String getThreadName() {
        return this.threadName;
    }
}

