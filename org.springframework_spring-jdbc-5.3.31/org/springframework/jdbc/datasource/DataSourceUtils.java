/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.transaction.TransactionDefinition
 *  org.springframework.transaction.support.TransactionSynchronization
 *  org.springframework.transaction.support.TransactionSynchronizationManager
 *  org.springframework.util.Assert
 */
package org.springframework.jdbc.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import javax.sql.DataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.datasource.ConnectionHolder;
import org.springframework.jdbc.datasource.ConnectionProxy;
import org.springframework.jdbc.datasource.DelegatingDataSource;
import org.springframework.jdbc.datasource.SmartDataSource;
import org.springframework.lang.Nullable;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

public abstract class DataSourceUtils {
    public static final int CONNECTION_SYNCHRONIZATION_ORDER = 1000;
    private static final Log logger = LogFactory.getLog(DataSourceUtils.class);

    public static Connection getConnection(DataSource dataSource) throws CannotGetJdbcConnectionException {
        try {
            return DataSourceUtils.doGetConnection(dataSource);
        }
        catch (SQLException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
        catch (IllegalStateException ex) {
            throw new CannotGetJdbcConnectionException("Failed to obtain JDBC Connection", ex);
        }
    }

    public static Connection doGetConnection(DataSource dataSource) throws SQLException {
        Assert.notNull((Object)dataSource, (String)"No DataSource specified");
        ConnectionHolder conHolder = (ConnectionHolder)((Object)TransactionSynchronizationManager.getResource((Object)dataSource));
        if (conHolder != null && (conHolder.hasConnection() || conHolder.isSynchronizedWithTransaction())) {
            conHolder.requested();
            if (!conHolder.hasConnection()) {
                logger.debug((Object)"Fetching resumed JDBC Connection from DataSource");
                conHolder.setConnection(DataSourceUtils.fetchConnection(dataSource));
            }
            return conHolder.getConnection();
        }
        logger.debug((Object)"Fetching JDBC Connection from DataSource");
        Connection con = DataSourceUtils.fetchConnection(dataSource);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            try {
                ConnectionHolder holderToUse = conHolder;
                if (holderToUse == null) {
                    holderToUse = new ConnectionHolder(con);
                } else {
                    holderToUse.setConnection(con);
                }
                holderToUse.requested();
                TransactionSynchronizationManager.registerSynchronization((TransactionSynchronization)new ConnectionSynchronization(holderToUse, dataSource));
                holderToUse.setSynchronizedWithTransaction(true);
                if (holderToUse != conHolder) {
                    TransactionSynchronizationManager.bindResource((Object)dataSource, (Object)((Object)holderToUse));
                }
            }
            catch (RuntimeException ex) {
                DataSourceUtils.releaseConnection(con, dataSource);
                throw ex;
            }
        }
        return con;
    }

    private static Connection fetchConnection(DataSource dataSource) throws SQLException {
        Connection con = dataSource.getConnection();
        if (con == null) {
            throw new IllegalStateException("DataSource returned null from getConnection(): " + dataSource);
        }
        return con;
    }

    @Nullable
    public static Integer prepareConnectionForTransaction(Connection con, @Nullable TransactionDefinition definition) throws SQLException {
        Assert.notNull((Object)con, (String)"No Connection specified");
        boolean debugEnabled = logger.isDebugEnabled();
        if (definition != null && definition.isReadOnly()) {
            try {
                if (debugEnabled) {
                    logger.debug((Object)("Setting JDBC Connection [" + con + "] read-only"));
                }
                con.setReadOnly(true);
            }
            catch (RuntimeException | SQLException ex) {
                for (Throwable exToCheck = ex; exToCheck != null; exToCheck = exToCheck.getCause()) {
                    if (!exToCheck.getClass().getSimpleName().contains("Timeout")) continue;
                    throw ex;
                }
                logger.debug((Object)"Could not set JDBC Connection read-only", (Throwable)ex);
            }
        }
        Integer previousIsolationLevel = null;
        if (definition != null && definition.getIsolationLevel() != -1) {
            int currentIsolation;
            if (debugEnabled) {
                logger.debug((Object)("Changing isolation level of JDBC Connection [" + con + "] to " + definition.getIsolationLevel()));
            }
            if ((currentIsolation = con.getTransactionIsolation()) != definition.getIsolationLevel()) {
                previousIsolationLevel = currentIsolation;
                con.setTransactionIsolation(definition.getIsolationLevel());
            }
        }
        return previousIsolationLevel;
    }

    public static void resetConnectionAfterTransaction(Connection con, @Nullable Integer previousIsolationLevel, boolean resetReadOnly) {
        Assert.notNull((Object)con, (String)"No Connection specified");
        boolean debugEnabled = logger.isDebugEnabled();
        try {
            if (previousIsolationLevel != null) {
                if (debugEnabled) {
                    logger.debug((Object)("Resetting isolation level of JDBC Connection [" + con + "] to " + previousIsolationLevel));
                }
                con.setTransactionIsolation(previousIsolationLevel);
            }
            if (resetReadOnly) {
                if (debugEnabled) {
                    logger.debug((Object)("Resetting read-only flag of JDBC Connection [" + con + "]"));
                }
                con.setReadOnly(false);
            }
        }
        catch (Throwable ex) {
            logger.debug((Object)"Could not reset JDBC Connection after transaction", ex);
        }
    }

