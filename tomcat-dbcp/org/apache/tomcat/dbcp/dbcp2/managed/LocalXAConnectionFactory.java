/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.dbcp2.managed.XAConnectionFactory;

public class LocalXAConnectionFactory
implements XAConnectionFactory {
    private final TransactionRegistry transactionRegistry;
    private final ConnectionFactory connectionFactory;

    public LocalXAConnectionFactory(TransactionManager transactionManager, ConnectionFactory connectionFactory) {
        this(transactionManager, null, connectionFactory);
    }

    public LocalXAConnectionFactory(TransactionManager transactionManager, TransactionSynchronizationRegistry transactionSynchronizationRegistry, ConnectionFactory connectionFactory) {
        Objects.requireNonNull(transactionManager, "transactionManager");
        Objects.requireNonNull(connectionFactory, "connectionFactory");
        this.transactionRegistry = new TransactionRegistry(transactionManager, transactionSynchronizationRegistry);
        this.connectionFactory = connectionFactory;
    }

    @Override
    public Connection createConnection() throws SQLException {
        Connection connection = this.connectionFactory.createConnection();
        LocalXAResource xaResource = new LocalXAResource(connection);
        this.transactionRegistry.registerConnection(connection, xaResource);
        return connection;
    }

    public ConnectionFactory getConnectionFactory() {
        return this.connectionFactory;
    }

    @Override
    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    protected static class LocalXAResource
    implements XAResource {
        private static final Xid[] EMPTY_XID_ARRAY = new Xid[0];
        private final Connection connection;
        private Xid currentXid;
        private boolean originalAutoCommit;

        public LocalXAResource(Connection localTransaction) {
            this.connection = localTransaction;
        }

        private Xid checkCurrentXid() throws XAException {
            if (this.currentXid == null) {
                throw new XAException("There is no current transaction");
            }
            return this.currentXid;
        }

        @Override
        public synchronized void commit(Xid xid, boolean flag) throws XAException {
            Objects.requireNonNull(xid, "xid");
            if (!this.checkCurrentXid().equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
            try {
                if (this.connection.isClosed()) {
                    throw new XAException("Connection is closed");
                }
                if (!this.connection.isReadOnly()) {
                    this.connection.commit();
                }
            }
            catch (SQLException e) {
                throw (XAException)new XAException().initCause(e);
            }
            finally {
                try {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                }
                catch (SQLException sQLException) {}
                this.currentXid = null;
            }
        }

        @Override
        public synchronized void end(Xid xid, int flag) throws XAException {
            Objects.requireNonNull(xid, "xid");
            if (!this.checkCurrentXid().equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
        }

        @Override
        public synchronized void forget(Xid xid) {
            if (xid != null && xid.equals(this.currentXid)) {
                this.currentXid = null;
            }
        }

        @Override
        public int getTransactionTimeout() {
            return 0;
        }

        public synchronized Xid getXid() {
            return this.currentXid;
        }

        @Override
        public boolean isSameRM(XAResource xaResource) {
            return this == xaResource;
        }

        @Override
        public synchronized int prepare(Xid xid) {
            try {
                if (this.connection.isReadOnly()) {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                    return 3;
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
            return 0;
        }

        @Override
        public Xid[] recover(int flag) {
            return EMPTY_XID_ARRAY;
        }

        @Override
        public synchronized void rollback(Xid xid) throws XAException {
            Objects.requireNonNull(xid, "xid");
            if (!this.checkCurrentXid().equals(xid)) {
                throw new XAException("Invalid Xid: expected " + this.currentXid + ", but was " + xid);
            }
            try {
                this.connection.rollback();
            }
            catch (SQLException e) {
                throw (XAException)new XAException().initCause(e);
            }
            finally {
                try {
                    this.connection.setAutoCommit(this.originalAutoCommit);
                }
                catch (SQLException sQLException) {}
                this.currentXid = null;
            }
        }

        @Override
        public boolean setTransactionTimeout(int transactionTimeout) {
            return false;
        }

        @Override
        public synchronized void start(Xid xid, int flag) throws XAException {
            if (flag == 0) {
                if (this.currentXid != null) {
                    throw new XAException("Already enlisted in another transaction with xid " + xid);
                }
                try {
                    this.originalAutoCommit = this.connection.getAutoCommit();
                }
                catch (SQLException ignored) {
                    this.originalAutoCommit = true;
                }
                try {
                    this.connection.setAutoCommit(false);
                }
                catch (SQLException e) {
                    throw (XAException)new XAException("Count not turn off auto commit for a XA transaction").initCause(e);
                }
                this.currentXid = xid;
            } else if (flag == 0x8000000) {
                if (!xid.equals(this.currentXid)) {
                    throw new XAException("Attempting to resume in different transaction: expected " + this.currentXid + ", but was " + xid);
                }
            } else {
                throw new XAException("Unknown start flag " + flag);
            }
        }
    }
}

