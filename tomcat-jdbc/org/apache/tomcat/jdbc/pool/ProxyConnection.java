/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.SQLException;
import javax.sql.XAConnection;
import org.apache.tomcat.jdbc.pool.ConnectionPool;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PooledConnection;

public class ProxyConnection
extends JdbcInterceptor {
    protected PooledConnection connection = null;
    protected ConnectionPool pool = null;

    public PooledConnection getConnection() {
        return this.connection;
    }

    public void setConnection(PooledConnection connection) {
        this.connection = connection;
    }

    public ConnectionPool getPool() {
        return this.pool;
    }

    public void setPool(ConnectionPool pool) {
        this.pool = pool;
    }

    protected ProxyConnection(ConnectionPool parent, PooledConnection con, boolean useEquals) {
        this.pool = parent;
        this.connection = con;
        this.setUseEquals(useEquals);
    }

    @Override
    public void reset(ConnectionPool parent, PooledConnection con) {
        this.pool = parent;
        this.connection = con;
    }

    public boolean isWrapperFor(Class<?> iface) {
        if (iface == XAConnection.class && this.connection.getXAConnection() != null) {
            return true;
        }
        return iface.isInstance(this.connection.getConnection());
    }

    public Object unwrap(Class<?> iface) throws SQLException {
        if (iface == PooledConnection.class) {
            return this.connection;
        }
        if (iface == XAConnection.class) {
            return this.connection.getXAConnection();
        }
        if (this.isWrapperFor(iface)) {
            return this.connection.getConnection();
        }
        throw new SQLException("Not a wrapper of " + iface.getName());
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (this.compare("isClosed", method)) {
            return this.isClosed();
        }
        if (this.compare("close", method)) {
            if (this.connection == null) {
                return null;
            }
            PooledConnection poolc = this.connection;
            this.connection = null;
            this.pool.returnConnection(poolc);
            return null;
        }
        if (this.compare("toString", method)) {
            return this.toString();
        }
        if (this.compare("getConnection", method) && this.connection != null) {
            return this.connection.getConnection();
        }
        if (method.getDeclaringClass().isAssignableFrom(XAConnection.class) && this.connection != null) {
            try {
                return method.invoke((Object)this.connection.getXAConnection(), args);
            }
            catch (Throwable t) {
                if (t instanceof InvocationTargetException) {
                    throw t.getCause() != null ? t.getCause() : t;
                }
                throw t;
            }
        }
        if (this.isClosed()) {
            throw new SQLException("Connection has already been closed.");
        }
        if (this.compare("unwrap", method)) {
            return this.unwrap((Class)args[0]);
        }
        if (this.compare("isWrapperFor", method)) {
            return this.isWrapperFor((Class)args[0]);
        }
        try {
            PooledConnection poolc = this.connection;
            if (poolc != null) {
                return method.invoke((Object)poolc.getConnection(), args);
            }
            throw new SQLException("Connection has already been closed.");
        }
        catch (Throwable t) {
            if (t instanceof InvocationTargetException) {
                throw t.getCause() != null ? t.getCause() : t;
            }
            throw t;
        }
    }

    public boolean isClosed() {
        return this.connection == null || this.connection.isDiscarded();
    }

    public PooledConnection getDelegateConnection() {
        return this.connection;
    }

    public ConnectionPool getParentPool() {
        return this.pool;
    }

    public String toString() {
        return "ProxyConnection[" + (this.connection != null ? this.connection.toString() : "null") + "]";
    }
}

