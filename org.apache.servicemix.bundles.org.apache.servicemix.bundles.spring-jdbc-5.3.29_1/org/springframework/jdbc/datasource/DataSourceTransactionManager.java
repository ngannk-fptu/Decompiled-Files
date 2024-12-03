/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.InitializingBean
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.CannotCreateTransactionException
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.TransactionSystemException
 *  org.springframework.transaction.support.AbstractPlatformTransactionManager
 *  org.springframework.transaction.support.DefaultTransactionStatus
 *  org.springframework.transaction.support.ResourceTransactionManager
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.transaction.support.TransactionSynchronizationUtils
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.datasource.JdbcTransactionObjectSupport;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.lang.Nullable;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.DefaultTransactionStatus;
import org.springframework.transaction.support.ResourceTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.transaction.support.TransactionSynchronizationUtils;
import org.springframework.util.Assert;

public class DataSourceTransactionManager
extends AbstractPlatformTransactionManager
implements ResourceTransactionManager,
InitializingBean {
    @Nullable
    private DataSource dataSource;
    private boolean enforceReadOnly = false;

    public DataSourceTransactionManager() {
        this.setNestedTransactionAllowed(true);
    }

    public DataSourceTransactionManager(DataSource dataSource) {
        this();
        this.setDataSource(dataSource);
        this.afterPropertiesSet();
    }

    public void setDataSource(@Nullable DataSource dataSource) {
        this.dataSource = dataSource instanceof TransactionAwareDataSourceProxy ? ((TransactionAwareDataSourceProxy)dataSource).getTargetDataSource() : dataSource;
    }

    @Nullable
    public DataSource getDataSource() {
        return this.dataSource;
    }

    protected DataSource obtainDataSource() {
        DataSource dataSource = this.getDataSource();
        Assert.state((dataSource != null ? 1 : 0) != 0, (String)"No DataSource set");
        return dataSource;
    }

    public void setEnforceReadOnly(boolean enforceReadOnly) {
        this.enforceReadOnly = enforceReadOnly;
    }

    public boolean isEnforceReadOnly() {
        return this.enforceReadOnly;
    }

    public void afterPropertiesSet() {
        if (this.getDataSource() == null) {
            throw new IllegalArgumentException("Property 'dataSource' is required");
        }
    }

    public Object getResourceFactory() {
        return this.obtainDataSource();
    }

    protected Object doGetTransaction() {
        DataSourceTransactionObject txObject = new DataSourceTransactionObject();
        txObject.setSavepointAllowed(this.isNestedTransactionAllowed());
        ConnectionHolder conHolder = (ConnectionHolder)((Object)TransactionSynchronizationManager.getResource((Object)this.obtainDataSource()));
        txObject.setConnectionHolder(conHolder, false);
        return txObject;
    }

    protected boolean isExistingTransaction(Object transaction) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)transaction;
        return txObject.hasConnectionHolder() && txObject.getConnectionHolder().isTransactionActive();
    }

    protected void doBegin(Object transaction, TransactionDefinition definition) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)transaction;
        Connection con = null;
        try {
            if (!txObject.hasConnectionHolder() || txObject.getConnectionHolder().isSynchronizedWithTransaction()) {
                Connection newCon = this.obtainDataSource().getConnection();
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Acquired Connection [" + newCon + "] for JDBC transaction"));
                }
                txObject.setConnectionHolder(new ConnectionHolder(newCon), true);
            }
            txObject.getConnectionHolder().setSynchronizedWithTransaction(true);
            con = txObject.getConnectionHolder().getConnection();
            Integer previousIsolationLevel = DataSourceUtils.prepareConnectionForTransaction(con, definition);
            txObject.setPreviousIsolationLevel(previousIsolationLevel);
            txObject.setReadOnly(definition.isReadOnly());
            if (con.getAutoCommit()) {
                txObject.setMustRestoreAutoCommit(true);
                if (this.logger.isDebugEnabled()) {
                    this.logger.debug((Object)("Switching JDBC Connection [" + con + "] to manual commit"));
                }
                con.setAutoCommit(false);
            }
            this.prepareTransactionalConnection(con, definition);
            txObject.getConnectionHolder().setTransactionActive(true);
            int timeout = this.determineTimeout(definition);
            if (timeout != -1) {
                txObject.getConnectionHolder().setTimeoutInSeconds(timeout);
            }
            if (txObject.isNewConnectionHolder()) {
                TransactionSynchronizationManager.bindResource((Object)this.obtainDataSource(), (Object)((Object)txObject.getConnectionHolder()));
            }
        }
        catch (Throwable ex) {
            if (txObject.isNewConnectionHolder()) {
                DataSourceUtils.releaseConnection(con, this.obtainDataSource());
                txObject.setConnectionHolder(null, false);
            }
            throw new CannotCreateTransactionException("Could not open JDBC Connection for transaction", ex);
        }
    }

    protected Object doSuspend(Object transaction) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)transaction;
        txObject.setConnectionHolder(null);
        return TransactionSynchronizationManager.unbindResource((Object)this.obtainDataSource());
    }

    protected void doResume(@Nullable Object transaction, Object suspendedResources) {
        TransactionSynchronizationManager.bindResource((Object)this.obtainDataSource(), (Object)suspendedResources);
    }

    protected void doCommit(DefaultTransactionStatus status) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (status.isDebug()) {
            this.logger.debug((Object)("Committing JDBC transaction on Connection [" + con + "]"));
        }
        try {
            con.commit();
        }
        catch (SQLException ex) {
            throw this.translateException("JDBC commit", ex);
        }
    }

    protected void doRollback(DefaultTransactionStatus status) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)status.getTransaction();
        Connection con = txObject.getConnectionHolder().getConnection();
        if (status.isDebug()) {
            this.logger.debug((Object)("Rolling back JDBC transaction on Connection [" + con + "]"));
        }
        try {
            con.rollback();
        }
        catch (SQLException ex) {
            throw this.translateException("JDBC rollback", ex);
        }
    }

    protected void doSetRollbackOnly(DefaultTransactionStatus status) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)status.getTransaction();
        if (status.isDebug()) {
            this.logger.debug((Object)("Setting JDBC transaction [" + txObject.getConnectionHolder().getConnection() + "] rollback-only"));
        }
        txObject.setRollbackOnly();
    }

    protected void doCleanupAfterCompletion(Object transaction) {
        DataSourceTransactionObject txObject = (DataSourceTransactionObject)transaction;
        if (txObject.isNewConnectionHolder()) {
            TransactionSynchronizationManager.unbindResource((Object)this.obtainDataSource());
        }
        Connection con = txObject.getConnectionHolder().getConnection();
        try {
            if (txObject.isMustRestoreAutoCommit()) {
                con.setAutoCommit(true);
            }
            DataSourceUtils.resetConnectionAfterTransaction(con, txObject.getPreviousIsolationLevel(), txObject.isReadOnly());
        }
        catch (Throwable ex) {
            this.logger.debug((Object)"Could not reset JDBC Connection after transaction", ex);
        }
        if (txObject.isNewConnectionHolder()) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug((Object)("Releasing JDBC Connection [" + con + "] after transaction"));
            }
            DataSourceUtils.releaseConnection(con, this.dataSource);
        }
        txObject.getConnectionHolder().clear();
    }

    protected void prepareTransactionalConnection(Connection con, TransactionDefinition definition) throws SQLException {
        if (this.isEnforceReadOnly() && definition.isReadOnly()) {
            try (Statement stmt = con.createStatement();){
                stmt.executeUpdate("SET TRANSACTION READ ONLY");
            }
        }
    }

    protected RuntimeException translateException(String task, SQLException ex) {
        return new TransactionSystemException(task + " failed", (Throwable)ex);
    }

    private static class DataSourceTransactionObject
    extends JdbcTransactionObjectSupport {
        private boolean newConnectionHolder;
        private boolean mustRestoreAutoCommit;

        private DataSourceTransactionObject() {
        }

        public void setConnectionHolder(@Nullable ConnectionHolder connectionHolder, boolean newConnectionHolder) {
            super.setConnectionHolder(connectionHolder);
            this.newConnectionHolder = newConnectionHolder;
        }

        public boolean isNewConnectionHolder() {
            return this.newConnectionHolder;
        }

        public void setMustRestoreAutoCommit(boolean mustRestoreAutoCommit) {
            this.mustRestoreAutoCommit = mustRestoreAutoCommit;
        }

        public boolean isMustRestoreAutoCommit() {
            return this.mustRestoreAutoCommit;
        }

        public void setRollbackOnly() {
            this.getConnectionHolder().setRollbackOnly();
        }

        public boolean isRollbackOnly() {
            return this.getConnectionHolder().isRollbackOnly();
        }

        @Override
        public void flush() {
            if (TransactionSynchronizationManager.isSynchronizationActive()) {
                TransactionSynchronizationUtils.triggerFlush();
            }
        }
    }
}

