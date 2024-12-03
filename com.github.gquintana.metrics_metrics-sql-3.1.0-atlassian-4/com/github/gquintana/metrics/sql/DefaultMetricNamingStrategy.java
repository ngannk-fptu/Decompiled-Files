/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.EventType;
import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.sql.MetricNamingStrategy;
import com.github.gquintana.metrics.sql.StatementTimerContext;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Optional;
import javax.sql.PooledConnection;

public class DefaultMetricNamingStrategy
implements MetricNamingStrategy {
    protected String getSqlId(String sql) {
        return "[" + sql.toLowerCase() + "]";
    }

    protected Timer startTimer(Class<?> clazz, String databaseName) {
        Timer timer = new Timer(clazz, databaseName, Optional.empty(), Optional.empty(), Optional.empty());
        timer.start();
        return timer;
    }

    protected Timer startTimer(Class<?> clazz, String databaseName, String sql) {
        Timer timer = new Timer(clazz, databaseName, Optional.of(sql), Optional.empty(), Optional.empty());
        timer.start();
        return timer;
    }

    protected Timer startTimer(Class<?> clazz, String databaseName, String sql, String sqlId) {
        Timer timer = new Timer(clazz, databaseName, Optional.of(sql), Optional.of(sqlId), Optional.empty());
        timer.start();
        return timer;
    }

    protected Timer startTimer(Class<?> clazz, String databaseName, String sql, String sqlId, EventType eventType) {
        Timer timer = new Timer(clazz, databaseName, Optional.of(sql), Optional.of(sqlId), Optional.of(eventType));
        timer.start();
        return timer;
    }

    @Override
    public Timer startPooledConnectionTimer(String databaseName) {
        return this.startTimer(PooledConnection.class, databaseName);
    }

    @Override
    public Timer startConnectionTimer(String databaseName) {
        return this.startTimer(Connection.class, databaseName);
    }

    @Override
    public Timer startStatementTimer(String databaseName) {
        return this.startTimer(Statement.class, databaseName);
    }

    protected StatementTimerContext startStatementTimer(Class<? extends Statement> clazz, String databaseName, String sql, String sqlId) {
        String lSqlId = sqlId == null ? this.getSqlId(sql) : sqlId;
        Timer timerContext = this.startTimer(clazz, databaseName, sql, lSqlId);
        return new StatementTimerContext(timerContext, sql, lSqlId);
    }

    protected StatementTimerContext startStatementExecuteTimer(Class<? extends Statement> clazz, String databaseName, String sql, String sqlId) {
        String lSqlId = sqlId == null ? this.getSqlId(sql) : sqlId;
        Timer timerContext = this.startTimer(clazz, databaseName, sql, lSqlId, EventType.EXECUTION);
        return new StatementTimerContext(timerContext, sql, lSqlId);
    }

    @Override
    public StatementTimerContext startStatementExecuteTimer(String databaseName, String sql) {
        return this.startStatementExecuteTimer(Statement.class, databaseName, sql, null);
    }

    @Override
    public StatementTimerContext startPreparedStatementTimer(String databaseName, String sql, String sqlId) {
        return this.startStatementTimer(PreparedStatement.class, databaseName, sql, sqlId);
    }

    @Override
    public StatementTimerContext startPreparedStatementExecuteTimer(String databaseName, String sql, String sqlId) {
        return this.startStatementExecuteTimer(PreparedStatement.class, databaseName, sql, sqlId);
    }

    @Override
    public StatementTimerContext startCallableStatementTimer(String databaseName, String sql, String sqlId) {
        return this.startStatementTimer(CallableStatement.class, databaseName, sql, sqlId);
    }

    @Override
    public StatementTimerContext startCallableStatementExecuteTimer(String databaseName, String sql, String sqlId) {
        return this.startStatementExecuteTimer(CallableStatement.class, databaseName, sql, sqlId);
    }

    @Override
    public Timer startResultSetTimer(String databaseName, String sql, String sqlId) {
        return this.startTimer(ResultSet.class, databaseName, sql);
    }
}

