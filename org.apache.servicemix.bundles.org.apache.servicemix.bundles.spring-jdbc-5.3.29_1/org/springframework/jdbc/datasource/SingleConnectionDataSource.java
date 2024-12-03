/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.DisposableBean
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.jdbc.datasource;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public class SingleConnectionDataSource
extends DriverManagerDataSource
implements SmartDataSource,
DisposableBean {
    private boolean suppressClose;
    @Nullable
    private Boolean autoCommit;
    @Nullable
    private Connection target;
    @Nullable
    private Connection connection;
    private final Object connectionMonitor = new Object();

    public SingleConnectionDataSource() {
    }

    public SingleConnectionDataSource(String url, String username, String password, boolean suppressClose) {
        super(url, username, password);
        this.suppressClose = suppressClose;
    }

    public SingleConnectionDataSource(String url, boolean suppressClose) {
        super(url);
        this.suppressClose = suppressClose;
    }

    public SingleConnectionDataSource(Connection target, boolean suppressClose) {
        Assert.notNull((Object)target, (String)"Connection must not be null");
        this.target = target;
        this.suppressClose = suppressClose;
        this.connection = suppressClose ? this.getCloseSuppressingConnectionProxy(target) : target;
    }

    public void setSuppressClose(boolean suppressClose) {
        this.suppressClose = suppressClose;
    }

    protected boolean isSuppressClose() {
        return this.suppressClose;
    }

    public void setAutoCommit(boolean autoCommit) {
        this.autoCommit = autoCommit;
    }

    @Nullable
    protected Boolean getAutoCommitValue() {
        return this.autoCommit;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Connection getConnection() throws SQLException {
        Object object = this.connectionMonitor;
        synchronized (object) {
            if (this.connection == null) {
                this.initConnection();
            }
            if (this.connection.isClosed()) {
                throw new SQLException("Connection was closed in SingleConnectionDataSource. Check that user code checks shouldClose() before closing Connections, or set 'suppressClose' to 'true'");
            }
            return this.connection;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (ObjectUtils.nullSafeEquals((Object)username, (Object)this.getUsername()) && ObjectUtils.nullSafeEquals((Object)password, (Object)this.getPassword())) {
            return this.getConnection();
        }
        throw new SQLException("SingleConnectionDataSource does not support custom username and password");
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean shouldClose(Connection con) {
        Object object = this.connectionMonitor;
        synchronized (object) {
            return con != this.connection && con != this.target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void destroy() {
        Object object = this.connectionMonitor;
        synchronized (object) {
            this.closeConnection();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void initConnection() throws SQLException {
        if (this.getUrl() == null) {
            throw new IllegalStateException("'url' property is required for lazily initializing a Connection");
        }
        Object object = this.connectionMonitor;
        synchronized (object) {
            this.closeConnection();
            this.target = this.getConnectionFromDriver(this.getUsername(), this.getPassword());
            this.prepareConnection(this.target);
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Established shared JDBC Connection: " + this.target));
            }
            this.connection = this.isSuppressClose() ? this.getCloseSuppressingConnectionProxy(this.target) : this.target;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetConnection() {
        Object object = this.connectionMonitor;
        synchronized (object) {
            this.closeConnection();
            this.target = null;
            this.connection = null;
        }
    }

    protected void prepareConnection(Connection con) throws SQLException {
        Boolean autoCommit = this.getAutoCommitValue();
        if (autoCommit != null && con.getAutoCommit() != autoCommit.booleanValue()) {
            con.setAutoCommit(autoCommit);
        }
    }

    private void closeConnection() {
        if (this.target != null) {
            try {
                this.target.close();
            }
            catch (Throwable ex) {
                this.logger.info((Object)"Could not close shared JDBC Connection", ex);
            }
        }
    }

    protected Connection getCloseSuppressingConnectionProxy(Connection target) {
        return (Connection)Proxy.newProxyInstance(ConnectionProxy.class.getClassLoader(), new Class[]{ConnectionProxy.class}, (InvocationHandler)new CloseSuppressingInvocationHandler(target));
    }

    private static class CloseSuppressingInvocationHandler
    implements InvocationHandler {
        private final Connection target;

        public CloseSuppressingInvocationHandler(Connection target) {
            this.target = target;
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
                case "close": {
                    return null;
                }
                case "isClosed": {
                    return this.target.isClosed();
                }
                case "getTargetConnection": {
                    return this.target;
                }
                case "unwrap": {
                    return ((Class)args[0]).isInstance(proxy) ? proxy : this.target.unwrap((Class)args[0]);
                }
                case "isWrapperFor": {
                    return ((Class)args[0]).isInstance(proxy) || this.target.isWrapperFor((Class)args[0]);
                }
            }
            try {
                return method.invoke((Object)this.target, args);
            }
            catch (InvocationTargetException ex) {
                throw ex.getTargetException();
            }
        }
    }
}

