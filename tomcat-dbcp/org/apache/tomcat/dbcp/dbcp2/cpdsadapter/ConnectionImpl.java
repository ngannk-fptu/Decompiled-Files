/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.cpdsadapter;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.apache.tomcat.dbcp.dbcp2.DelegatingCallableStatement;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.DelegatingPreparedStatement;
import org.apache.tomcat.dbcp.dbcp2.cpdsadapter.PooledConnectionImpl;

final class ConnectionImpl
extends DelegatingConnection<Connection> {
    private final boolean accessToUnderlyingConnectionAllowed;
    private final PooledConnectionImpl pooledConnection;

    ConnectionImpl(PooledConnectionImpl pooledConnection, Connection connection, boolean accessToUnderlyingConnectionAllowed) {
        super(connection);
        this.pooledConnection = pooledConnection;
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    @Override
    public void close() throws SQLException {
        if (!this.isClosedInternal()) {
            try {
                this.passivate();
            }
            finally {
                this.setClosedInternal(true);
                this.pooledConnection.notifyListeners();
            }
        }
    }

    @Override
    public Connection getDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return this.getDelegateInternal();
        }
        return null;
    }

    @Override
    public Connection getInnermostDelegate() {
        if (this.isAccessToUnderlyingConnectionAllowed()) {
            return super.getInnermostDelegateInternal();
        }
        return null;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this.pooledConnection.prepareCall(sql));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this.pooledConnection.prepareCall(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingCallableStatement(this, this.pooledConnection.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, autoGeneratedKeys));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, columnIndexes));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        this.checkOpen();
        try {
            return new DelegatingPreparedStatement(this, this.pooledConnection.prepareStatement(sql, columnNames));
        }
        catch (SQLException e) {
            this.handleException(e);
            return null;
        }
    }
}

