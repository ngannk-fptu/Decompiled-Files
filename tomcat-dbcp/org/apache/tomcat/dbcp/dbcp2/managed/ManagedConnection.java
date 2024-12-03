/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionContext;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionContextListener;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.pool2.ObjectPool;

public class ManagedConnection<C extends Connection>
extends DelegatingConnection<C> {
    private final ObjectPool<C> pool;
    private final TransactionRegistry transactionRegistry;
    private final boolean accessToUnderlyingConnectionAllowed;
    private TransactionContext transactionContext;
    private boolean isSharedConnection;
    private final Lock lock;

    public ManagedConnection(ObjectPool<C> pool, TransactionRegistry transactionRegistry, boolean accessToUnderlyingConnectionAllowed) throws SQLException {
        super(null);
        this.pool = pool;
        this.transactionRegistry = transactionRegistry;
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
        this.lock = new ReentrantLock();
        this.updateTransactionStatus();
    }

    @Override
    protected void checkOpen() throws SQLException {
        super.checkOpen();
        this.updateTransactionStatus();
    }

    @Override
    public void close() throws SQLException {
        if (!this.isClosedInternal()) {
            this.lock.lock();
            try {
                if (this.transactionContext == null || this.transactionContext.isTransactionComplete()) {
                    super.close();
                }
            }
            finally {
                try {
                    this.setClosedInternal(true);
                }
                finally {
                    this.lock.unlock();
                }
            }
        }
    }

    @Override
    public void commit() throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.commit();
    }

    @Override
    public C getDelegate() {
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

    public TransactionContext getTransactionContext() {
        return this.transactionContext;
    }

    public TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public void rollback() throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.rollback();
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Auto-commit can not be set while enrolled in a transaction");
        }
        super.setAutoCommit(autoCommit);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        if (this.transactionContext != null) {
            throw new SQLException("Read-only can not be set while enrolled in a transaction");
        }
        super.setReadOnly(readOnly);
    }

    protected void transactionComplete() {
        this.lock.lock();
        try {
            this.transactionContext.completeTransaction();
        }
        finally {
            this.lock.unlock();
        }
        if (this.isSharedConnection) {
            this.setDelegate(null);
            this.isSharedConnection = false;
        }
        this.clearCachedState();
        Object delegate = this.getDelegateInternal();
        if (this.isClosedInternal() && delegate != null) {
            try {
                this.setDelegate(null);
                if (!delegate.isClosed()) {
                    delegate.close();
                }
            }
            catch (SQLException sQLException) {
                // empty catch block
            }
        }
    }

    private void updateTransactionStatus() throws SQLException {
        if (this.transactionContext != null && !this.transactionContext.isTransactionComplete()) {
            if (this.transactionContext.isActive()) {
                if (this.transactionContext != this.transactionRegistry.getActiveTransactionContext()) {
                    throw new SQLException("Connection can not be used while enlisted in another transaction");
                }
                return;
            }
            this.transactionComplete();
        }
        this.transactionContext = this.transactionRegistry.getActiveTransactionContext();
        if (this.transactionContext != null && this.transactionContext.getSharedConnection() != null) {
            Object connection = this.getDelegateInternal();
            this.setDelegate(null);
            if (connection != null && this.transactionContext.getSharedConnection() != connection) {
                try {
                    this.pool.returnObject(connection);
                }
                catch (Exception e) {
                    try {
                        this.pool.invalidateObject(connection);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            this.transactionContext.addTransactionContextListener(new CompletionListener());
            Connection shared = this.transactionContext.getSharedConnection();
            this.setDelegate(shared);
            this.isSharedConnection = true;
        } else {
            Object connection = this.getDelegateInternal();
            if (connection == null) {
                try {
                    connection = (Connection)this.pool.borrowObject();
                    this.setDelegate(connection);
                }
                catch (Exception e) {
                    throw new SQLException("Unable to acquire a new connection from the pool", e);
                }
            }
            if (this.transactionContext != null) {
                this.transactionContext.addTransactionContextListener(new CompletionListener());
                try {
                    this.transactionContext.setSharedConnection((Connection)connection);
                }
                catch (SQLException e) {
                    this.transactionContext = null;
                    try {
                        this.pool.invalidateObject(connection);
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                    throw e;
                }
            }
        }
        this.clearCachedState();
    }

    protected class CompletionListener
    implements TransactionContextListener {
        protected CompletionListener() {
        }

        @Override
        public void afterCompletion(TransactionContext completedContext, boolean committed) {
            if (completedContext == ManagedConnection.this.transactionContext) {
                ManagedConnection.this.transactionComplete();
            }
        }
    }
}

