/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.Constants
 *  org.springframework.lang.Nullable
 */
package org.springframework.jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.Constants;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.lang.Nullable;

public class LazyConnectionDataSourceProxy
extends DelegatingDataSource {
    private static final Constants constants = new Constants(Connection.class);
    private static final Log logger = LogFactory.getLog(LazyConnectionDataSourceProxy.class);
    @Nullable
    private Boolean defaultAutoCommit;
    @Nullable
    private Integer defaultTransactionIsolation;

    public LazyConnectionDataSourceProxy() {
    }

    public LazyConnectionDataSourceProxy(DataSource targetDataSource) {
        this.setTargetDataSource(targetDataSource);
        this.afterPropertiesSet();
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolationName(String constantName) {
        this.setDefaultTransactionIsolation(constants.asNumber(constantName).intValue());
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (this.defaultAutoCommit == null || this.defaultTransactionIsolation == null) {
            try (Connection con = this.obtainTargetDataSource().getConnection();){
                this.checkDefaultConnectionProperties(con);
            }
            catch (SQLException ex) {
                logger.debug((Object)"Could not retrieve default auto-commit and transaction isolation settings", (Throwable)ex);
            }
        }
    }

    protected synchronized void checkDefaultConnectionProperties(Connection con) throws SQLException {
        if (this.defaultAutoCommit == null) {
            this.defaultAutoCommit = con.getAutoCommit();
        }
        if (this.defaultTransactionIsolation == null) {
            this.defaultTransactionIsolation = con.getTransactionIsolation();
        }
    }

    @Nullable
    protected Boolean defaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    @Nullable
    protected Integer defaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, (InvocationHandler)new LazyConnectionInvocationHandler());
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, (InvocationHandler)new LazyConnectionInvocationHandler(username, password));
    }

    private class LazyConnectionInvocationHandler
    implements InvocationHandler {
        @Nullable
        private String username;
        @Nullable
        private String password;
        @Nullable
        private Boolean autoCommit;
        @Nullable
        private Integer transactionIsolation;
        private boolean readOnly = false;
        private int holdability = 2;
        private boolean closed = false;
        @Nullable
        private Connection target;

        public LazyConnectionInvocationHandler() {
            this.autoCommit = LazyConnectionDataSourceProxy.this.defaultAutoCommit();
            this.transactionIsolation = LazyConnectionDataSourceProxy.this.defaultTransactionIsolation();
        }

        public LazyConnectionInvocationHandler(String username, String password) {
            this();
            this.username = username;
            this.password = password;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "getTargetConnection": {
                    return this.getTargetConnection(method);
                }
                case "unwrap": {
                    if (!((Class)args[0]).isInstance(proxy)) break;
                    return proxy;
                }
                case "isWrapperFor": {
                    if (!((Class)args[0]).isInstance(proxy)) break;
                    return true;
                }
            }
            if (!this.hasTargetConnection()) {
                switch (method.getName()) {
                    case "toString": {
                        return "Lazy Connection proxy for target DataSource [" + LazyConnectionDataSourceProxy.this.getTargetDataSource() + "]";
                    }
                    case "getAutoCommit": {
                        if (this.autoCommit == null) break;
                        return this.autoCommit;
                    }
                    case "setAutoCommit": {
                        this.autoCommit = (Boolean)args[0];
                        return null;
                    }
                    case "getTransactionIsolation": {
                        if (this.transactionIsolation == null) break;
                        return this.transactionIsolation;
                    }
                    case "setTransactionIsolation": {
                        this.transactionIsolation = (Integer)args[0];
                        return null;
                    }
                    case "isReadOnly": {
                        return this.readOnly;
                    }
                    case "setReadOnly": {
                        this.readOnly = (Boolean)args[0];
                        return null;
                    }
                    case "getHoldability": {
                        return this.holdability;
                    }
                    case "setHoldability": {
                        this.holdability = (Integer)args[0];
                        return null;
                    }
                    case "commit": 
                    case "rollback": {
                        return null;
                    }
                    case "getWarnings": 
                    case "clearWarnings": {
                        return null;
                    }
                    case "close": {
                        this.closed = true;
                        return null;
                    }
                    case "isClosed": {
                        return this.closed;
                    }
                    default: {
                        if (!this.closed) break;
                        throw new SQLException("Illegal operation: connection is closed");
                    }
                }
            }
            try {
                return method.invoke((Object)this.getTargetConnection(method), args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }

        private boolean hasTargetConnection() {
            return this.target != null;
        }

        private Connection getTargetConnection(Method operation) throws SQLException {
            if (this.target == null) {
                if (logger.isTraceEnabled()) {
                    logger.trace((Object)("Connecting to database for operation '" + operation.getName() + "'"));
                }
                this.target = this.username != null ? LazyConnectionDataSourceProxy.this.obtainTargetDataSource().getConnection(this.username, this.password) : LazyConnectionDataSourceProxy.this.obtainTargetDataSource().getConnection();
                LazyConnectionDataSourceProxy.this.checkDefaultConnectionProperties(this.target);
                if (this.readOnly) {
                    try {
                        this.target.setReadOnly(true);
                    }
                    catch (Exception ex) {
                        logger.debug((Object)"Could not set JDBC Connection read-only", (Throwable)ex);
                    }
                }
                if (this.transactionIsolation != null && !this.transactionIsolation.equals(LazyConnectionDataSourceProxy.this.defaultTransactionIsolation())) {
                    this.target.setTransactionIsolation(this.transactionIsolation);
                }
                if (this.autoCommit != null && this.autoCommit.booleanValue() != this.target.getAutoCommit()) {
                    this.target.setAutoCommit(this.autoCommit);
                }
            } else if (logger.isTraceEnabled()) {
                logger.trace((Object)("Using existing database connection for operation '" + operation.getName() + "'"));
            }
            return this.target;
        }
    }
}

