/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.ProxyFactory;
import com.github.gquintana.metrics.proxy.ReflectProxyFactory;
import com.github.gquintana.metrics.sql.CallableStatementProxyHandler;
import com.github.gquintana.metrics.sql.ConnectionProxyHandler;
import com.github.gquintana.metrics.sql.DataSourceProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import com.github.gquintana.metrics.sql.MetricNamingStrategy;
import com.github.gquintana.metrics.sql.PooledConnectionProxyHandler;
import com.github.gquintana.metrics.sql.PreparedStatementProxyHandler;
import com.github.gquintana.metrics.sql.ResultSetProxyHandler;
import com.github.gquintana.metrics.sql.StatementProxyHandler;
import com.github.gquintana.metrics.sql.StatementTimerContext;
import com.github.gquintana.metrics.sql.XADataSourceProxyHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import javax.sql.RowSet;
import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.FilteredRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.WebRowSet;

public class JdbcProxyFactory {
    private final MetricNamingStrategy metricNamingStrategy;
    private final ProxyFactory proxyFactory;

    public JdbcProxyFactory(MetricNamingStrategy namingStrategy) {
        this(namingStrategy, new ReflectProxyFactory());
    }

    public JdbcProxyFactory(MetricNamingStrategy namingStrategy, ProxyFactory proxyFactory) {
        this.metricNamingStrategy = namingStrategy;
        this.proxyFactory = proxyFactory;
    }

    private <T> T newProxy(JdbcProxyHandler<T> proxyHandler) {
        return this.proxyFactory.newProxy(proxyHandler, proxyHandler.getProxyClass());
    }

    public DataSource wrapDataSource(String connectionFactoryName, DataSource wrappedDataSource) {
        return this.newProxy(new DataSourceProxyHandler(wrappedDataSource, connectionFactoryName, this));
    }

    public XADataSource wrapXADataSource(String connectionFactoryName, XADataSource wrappedDataSource) {
        return this.newProxy(new XADataSourceProxyHandler(wrappedDataSource, connectionFactoryName, this));
    }

    public PooledConnection wrapPooledConnection(String connectionFactoryName, PooledConnection wrappedConnection) {
        Timer lifeTimerContext = this.metricNamingStrategy.startPooledConnectionTimer(connectionFactoryName);
        return this.newProxy(new PooledConnectionProxyHandler<PooledConnection>(wrappedConnection, PooledConnection.class, connectionFactoryName, this, lifeTimerContext));
    }

    public XAConnection wrapXAConnection(String connectionFactoryName, XAConnection wrappedConnection) {
        Timer lifeTimerContext = this.metricNamingStrategy.startPooledConnectionTimer(connectionFactoryName);
        return this.newProxy(new PooledConnectionProxyHandler<XAConnection>(wrappedConnection, XAConnection.class, connectionFactoryName, this, lifeTimerContext));
    }

    public Connection wrapConnection(String connectionFactoryName, Connection wrappedConnection) {
        Timer lifeTimerContext = this.metricNamingStrategy.startConnectionTimer(connectionFactoryName);
        return this.newProxy(new ConnectionProxyHandler(wrappedConnection, connectionFactoryName, this, lifeTimerContext));
    }

    public Statement wrapStatement(String connectionFactoryName, Statement statement) {
        Timer lifeTimerContext = this.metricNamingStrategy.startStatementTimer(connectionFactoryName);
        return this.newProxy(new StatementProxyHandler(statement, connectionFactoryName, this, lifeTimerContext));
    }

    public StatementTimerContext startStatementExecuteTimer(String connectionFactoryName, String sql) {
        return this.metricNamingStrategy.startStatementExecuteTimer(connectionFactoryName, sql);
    }

    public PreparedStatement wrapPreparedStatement(String connectionFactoryName, PreparedStatement preparedStatement, String sql) {
        StatementTimerContext lifeTimerContext = this.metricNamingStrategy.startPreparedStatementTimer(connectionFactoryName, sql, null);
        PreparedStatementProxyHandler proxyHandler = lifeTimerContext == null ? new PreparedStatementProxyHandler(preparedStatement, connectionFactoryName, this, null, sql, null) : new PreparedStatementProxyHandler(preparedStatement, connectionFactoryName, this, lifeTimerContext.getTimerContext(), lifeTimerContext.getSql(), lifeTimerContext.getSqlId());
        return this.newProxy(proxyHandler);
    }

    public StatementTimerContext startPreparedStatementExecuteTimer(String connectionFactoryName, String sql, String sqlId) {
        return this.metricNamingStrategy.startPreparedStatementExecuteTimer(connectionFactoryName, sql, sqlId);
    }

    public CallableStatement wrapCallableStatement(String connectionFactoryName, CallableStatement callableStatement, String sql) {
        StatementTimerContext lifeTimerContext = this.metricNamingStrategy.startCallableStatementTimer(connectionFactoryName, sql, null);
        CallableStatementProxyHandler proxyHandler = lifeTimerContext == null ? new CallableStatementProxyHandler(callableStatement, connectionFactoryName, this, null, sql, null) : new CallableStatementProxyHandler(callableStatement, connectionFactoryName, this, lifeTimerContext.getTimerContext(), lifeTimerContext.getSql(), lifeTimerContext.getSqlId());
        return this.newProxy(proxyHandler);
    }

    public StatementTimerContext startCallableStatementExecuteTimer(String connectionFactoryName, String sql, String sqlId) {
        return this.metricNamingStrategy.startCallableStatementExecuteTimer(connectionFactoryName, sql, sqlId);
    }

    public ResultSet wrapResultSet(String connectionFactoryName, ResultSet resultSet, String sql, String sqlId) {
        Timer lifeTimerContext = this.metricNamingStrategy.startResultSetTimer(connectionFactoryName, sql, sqlId);
        return this.newProxy(new ResultSetProxyHandler<ResultSet>(resultSet, this.getResultSetType(resultSet), connectionFactoryName, this, lifeTimerContext));
    }

    private Class<? extends ResultSet> getResultSetType(ResultSet resultSet) {
        Class resultSetType = resultSet instanceof RowSet ? (resultSet instanceof CachedRowSet ? (resultSet instanceof WebRowSet ? (resultSet instanceof FilteredRowSet ? FilteredRowSet.class : (resultSet instanceof JoinRowSet ? JoinRowSet.class : WebRowSet.class)) : CachedRowSet.class) : (resultSet instanceof JdbcRowSet ? JdbcRowSet.class : RowSet.class)) : ResultSet.class;
        return resultSetType;
    }
}

