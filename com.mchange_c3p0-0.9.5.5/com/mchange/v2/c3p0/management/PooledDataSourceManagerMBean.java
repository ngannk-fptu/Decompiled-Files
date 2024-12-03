/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.management;

import java.sql.SQLException;
import java.util.Collection;

public interface PooledDataSourceManagerMBean {
    public String getIdentityToken();

    public String getDataSourceName();

    public void setDataSourceName(String var1);

    public int getNumConnectionsDefaultUser() throws SQLException;

    public int getNumIdleConnectionsDefaultUser() throws SQLException;

    public int getNumBusyConnectionsDefaultUser() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException;

    public float getEffectivePropertyCycleDefaultUser() throws SQLException;

    public void softResetDefaultUser() throws SQLException;

    public int getNumConnections(String var1, String var2) throws SQLException;

    public int getNumIdleConnections(String var1, String var2) throws SQLException;

    public int getNumBusyConnections(String var1, String var2) throws SQLException;

    public int getNumUnclosedOrphanedConnections(String var1, String var2) throws SQLException;

    public float getEffectivePropertyCycle(String var1, String var2) throws SQLException;

    public void softReset(String var1, String var2) throws SQLException;

    public int getNumBusyConnectionsAllUsers() throws SQLException;

    public int getNumIdleConnectionsAllUsers() throws SQLException;

    public int getNumConnectionsAllUsers() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException;

    public int getThreadPoolSize() throws SQLException;

    public int getThreadPoolNumActiveThreads() throws SQLException;

    public int getThreadPoolNumIdleThreads() throws SQLException;

    public int getThreadPoolNumTasksPending() throws SQLException;

    public String sampleThreadPoolStackTraces() throws SQLException;

    public String sampleThreadPoolStatus() throws SQLException;

    public void softResetAllUsers() throws SQLException;

    public int getNumUserPools() throws SQLException;

    public Collection getAllUsers() throws SQLException;

    public void hardReset() throws SQLException;

    public void close() throws SQLException;
}

