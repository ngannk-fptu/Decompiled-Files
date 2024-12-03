/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.resource.jdbc.internal;

import java.sql.Connection;
import java.sql.SQLException;
import org.hibernate.TransactionException;
import org.hibernate.resource.jdbc.ResourceRegistry;
import org.hibernate.resource.jdbc.spi.LogicalConnectionImplementor;
import org.hibernate.resource.jdbc.spi.PhysicalJdbcTransaction;
import org.hibernate.resource.transaction.spi.TransactionStatus;
import org.jboss.logging.Logger;

public abstract class AbstractLogicalConnectionImplementor
implements LogicalConnectionImplementor,
PhysicalJdbcTransaction {
    private static final Logger log = Logger.getLogger(AbstractLogicalConnectionImplementor.class);
    private TransactionStatus status = TransactionStatus.NOT_ACTIVE;
    protected ResourceRegistry resourceRegistry;

    @Override
    public PhysicalJdbcTransaction getPhysicalJdbcTransaction() {
        this.errorIfClosed();
        return this;
    }

    protected void errorIfClosed() {
        if (!this.isOpen()) {
            throw new IllegalStateException(this.toString() + " is closed");
        }
    }

    @Override
    public ResourceRegistry getResourceRegistry() {
        return this.resourceRegistry;
    }

    @Override
    public void afterStatement() {
        log.trace((Object)"LogicalConnection#afterStatement");
    }

    @Override
    public void beforeTransactionCompletion() {
        log.trace((Object)"LogicalConnection#beforeTransactionCompletion");
    }

    @Override
    public void afterTransaction() {
        log.trace((Object)"LogicalConnection#afterTransaction");
        this.resourceRegistry.releaseResources();
    }

    protected abstract Connection getConnectionForTransactionManagement();

    @Override
    public void begin() {
        try {
            if (!this.doConnectionsFromProviderHaveAutoCommitDisabled()) {
                log.trace((Object)"Preparing to begin transaction via JDBC Connection.setAutoCommit(false)");
                this.getConnectionForTransactionManagement().setAutoCommit(false);
                log.trace((Object)"Transaction begun via JDBC Connection.setAutoCommit(false)");
            }
            this.status = TransactionStatus.ACTIVE;
        }
        catch (SQLException e) {
            throw new TransactionException("JDBC begin transaction failed: ", e);
        }
    }

    @Override
    public void commit() {
        try {
            log.trace((Object)"Preparing to commit transaction via JDBC Connection.commit()");
            this.getConnectionForTransactionManagement().commit();
            this.status = TransactionStatus.COMMITTED;
            log.trace((Object)"Transaction committed via JDBC Connection.commit()");
        }
        catch (SQLException e) {
            this.status = TransactionStatus.FAILED_COMMIT;
            throw new TransactionException("Unable to commit against JDBC Connection", e);
        }
        this.afterCompletion();
    }

    protected void afterCompletion() {
    }

    protected void resetConnection(boolean initiallyAutoCommit) {
        try {
            if (initiallyAutoCommit) {
                log.trace((Object)"re-enabling auto-commit on JDBC Connection after completion of JDBC-based transaction");
                this.getConnectionForTransactionManagement().setAutoCommit(true);
                this.status = TransactionStatus.NOT_ACTIVE;
            }
        }
        catch (Exception e) {
            log.debug((Object)("Could not re-enable auto-commit on JDBC Connection after completion of JDBC-based transaction : " + e));
        }
    }

    @Override
    public void rollback() {
        try {
            log.trace((Object)"Preparing to rollback transaction via JDBC Connection.rollback()");
            this.getConnectionForTransactionManagement().rollback();
            this.status = TransactionStatus.ROLLED_BACK;
            log.trace((Object)"Transaction rolled-back via JDBC Connection.rollback()");
        }
        catch (SQLException e) {
            this.status = TransactionStatus.FAILED_ROLLBACK;
            throw new TransactionException("Unable to rollback against JDBC Connection", e);
        }
        this.afterCompletion();
    }

    protected static boolean determineInitialAutoCommitMode(Connection providedConnection) {
        try {
            return providedConnection.getAutoCommit();
        }
        catch (SQLException e) {
            log.debug((Object)"Unable to ascertain initial auto-commit state of provided connection; assuming auto-commit");
            return true;
        }
    }

    @Override
    public TransactionStatus getStatus() {
        return this.status;
    }

    protected boolean doConnectionsFromProviderHaveAutoCommitDisabled() {
        return false;
    }
}

