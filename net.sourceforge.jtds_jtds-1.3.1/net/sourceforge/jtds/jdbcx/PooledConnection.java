/*
 * Decompiled with CFR 0.152.
 */
package net.sourceforge.jtds.jdbcx;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import net.sourceforge.jtds.jdbc.Messages;
import net.sourceforge.jtds.jdbcx.proxy.ConnectionProxy;

public class PooledConnection
implements javax.sql.PooledConnection {
    private ArrayList listeners = new ArrayList();
    protected Connection connection;

    public PooledConnection(Connection connection) {
        this.connection = connection;
    }

    @Override
    public synchronized void addConnectionEventListener(ConnectionEventListener listener) {
        this.listeners = (ArrayList)this.listeners.clone();
        this.listeners.add(listener);
    }

    @Override
    public synchronized void close() throws SQLException {
        this.connection.close();
        this.connection = null;
    }

    public synchronized void fireConnectionEvent(boolean closed, SQLException sqlException) {
        if (this.listeners.size() > 0) {
            ConnectionEvent connectionEvent = new ConnectionEvent(this, sqlException);
            for (ConnectionEventListener listener : this.listeners) {
                if (closed) {
                    listener.connectionClosed(connectionEvent);
                    continue;
                }
                try {
                    if (this.connection != null && !this.connection.isClosed()) continue;
                    listener.connectionErrorOccurred(connectionEvent);
                }
                catch (SQLException ex) {}
            }
        }
    }

    @Override
    public synchronized Connection getConnection() throws SQLException {
        if (this.connection == null) {
            this.fireConnectionEvent(false, new SQLException(Messages.get("error.jdbcx.conclosed"), "08003"));
            return null;
        }
        return new ConnectionProxy(this, this.connection);
    }

    @Override
    public synchronized void removeConnectionEventListener(ConnectionEventListener listener) {
        this.listeners = (ArrayList)this.listeners.clone();
        this.listeners.remove(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        throw new AbstractMethodError();
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        throw new AbstractMethodError();
    }
}

