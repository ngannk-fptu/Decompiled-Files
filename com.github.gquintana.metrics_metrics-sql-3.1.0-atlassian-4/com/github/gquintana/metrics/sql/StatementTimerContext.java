/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;

public class StatementTimerContext {
    private final String sql;
    private final String sqlId;
    Timer timerContext;

    public StatementTimerContext(Timer timerContext, String sql, String sqlId) {
        this.sql = sql;
        this.sqlId = sqlId;
        this.timerContext = timerContext;
    }

    public Timer getTimerContext() {
        return this.timerContext;
    }

    public String getSql() {
        return this.sql;
    }

    public String getSqlId() {
        return this.sqlId;
    }
}

