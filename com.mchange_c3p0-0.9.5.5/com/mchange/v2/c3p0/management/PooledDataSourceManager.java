/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.management;

import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.management.PooledDataSourceManagerMBean;
import java.sql.SQLException;
import java.util.Collection;

public class PooledDataSourceManager
implements PooledDataSourceManagerMBean {
    PooledDataSource pds;

    public PooledDataSourceManager(PooledDataSource pds) {
        this.pds = pds;
    }

    @Override
    public String getIdentityToken() {
        return this.pds.getIdentityToken();
    }

    @Override
    public String getDataSourceName() {
        return this.pds.getDataSourceName();
    }

    @Override
    public void setDataSourceName(String dataSourceName) {
        this.pds.setDataSourceName(dataSourceName);
    }

    @Override
    public int getNumConnectionsDefaultUser() throws SQLException {
        return this.pds.getNumConnectionsDefaultUser();
    }

    @Override
    public int getNumIdleConnectionsDefaultUser() throws SQLException {
        return this.pds.getNumIdleConnectionsDefaultUser();
    }

    @Override
    public int getNumBusyConnectionsDefaultUser() throws SQLException {
        return this.pds.getNumBusyConnectionsDefaultUser();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
        return this.pds.getNumUnclosedOrphanedConnectionsDefaultUser();
    }

    @Override
    public float getEffectivePropertyCycleDefaultUser() throws SQLException {
        return this.pds.getEffectivePropertyCycleDefaultUser();
    }

    @Override
    public int getThreadPoolSize() throws SQLException {
        return this.pds.getThreadPoolSize();
    }

    @Override
    public int getThreadPoolNumActiveThreads() throws SQLException {
        return this.pds.getThreadPoolNumActiveThreads();
    }

    @Override
    public int getThreadPoolNumIdleThreads() throws SQLException {
        return this.pds.getThreadPoolNumIdleThreads();
    }

    @Override
    public int getThreadPoolNumTasksPending() throws SQLException {
        return this.pds.getThreadPoolNumTasksPending();
    }

    @Override
    public String sampleThreadPoolStackTraces() throws SQLException {
        return this.pds.sampleThreadPoolStackTraces();
    }

    @Override
    public String sampleThreadPoolStatus() throws SQLException {
        return this.pds.sampleThreadPoolStatus();
    }

    @Override
    public void softResetDefaultUser() throws SQLException {
        this.pds.softResetDefaultUser();
    }

    @Override
    public int getNumConnections(String username, String password) throws SQLException {
        return this.pds.getNumConnections(username, password);
    }

    @Override
    public int getNumIdleConnections(String username, String password) throws SQLException {
        return this.pds.getNumIdleConnections(username, password);
    }

    @Override
    public int getNumBusyConnections(String username, String password) throws SQLException {
        return this.pds.getNumBusyConnections(username, password);
    }

    @Override
    public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
        return this.pds.getNumUnclosedOrphanedConnections(username, password);
    }

    @Override
    public float getEffectivePropertyCycle(String username, String password) throws SQLException {
        return this.pds.getEffectivePropertyCycle(username, password);
    }

    @Override
    public void softReset(String username, String password) throws SQLException {
        this.pds.softReset(username, password);
    }

    @Override
    public int getNumBusyConnectionsAllUsers() throws SQLException {
        return this.pds.getNumBusyConnectionsAllUsers();
    }

    @Override
    public int getNumIdleConnectionsAllUsers() throws SQLException {
        return this.pds.getNumIdleConnectionsAllUsers();
    }

    @Override
    public int getNumConnectionsAllUsers() throws SQLException {
        return this.pds.getNumConnectionsAllUsers();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
        return this.pds.getNumUnclosedOrphanedConnectionsAllUsers();
    }

    @Override
    public void softResetAllUsers() throws SQLException {
        this.pds.softResetAllUsers();
    }

    @Override
    public int getNumUserPools() throws SQLException {
        return this.pds.getNumUserPools();
    }

    @Override
    public Collection getAllUsers() throws SQLException {
        return this.pds.getAllUsers();
    }

    @Override
    public void hardReset() throws SQLException {
        this.pds.hardReset();
    }

    @Override
    public void close() throws SQLException {
        this.pds.close();
    }
}

