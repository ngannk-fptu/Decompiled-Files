/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.tomcat.dbcp.dbcp2.DelegatingConnection;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionContext;

public class TransactionRegistry {
    private final TransactionManager transactionManager;
    private final Map<Transaction, TransactionContext> caches = new WeakHashMap<Transaction, TransactionContext>();
    private final Map<Connection, XAResource> xaResources = new WeakHashMap<Connection, XAResource>();
    private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    public TransactionRegistry(TransactionManager transactionManager) {
        this(transactionManager, null);
    }

    public TransactionRegistry(TransactionManager transactionManager, TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        this.transactionManager = transactionManager;
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public TransactionContext getActiveTransactionContext() throws SQLException {
        Transaction transaction = null;
        try {
            transaction = this.transactionManager.getTransaction();
            if (transaction == null) {
                return null;
            }
        }
        catch (SystemException e) {
            throw new SQLException("Unable to determine current transaction ", e);
        }
        TransactionRegistry transactionRegistry = this;
        synchronized (transactionRegistry) {
            return this.caches.computeIfAbsent(transaction, k -> new TransactionContext(this, (Transaction)k, this.transactionSynchronizationRegistry));
        }
    }

    private Connection getConnectionKey(Connection connection) {
        Connection result = connection instanceof DelegatingConnection ? ((DelegatingConnection)connection).getInnermostDelegateInternal() : connection;
        return result;
    }

    public synchronized XAResource getXAResource(Connection connection) throws SQLException {
        Objects.requireNonNull(connection, "connection");
        Connection key = this.getConnectionKey(connection);
        XAResource xaResource = this.xaResources.get(key);
        if (xaResource == null) {
            throw new SQLException("Connection does not have a registered XAResource " + connection);
        }
        return xaResource;
    }

    public synchronized void registerConnection(Connection connection, XAResource xaResource) {
        Objects.requireNonNull(connection, "connection");
        Objects.requireNonNull(xaResource, "xaResource");
        this.xaResources.put(connection, xaResource);
    }

    public synchronized void unregisterConnection(Connection connection) {
        this.xaResources.remove(this.getConnectionKey(connection));
    }
}