    @Deprecated
    public static void resetConnectionAfterTransaction(Connection con, @Nullable Integer previousIsolationLevel) {
        Assert.notNull((Object)con, (String)"No Connection specified");
        try {
            if (previousIsolationLevel != null) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Resetting isolation level of JDBC Connection [" + con + "] to " + previousIsolationLevel));
                }
                con.setTransactionIsolation(previousIsolationLevel);
            }
            if (con.isReadOnly()) {
                if (logger.isDebugEnabled()) {
                    logger.debug((Object)("Resetting read-only flag of JDBC Connection [" + con + "]"));
                }
                con.setReadOnly(false);
            }
        }
        catch (Throwable ex) {
            logger.debug((Object)"Could not reset JDBC Connection after transaction", ex);
        }
    }

    public static boolean isConnectionTransactional(Connection con, @Nullable DataSource dataSource) {
        if (dataSource == null) {
            return false;
        }
        ConnectionHolder conHolder = (ConnectionHolder)((Object)TransactionSynchronizationManager.getResource((Object)dataSource));
        return conHolder != null && DataSourceUtils.connectionEquals(conHolder, con);
    }

    public static void applyTransactionTimeout(Statement stmt, @Nullable DataSource dataSource) throws SQLException {
        DataSourceUtils.applyTimeout(stmt, dataSource, -1);
    }

    public static void applyTimeout(Statement stmt, @Nullable DataSource dataSource, int timeout) throws SQLException {
        Assert.notNull((Object)stmt, (String)"No Statement specified");
        ConnectionHolder holder = null;
        if (dataSource != null) {
            holder = (ConnectionHolder)((Object)TransactionSynchronizationManager.getResource((Object)dataSource));
        }
        if (holder != null && holder.hasTimeout()) {
            stmt.setQueryTimeout(holder.getTimeToLiveInSeconds());
        } else if (timeout >= 0) {
            stmt.setQueryTimeout(timeout);
        }
    }

    public static void releaseConnection(@Nullable Connection con, @Nullable DataSource dataSource) {
        try {
            DataSourceUtils.doReleaseConnection(con, dataSource);
        }
        catch (SQLException ex) {
            logger.debug((Object)"Could not close JDBC Connection", (Throwable)ex);
        }
        catch (Throwable ex) {
            logger.debug((Object)"Unexpected exception on closing JDBC Connection", ex);
        }
    }

    public static void doReleaseConnection(@Nullable Connection con, @Nullable DataSource dataSource) throws SQLException {
        ConnectionHolder conHolder;
        if (con == null) {
            return;
        }
        if (dataSource != null && (conHolder = (ConnectionHolder)((Object)TransactionSynchronizationManager.getResource((Object)dataSource))) != null && DataSourceUtils.connectionEquals(conHolder, con)) {
            conHolder.released();
            return;
        }
        DataSourceUtils.doCloseConnection(con, dataSource);
    }

    public static void doCloseConnection(Connection con, @Nullable DataSource dataSource) throws SQLException {
        if (!(dataSource instanceof SmartDataSource) || ((SmartDataSource)dataSource).shouldClose(con)) {
            con.close();
        }
    }

    private static boolean connectionEquals(ConnectionHolder conHolder, Connection passedInCon) {
        if (!conHolder.hasConnection()) {
            return false;
        }
        Connection heldCon = conHolder.getConnection();
        return heldCon == passedInCon || heldCon.equals(passedInCon) || DataSourceUtils.getTargetConnection(heldCon).equals(passedInCon);
    }

    public static Connection getTargetConnection(Connection con) {
        Connection conToUse = con;
        while (conToUse instanceof ConnectionProxy) {
            conToUse = ((ConnectionProxy)conToUse).getTargetConnection();
        }
        return conToUse;
    }

    private static int getConnectionSynchronizationOrder(DataSource dataSource) {
        int order = 1000;
        DataSource currDs = dataSource;
        while (currDs instanceof DelegatingDataSource) {
            --order;
            currDs = ((DelegatingDataSource)currDs).getTargetDataSource();
        }
        return order;
    }

    private static class ConnectionSynchronization
    implements TransactionSynchronization {
        private final ConnectionHolder connectionHolder;
        private final DataSource dataSource;
        private final int order;
        private boolean holderActive = true;

        public ConnectionSynchronization(ConnectionHolder connectionHolder, DataSource dataSource) {
            this.connectionHolder = connectionHolder;
            this.dataSource = dataSource;
            this.order = DataSourceUtils.getConnectionSynchronizationOrder(dataSource);
        }

        public int getOrder() {
            return this.order;
        }

        public void suspend() {
            if (this.holderActive) {
                TransactionSynchronizationManager.unbindResource((Object)this.dataSource);
                if (this.connectionHolder.hasConnection() && !this.connectionHolder.isOpen()) {
                    DataSourceUtils.releaseConnection(this.connectionHolder.getConnection(), this.dataSource);
                    this.connectionHolder.setConnection(null);
                }
            }
        }

        public void resume() {
            if (this.holderActive) {
                TransactionSynchronizationManager.bindResource((Object)this.dataSource, (Object)((Object)this.connectionHolder));
            }
        }

        public void beforeCompletion() {
            if (!this.connectionHolder.isOpen()) {
                TransactionSynchronizationManager.unbindResource((Object)this.dataSource);
                this.holderActive = false;
                if (this.connectionHolder.hasConnection()) {
                    DataSourceUtils.releaseConnection(this.connectionHolder.getConnection(), this.dataSource);
                }
            }
        }

        public void afterCompletion(int status) {
            if (this.holderActive) {
                TransactionSynchronizationManager.unbindResourceIfPossible((Object)this.dataSource);
                this.holderActive = false;
                if (this.connectionHolder.hasConnection()) {
                    DataSourceUtils.releaseConnection(this.connectionHolder.getConnection(), this.dataSource);
                    this.connectionHolder.setConnection(null);
                }
            }
            this.connectionHolder.reset();
        }
    }
}

