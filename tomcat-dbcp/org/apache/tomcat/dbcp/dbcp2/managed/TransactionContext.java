/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.RollbackException
 *  javax.transaction.Synchronization
 *  javax.transaction.SystemException
 *  javax.transaction.Transaction
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.lang.ref.WeakReference;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;
import javax.transaction.RollbackException;
import javax.transaction.Synchronization;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionSynchronizationRegistry;
import javax.transaction.xa.XAResource;
import org.apache.tomcat.dbcp.dbcp2.managed.SynchronizationAdapter;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionContextListener;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;

public class TransactionContext {
    private final TransactionRegistry transactionRegistry;
    private final WeakReference<Transaction> transactionRef;
    private final TransactionSynchronizationRegistry transactionSynchronizationRegistry;
    private Connection sharedConnection;
    private boolean transactionComplete;

    public TransactionContext(TransactionRegistry transactionRegistry, Transaction transaction) {
        this(transactionRegistry, transaction, null);
    }

    public TransactionContext(TransactionRegistry transactionRegistry, Transaction transaction, TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        Objects.requireNonNull(transactionRegistry, "transactionRegistry");
        Objects.requireNonNull(transaction, "transaction");
        this.transactionRegistry = transactionRegistry;
        this.transactionRef = new WeakReference<Transaction>(transaction);
        this.transactionComplete = false;
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }

    public void addTransactionContextListener(final TransactionContextListener listener) throws SQLException {
        try {
            if (!this.isActive()) {
                Transaction transaction = (Transaction)this.transactionRef.get();
                listener.afterCompletion(this, transaction != null && transaction.getStatus() == 3);
                return;
            }
            SynchronizationAdapter s = new SynchronizationAdapter(){

                @Override
                public void afterCompletion(int status) {
                    listener.afterCompletion(TransactionContext.this, status == 3);
                }
            };
            if (this.transactionSynchronizationRegistry != null) {
                this.transactionSynchronizationRegistry.registerInterposedSynchronization((Synchronization)s);
            } else {
                this.getTransaction().registerSynchronization((Synchronization)s);
            }
        }
        catch (RollbackException s) {
        }
        catch (Exception e) {
            throw new SQLException("Unable to register transaction context listener", e);
        }
    }

    public void completeTransaction() {
        this.transactionComplete = true;
    }

    public Connection getSharedConnection() {
        return this.sharedConnection;
    }

    private Transaction getTransaction() throws SQLException {
        Transaction transaction = (Transaction)this.transactionRef.get();
        if (transaction == null) {
            throw new SQLException("Unable to enlist connection because the transaction has been garbage collected");
        }
        return transaction;
    }

    public boolean isActive() throws SQLException {
        try {
            Transaction transaction = (Transaction)this.transactionRef.get();
            if (transaction == null) {
                return false;
            }
            int status = transaction.getStatus();
            return status == 0 || status == 1;
        }
        catch (SystemException e) {
            throw new SQLException("Unable to get transaction status", e);
        }
    }

    public boolean isTransactionComplete() {
        return this.transactionComplete;
    }

    public void setSharedConnection(Connection sharedConnection) throws SQLException {
        if (this.sharedConnection != null) {
            throw new IllegalStateException("A shared connection is already set");
        }
        Transaction transaction = this.getTransaction();
        try {
            XAResource xaResource = this.transactionRegistry.getXAResource(sharedConnection);
            if (!transaction.enlistResource(xaResource)) {
                throw new SQLException("Unable to enlist connection in transaction: enlistResource returns 'false'.");
            }
        }
        catch (IllegalStateException e) {
            throw new SQLException("Unable to enlist connection in the transaction", e);
        }
        catch (RollbackException e) {
        }
        catch (SystemException e) {
            throw new SQLException("Unable to enlist connection the transaction", e);
        }
        this.sharedConnection = sharedConnection;
    }
}

