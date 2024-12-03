/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.quartz.SchedulerConfigException
 *  org.quartz.impl.jdbcjobstore.JobStoreCMT
 *  org.quartz.impl.jdbcjobstore.Semaphore
 *  org.quartz.impl.jdbcjobstore.SimpleSemaphore
 *  org.quartz.spi.ClassLoadHelper
 *  org.quartz.spi.SchedulerSignaler
 *  org.quartz.utils.ConnectionProvider
 *  org.quartz.utils.DBConnectionManager
 *  org.springframework.jdbc.datasource.DataSourceUtils
 *  org.springframework.jdbc.support.JdbcUtils
 *  org.springframework.jdbc.support.MetaDataAccessException
 *  org.springframework.lang.Nullable
 */
package org.springframework.scheduling.quartz;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import javax.sql.DataSource;
import org.quartz.SchedulerConfigException;
import org.quartz.impl.jdbcjobstore.JobStoreCMT;
import org.quartz.impl.jdbcjobstore.Semaphore;
import org.quartz.impl.jdbcjobstore.SimpleSemaphore;
import org.quartz.spi.ClassLoadHelper;
import org.quartz.spi.SchedulerSignaler;
import org.quartz.utils.ConnectionProvider;
import org.quartz.utils.DBConnectionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.jdbc.support.MetaDataAccessException;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

public class LocalDataSourceJobStore
extends JobStoreCMT {
    public static final String TX_DATA_SOURCE_PREFIX = "springTxDataSource.";
    public static final String NON_TX_DATA_SOURCE_PREFIX = "springNonTxDataSource.";
    @Nullable
    private DataSource dataSource;

    public void initialize(ClassLoadHelper loadHelper, SchedulerSignaler signaler) throws SchedulerConfigException {
        this.dataSource = SchedulerFactoryBean.getConfigTimeDataSource();
        if (this.dataSource == null) {
            throw new SchedulerConfigException("No local DataSource found for configuration - 'dataSource' property must be set on SchedulerFactoryBean");
        }
        this.setDataSource(TX_DATA_SOURCE_PREFIX + this.getInstanceName());
        this.setDontSetAutoCommitFalse(true);
        DBConnectionManager.getInstance().addConnectionProvider(TX_DATA_SOURCE_PREFIX + this.getInstanceName(), new ConnectionProvider(){

            public Connection getConnection() throws SQLException {
                return DataSourceUtils.doGetConnection((DataSource)LocalDataSourceJobStore.this.dataSource);
            }

            public void shutdown() {
            }

            public void initialize() {
            }
        });
        DataSource nonTxDataSource = SchedulerFactoryBean.getConfigTimeNonTransactionalDataSource();
        final DataSource nonTxDataSourceToUse = nonTxDataSource != null ? nonTxDataSource : this.dataSource;
        this.setNonManagedTXDataSource(NON_TX_DATA_SOURCE_PREFIX + this.getInstanceName());
        DBConnectionManager.getInstance().addConnectionProvider(NON_TX_DATA_SOURCE_PREFIX + this.getInstanceName(), new ConnectionProvider(){

            public Connection getConnection() throws SQLException {
                return nonTxDataSourceToUse.getConnection();
            }

            public void shutdown() {
            }

            public void initialize() {
            }
        });
        try {
            String productName = (String)JdbcUtils.extractDatabaseMetaData((DataSource)this.dataSource, DatabaseMetaData::getDatabaseProductName);
            productName = JdbcUtils.commonDatabaseName((String)productName);
            if (productName != null && productName.toLowerCase().contains("hsql")) {
                this.setUseDBLocks(false);
                this.setLockHandler((Semaphore)new SimpleSemaphore());
            }
        }
        catch (MetaDataAccessException ex) {
            this.logWarnIfNonZero(1, "Could not detect database type. Assuming locks can be taken.");
        }
        super.initialize(loadHelper, signaler);
    }

    protected void closeConnection(Connection con) {
        DataSourceUtils.releaseConnection((Connection)con, (DataSource)this.dataSource);
    }
}

