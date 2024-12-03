/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import com.microsoft.sqlserver.jdbc.SQLServerConnection;
import com.microsoft.sqlserver.jdbc.SQLServerConnectionPoolProxy;
import com.microsoft.sqlserver.jdbc.SQLServerDataSource;
import com.microsoft.sqlserver.jdbc.SQLServerException;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.PooledConnection;
import javax.sql.StatementEventListener;

public class SQLServerPooledConnection
implements PooledConnection,
Serializable {
    private static final long serialVersionUID = 3492921646187451164L;
    private final Vector<ConnectionEventListener> listeners;
    private SQLServerDataSource factoryDataSource;
    private SQLServerConnection physicalConnection;
    private SQLServerConnectionPoolProxy lastProxyConnection;
    private String factoryUser;
    private String factoryPassword;
    private transient Logger pcLogger;
    private final String traceID;
    private static final AtomicInteger basePooledConnectionID = new AtomicInteger(0);
    private final transient Lock lock = new ReentrantLock();
    private final transient Lock listenersLock = new ReentrantLock();

    SQLServerPooledConnection(SQLServerDataSource ds, String user, String password) throws SQLException {
        this.listeners = new Vector();
        this.traceID = this.getClass().getSimpleName() + ":" + SQLServerPooledConnection.nextPooledConnectionID();
        this.pcLogger = SQLServerDataSource.dsLogger;
        this.factoryDataSource = ds;
        this.factoryUser = user;
        this.factoryPassword = password;
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Start create new connection for pool.");
        }
        this.physicalConnection = this.createNewConnection();
        if (this.pcLogger.isLoggable(Level.FINE)) {
            this.pcLogger.fine(this.toString() + " created by (" + ds.toString() + ") Physical connection " + this.safeCID() + ", End create new connection for pool");
        }
    }

    public String toString() {
        return this.traceID;
    }

    private SQLServerConnection createNewConnection() throws SQLException {
        return this.factoryDataSource.getConnectionInternal(this.factoryUser, this.factoryPassword, this);
    }

    @Override
    public Connection getConnection() throws SQLException {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " user:(default).");
        }
        this.lock.lock();
        try {
            if (this.physicalConnection == null) {
                SQLServerException.makeFromDriverError(null, this, SQLServerException.getErrString("R_physicalConnectionIsClosed"), "", true);
            }
            this.physicalConnection.doSecurityCheck();
            if (this.pcLogger.isLoggable(Level.FINE)) {
                this.pcLogger.fine(this.toString() + " Physical connection, " + this.safeCID());
            }
            if (this.physicalConnection.needsReconnect()) {
                this.physicalConnection.close();
                this.physicalConnection = this.createNewConnection();
            }
            if (null != this.lastProxyConnection) {
                this.physicalConnection.resetPooledConnection();
                if (!this.lastProxyConnection.isClosed()) {
                    if (this.pcLogger.isLoggable(Level.FINE)) {
                        this.pcLogger.fine(this.toString() + "proxy " + this.lastProxyConnection.toString() + " is not closed before getting the connection.");
                    }
                    this.lastProxyConnection.internalClose();
                }
            }
            this.lastProxyConnection = new SQLServerConnectionPoolProxy(this.physicalConnection);
            if (this.pcLogger.isLoggable(Level.FINE) && !this.lastProxyConnection.isClosed()) {
                this.pcLogger.fine(this.toString() + " proxy " + this.lastProxyConnection.toString() + " is returned.");
            }
            SQLServerConnectionPoolProxy sQLServerConnectionPoolProxy = this.lastProxyConnection;
            return sQLServerConnectionPoolProxy;
        }
        finally {
            this.lock.unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void notifyEvent(SQLServerException e) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Exception:" + e + this.safeCID());
        }
        if (null != e) {
            this.lock.lock();
            try {
                if (null != this.lastProxyConnection) {
                    this.lastProxyConnection.internalClose();
                    this.lastProxyConnection = null;
                }
            }
            finally {
                this.lock.unlock();
            }
        }
        this.listenersLock.lock();
        try {
            for (int i = 0; i < this.listeners.size(); ++i) {
                ConnectionEventListener listener = this.listeners.elementAt(i);
                if (listener == null) continue;
                ConnectionEvent ev = new ConnectionEvent(this, e);
                if (null == e) {
                    if (this.pcLogger.isLoggable(Level.FINER)) {
                        this.pcLogger.finer(this.toString() + " notifyEvent:connectionClosed " + this.safeCID());
                    }
                    listener.connectionClosed(ev);
                    continue;
                }
                if (this.pcLogger.isLoggable(Level.FINER)) {
                    this.pcLogger.finer(this.toString() + " notifyEvent:connectionErrorOccurred " + this.safeCID());
                }
                listener.connectionErrorOccurred(ev);
            }
        }
        finally {
            this.listenersLock.unlock();
        }
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + this.safeCID());
        }
        this.listenersLock.lock();
        try {
            this.listeners.add(listener);
        }
        finally {
            this.listenersLock.unlock();
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + " Closing physical connection, " + this.safeCID());
        }
        this.lock.lock();
        try {
            if (null != this.lastProxyConnection) {
                this.lastProxyConnection.internalClose();
            }
            if (null != this.physicalConnection) {
                this.physicalConnection.detachFromPool();
                this.physicalConnection.close();
            }
            this.physicalConnection = null;
        }
        finally {
            this.lock.unlock();
        }
        this.listenersLock.lock();
        try {
            this.listeners.clear();
        }
        finally {
            this.listenersLock.unlock();
        }
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        if (this.pcLogger.isLoggable(Level.FINER)) {
            this.pcLogger.finer(this.toString() + this.safeCID());
        }
        this.listenersLock.lock();
        try {
            this.listeners.remove(listener);
        }
        finally {
            this.listenersLock.unlock();
        }
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        throw new UnsupportedOperationException(SQLServerException.getErrString("R_notSupported"));
    }

    SQLServerConnection getPhysicalConnection() {
        return this.physicalConnection;
    }

    private static int nextPooledConnectionID() {
        return basePooledConnectionID.incrementAndGet();
    }

    private String safeCID() {
        if (null == this.physicalConnection) {
            return " ConnectionID:(null)";
        }
        return this.physicalConnection.toString();
    }
}

