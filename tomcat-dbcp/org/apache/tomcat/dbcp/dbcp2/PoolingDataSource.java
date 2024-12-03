/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.logging.Logger;
import javax.sql.DataSource;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.pool2.ObjectPool;
import org.apache.tomcat.dbcp.pool2.impl.GenericObjectPool;

public class PoolingDataSource<C extends Connection>
implements DataSource,
AutoCloseable {
    private static final Log log = LogFactory.getLog(PoolingDataSource.class);
    private boolean accessToUnderlyingConnectionAllowed;
    private PrintWriter logWriter;
    private final ObjectPool<C> pool;

    public PoolingDataSource(ObjectPool<C> pool) {
        Objects.requireNonNull(pool, "Pool must not be null.");
        this.pool = pool;
        if (this.pool instanceof GenericObjectPool) {
            PoolableConnectionFactory pcf = (PoolableConnectionFactory)((GenericObjectPool)this.pool).getFactory();
            Objects.requireNonNull(pcf, "PoolableConnectionFactory must not be null.");
            if (pcf.getPool() != this.pool) {
                log.warn((Object)Utils.getMessage("poolingDataSource.factoryConfig"));
                ObjectPool<PoolableConnection> p = this.pool;
                pcf.setPool(p);
            }
        }
    }

    @Override
    public void close() throws RuntimeException, SQLException {
        try {
            this.pool.close();
        }
        catch (RuntimeException rte) {
            throw new RuntimeException(Utils.getMessage("pool.close.fail"), rte);
        }
        catch (Exception e) {
            throw new SQLException(Utils.getMessage("pool.close.fail"), e);
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            Connection conn = (Connection)this.pool.borrowObject();
            if (conn == null) {
                return null;
            }
            return new PoolGuardConnectionWrapper(this, conn);
        }
        catch (NoSuchElementException e) {
            throw new SQLException("Cannot get a connection, pool error " + e.getMessage(), e);
        }
        catch (RuntimeException | SQLException e) {
            throw e;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Cannot get a connection, general error", e);
        }
        catch (Exception e) {
            throw new SQLException("Cannot get a connection, general error", e);
        }
    }

    @Override
    public Connection getConnection(String uname, String passwd) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getLoginTimeout() {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }

    @Override
    public PrintWriter getLogWriter() {
        return this.logWriter;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    protected ObjectPool<C> getPool() {
        return this.pool;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface != null && iface.isInstance(this);
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean allow) {
        this.accessToUnderlyingConnectionAllowed = allow;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        throw new UnsupportedOperationException("Login timeout is not supported.");
    }

    @Override
    public void setLogWriter(PrintWriter out) {
        this.logWriter = out;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (this.isWrapperFor(iface)) {
            return iface.cast(this);
        }
        throw new SQLException(this + " is not a wrapper for " + iface);
    }

    private static class PoolGuardConnectionWrapper<D extends Connection>
    extends DelegatingConnection<D> {
        final /* synthetic */ PoolingDataSource this$0;

        PoolGuardConnectionWrapper(D delegate) {
            this.this$0 = var1_1;
            super(delegate);
        }

        @Override
        public void close() throws SQLException {
            if (this.getDelegateInternal() != null) {
                super.close();
                super.setDelegate(null);
            }
        }

        @Override
        public D getDelegate() {
            return this.this$0.isAccessToUnderlyingConnectionAllowed() ? (D)super.getDelegate() : null;
        }

        @Override
        public Connection getInnermostDelegate() {
            return this.this$0.isAccessToUnderlyingConnectionAllowed() ? super.getInnermostDelegate() : null;
        }

        @Override
        public boolean isClosed() throws SQLException {
            return this.getDelegateInternal() == null || super.isClosed();
        }
    }
}

