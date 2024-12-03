/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.sql.StatementTimerContext;

public interface MetricNamingStrategy {
    public Timer startPooledConnectionTimer(String var1);

    public Timer startConnectionTimer(String var1);

    public Timer startStatementTimer(String var1);

    public StatementTimerContext startStatementExecuteTimer(String var1, String var2);

    public StatementTimerContext startPreparedStatementTimer(String var1, String var2, String var3);

    public StatementTimerContext startPreparedStatementExecuteTimer(String var1, String var2, String var3);

    public StatementTimerContext startCallableStatementTimer(String var1, String var2, String var3);

    public StatementTimerContext startCallableStatementExecuteTimer(String var1, String var2, String var3);

    public Timer startResultSetTimer(String var1, String var2, String var3);
}

