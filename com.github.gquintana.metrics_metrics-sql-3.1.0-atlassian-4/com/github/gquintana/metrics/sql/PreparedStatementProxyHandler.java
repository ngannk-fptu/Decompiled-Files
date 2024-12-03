/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.AbstractStatementProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.StatementTimerContext;
import java.sql.PreparedStatement;

public class PreparedStatementProxyHandler
extends AbstractStatementProxyHandler<PreparedStatement> {
    private final String sql;
    private final String sqlId;

    public PreparedStatementProxyHandler(PreparedStatement delegate, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext, String sql, String sqlId) {
        super(delegate, PreparedStatement.class, name, proxyFactory, lifeTimerContext);
        this.sql = sql;
        this.sqlId = sqlId;
    }

    @Override
    protected final Object execute(MethodInvocation<PreparedStatement> methodInvocation) throws Throwable {
        String lSqlId;
        String lSql;
        if (methodInvocation.getArgCount() > 0) {
            lSql = methodInvocation.getArgAt(0, String.class);
            lSqlId = null;
        } else {
            lSql = this.sql;
            lSqlId = this.sqlId;
        }
        StatementTimerContext timerContext = this.proxyFactory.startPreparedStatementExecuteTimer(this.name, lSql, lSqlId);
        Object result = methodInvocation.proceed();
        result = this.stopTimer(timerContext, result);
        return result;
    }
}

