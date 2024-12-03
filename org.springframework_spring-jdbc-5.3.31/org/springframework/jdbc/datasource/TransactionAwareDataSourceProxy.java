/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 */
package org.springframework.jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.lang.Nullable;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class TransactionAwareDataSourceProxy
extends DelegatingDataSource {
    private boolean reobtainTransactionalConnections = false;

    public TransactionAwareDataSourceProxy() {
    }

    public TransactionAwareDataSourceProxy(DataSource targetDataSource) {
        super(targetDataSource);
    }

    public void setReobtainTransactionalConnections(boolean reobtainTransactionalConnections) {
        this.reobtainTransactionalConnections = reobtainTransactionalConnections;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.getTransactionAwareConnectionProxy(this.obtainTargetDataSource());
    }

    protected Connection getTransactionAwareConnectionProxy(DataSource targetDataSource) {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, (InvocationHandler)new TransactionAwareInvocationHandler(targetDataSource));
    }

    protected boolean shouldObtainFixedConnection(DataSource targetDataSource) {
        return !TransactionSynchronizationManager.isSynchronizationActive() || !this.reobtainTransactionalConnections;
    }

    private class TransactionAwareInvocationHandler
    implements InvocationHandler {
        private final DataSource targetDataSource;
        @Nullable
        private Connection target;
        private boolean closed = false;

        public TransactionAwareInvocationHandler(DataSource targetDataSource) {
            this.targetDataSource = targetDataSource;
        }

        @Override
        @Nullable
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Connection actualTarget;
            switch (method.getName()) {
                case "equals": {
                    return proxy == args[0];
                }
                case "hashCode": {
                    return System.identityHashCode(proxy);
                }
                case "toString": {
                    StringBuilder sb = new StringBuilder("Transaction-aware proxy for target Connection ");
                    if (this.target != null) {
                        sb.append('[').append(this.target.toString()).append(']');
                    } else {
                        sb.append(" from DataSource [").append(this.targetDataSource).append(']');
                    }
                    return sb.toString();
                }
                case "close": {
                    DataSourceUtils.doReleaseConnection(this.target, this.targetDataSource);
                    this.closed = true;
                    return null;
                }
                case "isClosed": {
                    return this.closed;
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
            if (this.target == null) {
                if (method.getName().equals("getWarnings") || method.getName().equals("clearWarnings")) {
                    return null;
                }
                if (this.closed) {
                    throw new SQLException("Connection handle already closed");
                }
                if (TransactionAwareDataSourceProxy.this.shouldObtainFixedConnection(this.targetDataSource)) {
                    this.target = DataSourceUtils.doGetConnection(this.targetDataSource);
                }
            }
            if ((actualTarget = this.target) == null) {
                actualTarget = DataSourceUtils.doGetConnection(this.targetDataSource);
            }
            if (method.getName().equals("getTargetConnection")) {
                return actualTarget;
            }
            try {
                Object retVal = method.invoke((Object)actualTarget, args);
                if (retVal instanceof Statement) {
                    DataSourceUtils.applyTransactionTimeout((Statement)retVal, this.targetDataSource);
                }
                Object object = retVal;
                return object;
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
            finally {
                if (actualTarget != this.target) {
                    DataSourceUtils.doReleaseConnection(actualTarget, this.targetDataSource);
                }
            }
        }
    }
}

