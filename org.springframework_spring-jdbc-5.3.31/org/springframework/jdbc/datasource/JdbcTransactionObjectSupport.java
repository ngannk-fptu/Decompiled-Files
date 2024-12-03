/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.NestedTransactionNotSupportedException
 *  org.springframework.transaction.SavepointManager
 *  org.springframework.transaction.TransactionException
 *  org.springframework.transaction.TransactionSystemException
 *  org.springframework.transaction.TransactionUsageException
 *  org.springframework.transaction.support.SmartTransactionObject
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.SQLException;
import java.sql.Savepoint;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.NestedTransactionNotSupportedException;
import org.springframework.transaction.SavepointManager;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.TransactionUsageException;
import org.springframework.transaction.support.SmartTransactionObject;
import org.springframework.util.Assert;

public abstract class JdbcTransactionObjectSupport
implements SavepointManager,
SmartTransactionObject {
    private static final Log logger = LogFactory.getLog(JdbcTransactionObjectSupport.class);
    @Nullable
    private ConnectionHolder connectionHolder;
    @Nullable
    private Integer previousIsolationLevel;
    private boolean readOnly = false;
    private boolean savepointAllowed = false;

    public void setConnectionHolder(@Nullable ConnectionHolder connectionHolder) {
        this.connectionHolder = connectionHolder;
    }

    public ConnectionHolder getConnectionHolder() {
        Assert.state((this.connectionHolder != null ? 1 : 0) != 0, (String)"No ConnectionHolder available");
        return this.connectionHolder;
    }

    public boolean hasConnectionHolder() {
        return this.connectionHolder != null;
    }

    public void setPreviousIsolationLevel(@Nullable Integer previousIsolationLevel) {
        this.previousIsolationLevel = previousIsolationLevel;
    }

    @Nullable
    public Integer getPreviousIsolationLevel() {
        return this.previousIsolationLevel;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isReadOnly() {
        return this.readOnly;
    }

    public void setSavepointAllowed(boolean savepointAllowed) {
        this.savepointAllowed = savepointAllowed;
    }

    public boolean isSavepointAllowed() {
        return this.savepointAllowed;
    }

    public void flush() {
    }

    public Object createSavepoint() throws TransactionException {
        ConnectionHolder conHolder = this.getConnectionHolderForSavepoint();
        try {
            if (!conHolder.supportsSavepoints()) {
                throw new NestedTransactionNotSupportedException("Cannot create a nested transaction because savepoints are not supported by your JDBC driver");
            }
            if (conHolder.isRollbackOnly()) {
                throw new CannotCreateTransactionException("Cannot create savepoint for transaction which is already marked as rollback-only");
            }
            return conHolder.createSavepoint();
        }
        catch (SQLException ex) {
            throw new CannotCreateTransactionException("Could not create JDBC savepoint", (Throwable)ex);
        }
    }

    public void rollbackToSavepoint(Object savepoint) throws TransactionException {
        ConnectionHolder conHolder = this.getConnectionHolderForSavepoint();
        try {
            conHolder.getConnection().rollback((Savepoint)savepoint);
            conHolder.resetRollbackOnly();
        }
        catch (Throwable ex) {
            throw new TransactionSystemException("Could not roll back to JDBC savepoint", ex);
        }
    }

    public void releaseSavepoint(Object savepoint) throws TransactionException {
        ConnectionHolder conHolder = this.getConnectionHolderForSavepoint();
        try {
            conHolder.getConnection().releaseSavepoint((Savepoint)savepoint);
        }
        catch (Throwable ex) {
            logger.debug((Object)"Could not explicitly release JDBC savepoint", ex);
        }
    }

    protected ConnectionHolder getConnectionHolderForSavepoint() throws TransactionException {
        if (!this.isSavepointAllowed()) {
            throw new NestedTransactionNotSupportedException("Transaction manager does not allow nested transactions");
        }
        if (!this.hasConnectionHolder()) {
            throw new TransactionUsageException("Cannot create nested transaction when not exposing a JDBC transaction");
        }
        return this.getConnectionHolder();
    }
}

