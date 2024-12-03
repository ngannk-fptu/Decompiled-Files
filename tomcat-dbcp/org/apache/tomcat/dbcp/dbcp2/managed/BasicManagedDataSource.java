/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.transaction.TransactionManager
 *  javax.transaction.TransactionSynchronizationRegistry
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import java.sql.SQLException;
import javax.sql.DataSource;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.TransactionSynchronizationRegistry;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.dbcp2.ConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnection;
import org.apache.tomcat.dbcp.dbcp2.PoolableConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.Utils;
import org.apache.tomcat.dbcp.dbcp2.managed.DataSourceXAConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.managed.LocalXAConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.managed.ManagedDataSource;
import org.apache.tomcat.dbcp.dbcp2.managed.PoolableManagedConnectionFactory;
import org.apache.tomcat.dbcp.dbcp2.managed.TransactionRegistry;
import org.apache.tomcat.dbcp.dbcp2.managed.XAConnectionFactory;

public class BasicManagedDataSource
extends BasicDataSource {
    private TransactionRegistry transactionRegistry;
    private transient TransactionManager transactionManager;
    private String xaDataSource;
    private XADataSource xaDataSourceInstance;
    private transient TransactionSynchronizationRegistry transactionSynchronizationRegistry;

    @Override
    protected ConnectionFactory createConnectionFactory() throws SQLException {
        if (this.transactionManager == null) {
            throw new SQLException("Transaction manager must be set before a connection can be created");
        }
        if (this.xaDataSource == null) {
            ConnectionFactory connectionFactory = super.createConnectionFactory();
            LocalXAConnectionFactory xaConnectionFactory = new LocalXAConnectionFactory(this.getTransactionManager(), this.getTransactionSynchronizationRegistry(), connectionFactory);
            this.transactionRegistry = xaConnectionFactory.getTransactionRegistry();
            return xaConnectionFactory;
        }
        if (this.xaDataSourceInstance == null) {
            Class<?> xaDataSourceClass = null;
            try {
                xaDataSourceClass = Class.forName(this.xaDataSource);
            }
            catch (Exception t) {
                String message = "Cannot load XA data source class '" + this.xaDataSource + "'";
                throw new SQLException(message, t);
            }
            try {
                this.xaDataSourceInstance = (XADataSource)xaDataSourceClass.getConstructor(new Class[0]).newInstance(new Object[0]);
            }
            catch (Exception t) {
                String message = "Cannot create XA data source of class '" + this.xaDataSource + "'";
                throw new SQLException(message, t);
            }
        }
        DataSourceXAConnectionFactory xaConnectionFactory = new DataSourceXAConnectionFactory(this.getTransactionManager(), this.xaDataSourceInstance, this.getUsername(), Utils.toCharArray(this.getPassword()), this.getTransactionSynchronizationRegistry());
        this.transactionRegistry = xaConnectionFactory.getTransactionRegistry();
        return xaConnectionFactory;
    }

    @Override
    protected DataSource createDataSourceInstance() throws SQLException {
        ManagedDataSource<PoolableConnection> pds = new ManagedDataSource<PoolableConnection>(this.getConnectionPool(), this.transactionRegistry);
        pds.setAccessToUnderlyingConnectionAllowed(this.isAccessToUnderlyingConnectionAllowed());
        return pds;
    }

    @Override
    protected PoolableConnectionFactory createPoolableConnectionFactory(ConnectionFactory driverConnectionFactory) throws SQLException {
        PoolableManagedConnectionFactory connectionFactory = null;
        try {
            connectionFactory = new PoolableManagedConnectionFactory((XAConnectionFactory)driverConnectionFactory, this.getRegisteredJmxName());
            connectionFactory.setValidationQuery(this.getValidationQuery());
            connectionFactory.setValidationQueryTimeout(this.getValidationQueryTimeoutDuration());
            connectionFactory.setConnectionInitSql(this.getConnectionInitSqls());
            connectionFactory.setDefaultReadOnly(this.getDefaultReadOnly());
            connectionFactory.setDefaultAutoCommit(this.getDefaultAutoCommit());
            connectionFactory.setDefaultTransactionIsolation(this.getDefaultTransactionIsolation());
            connectionFactory.setDefaultCatalog(this.getDefaultCatalog());
            connectionFactory.setDefaultSchema(this.getDefaultSchema());
            connectionFactory.setCacheState(this.getCacheState());
            connectionFactory.setPoolStatements(this.isPoolPreparedStatements());
            connectionFactory.setClearStatementPoolOnReturn(this.isClearStatementPoolOnReturn());
            connectionFactory.setMaxOpenPreparedStatements(this.getMaxOpenPreparedStatements());
            connectionFactory.setMaxConn(this.getMaxConnDuration());
            connectionFactory.setRollbackOnReturn(this.getRollbackOnReturn());
            connectionFactory.setAutoCommitOnReturn(this.getAutoCommitOnReturn());
            connectionFactory.setDefaultQueryTimeout(this.getDefaultQueryTimeoutDuration());
            connectionFactory.setFastFailValidation(this.getFastFailValidation());
            connectionFactory.setDisconnectionSqlCodes(this.getDisconnectionSqlCodes());
            BasicManagedDataSource.validateConnectionFactory(connectionFactory);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new SQLException("Cannot create PoolableConnectionFactory (" + e.getMessage() + ")", e);
        }
        return connectionFactory;
    }

    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    protected synchronized TransactionRegistry getTransactionRegistry() {
        return this.transactionRegistry;
    }

    public TransactionSynchronizationRegistry getTransactionSynchronizationRegistry() {
        return this.transactionSynchronizationRegistry;
    }

    public synchronized String getXADataSource() {
        return this.xaDataSource;
    }

    public synchronized XADataSource getXaDataSourceInstance() {
        return this.xaDataSourceInstance;
    }

    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    public void setTransactionSynchronizationRegistry(TransactionSynchronizationRegistry transactionSynchronizationRegistry) {
        this.transactionSynchronizationRegistry = transactionSynchronizationRegistry;
    }

    public synchronized void setXADataSource(String xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    public synchronized void setXaDataSourceInstance(XADataSource xaDataSourceInstance) {
        this.xaDataSourceInstance = xaDataSourceInstance;
        this.xaDataSource = xaDataSourceInstance == null ? null : xaDataSourceInstance.getClass().getName();
    }
}

