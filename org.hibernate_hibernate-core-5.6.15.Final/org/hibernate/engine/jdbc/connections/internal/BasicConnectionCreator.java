/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jdbc.connections.internal;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.engine.jdbc.connections.internal.ConnectionCreator;
import org.hibernate.engine.jdbc.spi.JdbcServices;
import org.hibernate.exception.JDBCConnectionException;
import org.hibernate.exception.internal.SQLStateConversionDelegate;
import org.hibernate.exception.spi.ConversionContext;
import org.hibernate.exception.spi.SQLExceptionConversionDelegate;
import org.hibernate.exception.spi.ViolatedConstraintNameExtracter;
import org.hibernate.internal.util.ValueHolder;
import org.hibernate.service.spi.ServiceRegistryImplementor;

public abstract class BasicConnectionCreator
implements ConnectionCreator {
    private final ServiceRegistryImplementor serviceRegistry;
    private final String url;
    private final Properties connectionProps;
    private final boolean autoCommit;
    private final Integer isolation;
    private final String initSql;
    private ValueHolder<SQLExceptionConversionDelegate> simpleConverterAccess = new ValueHolder<1>(new ValueHolder.DeferredInitializer<SQLExceptionConversionDelegate>(){

        @Override
        public SQLExceptionConversionDelegate initialize() {
            return new SQLExceptionConversionDelegate(){
                private final SQLStateConversionDelegate sqlStateDelegate = new SQLStateConversionDelegate(new ConversionContext(){

                    @Override
                    public ViolatedConstraintNameExtracter getViolatedConstraintNameExtracter() {
                        throw new HibernateException("Unexpected call to org.hibernate.exception.spi.ConversionContext.getViolatedConstraintNameExtracter");
                    }
                });

                @Override
                public JDBCException convert(SQLException sqlException, String message, String sql) {
                    JDBCException exception = this.sqlStateDelegate.convert(sqlException, message, sql);
                    if (exception == null) {
                        exception = new JDBCConnectionException(message, sqlException, sql);
                    }
                    return exception;
                }
            };
        }
    });

    public BasicConnectionCreator(ServiceRegistryImplementor serviceRegistry, String url, Properties connectionProps, boolean autocommit, Integer isolation, String initSql) {
        this.serviceRegistry = serviceRegistry;
        this.url = url;
        this.connectionProps = connectionProps;
        this.autoCommit = autocommit;
        this.isolation = isolation;
        this.initSql = initSql;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public Connection createConnection() {
        Connection conn = this.makeConnection(this.url, this.connectionProps);
        if (conn == null) {
            throw new HibernateException("Unable to make JDBC Connection [" + this.url + "]");
        }
        try {
            if (this.isolation != null) {
                conn.setTransactionIsolation(this.isolation);
            }
        }
        catch (SQLException e) {
            throw this.convertSqlException("Unable to set transaction isolation (" + this.isolation + ")", e);
        }
        try {
            if (conn.getAutoCommit() != this.autoCommit) {
                conn.setAutoCommit(this.autoCommit);
            }
        }
        catch (SQLException e) {
            throw this.convertSqlException("Unable to set auto-commit (" + this.autoCommit + ")", e);
        }
        if (this.initSql != null && !this.initSql.trim().isEmpty()) {
            try (Statement s = conn.createStatement();){
                s.execute(this.initSql);
            }
            catch (SQLException e) {
                throw this.convertSqlException("Unable to execute initSql (" + this.initSql + ")", e);
            }
        }
        return conn;
    }

    protected JDBCException convertSqlException(String message, SQLException e) {
        JdbcServices jdbcServices = this.serviceRegistry.getService(JdbcServices.class);
        if (jdbcServices != null && jdbcServices.getSqlExceptionHelper() != null) {
            return jdbcServices.getSqlExceptionHelper().convert(e, message, null);
        }
        return this.simpleConverterAccess.getValue().convert(e, message, null);
    }

    protected abstract Connection makeConnection(String var1, Properties var2);

    public Properties getConnectionProperties() {
        return new Properties(this.connectionProps);
    }
}

