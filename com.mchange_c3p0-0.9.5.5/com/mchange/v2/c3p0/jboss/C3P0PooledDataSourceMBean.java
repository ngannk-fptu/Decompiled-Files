/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.c3p0.jboss;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.naming.NamingException;

public interface C3P0PooledDataSourceMBean {
    public void setJndiName(String var1) throws NamingException;

    public String getJndiName();

    public String getDescription();

    public void setDescription(String var1) throws NamingException;

    public String getDriverClass();

    public void setDriverClass(String var1) throws PropertyVetoException, NamingException;

    public String getJdbcUrl();

    public void setJdbcUrl(String var1) throws NamingException;

    public String getUser();

    public void setUser(String var1) throws NamingException;

    public String getPassword();

    public void setPassword(String var1) throws NamingException;

    public int getUnreturnedConnectionTimeout();

    public void setUnreturnedConnectionTimeout(int var1) throws NamingException;

    public boolean isDebugUnreturnedConnectionStackTraces();

    public void setDebugUnreturnedConnectionStackTraces(boolean var1) throws NamingException;

    public boolean isForceSynchronousCheckins();

    public void setForceSynchronousCheckins(boolean var1) throws NamingException;

    public String getConnectionCustomizerClassName();

    public void setConnectionCustomizerClassName(String var1) throws NamingException;

    public int getMaxConnectionAge();

    public void setMaxConnectionAge(int var1) throws NamingException;

    public int getMaxIdleTimeExcessConnections();

    public void setMaxIdleTimeExcessConnections(int var1) throws NamingException;

    public int getMaxAdministrativeTaskTime();

    public void setMaxAdministrativeTaskTime(int var1) throws NamingException;

    public int getCheckoutTimeout();

    public void setCheckoutTimeout(int var1) throws NamingException;

    public int getAcquireIncrement();

    public void setAcquireIncrement(int var1) throws NamingException;

    public int getAcquireRetryAttempts();

    public void setAcquireRetryAttempts(int var1) throws NamingException;

    public int getAcquireRetryDelay();

    public void setAcquireRetryDelay(int var1) throws NamingException;

    public boolean isAutoCommitOnClose();

    public void setAutoCommitOnClose(boolean var1) throws NamingException;

    public String getConnectionTesterClassName();

    public void setConnectionTesterClassName(String var1) throws PropertyVetoException, NamingException;

    public String getAutomaticTestTable();

    public void setAutomaticTestTable(String var1) throws NamingException;

    public boolean isForceIgnoreUnresolvedTransactions();

    public void setForceIgnoreUnresolvedTransactions(boolean var1) throws NamingException;

    public int getIdleConnectionTestPeriod();

    public void setIdleConnectionTestPeriod(int var1) throws NamingException;

    public int getInitialPoolSize();

    public void setInitialPoolSize(int var1) throws NamingException;

    public int getMaxIdleTime();

    public void setMaxIdleTime(int var1) throws NamingException;

    public int getMaxPoolSize();

    public void setMaxPoolSize(int var1) throws NamingException;

    public int getMaxStatements();

    public void setMaxStatements(int var1) throws NamingException;

    public int getMaxStatementsPerConnection();

    public void setMaxStatementsPerConnection(int var1) throws NamingException;

    public int getMinPoolSize();

    public void setMinPoolSize(int var1) throws NamingException;

    public int getPropertyCycle();

    public void setPropertyCycle(int var1) throws NamingException;

    public boolean isBreakAfterAcquireFailure();

    public void setBreakAfterAcquireFailure(boolean var1) throws NamingException;

    public boolean isTestConnectionOnCheckout();

    public void setTestConnectionOnCheckout(boolean var1) throws NamingException;

    public boolean isTestConnectionOnCheckin();

    public void setTestConnectionOnCheckin(boolean var1) throws NamingException;

    public boolean isUsesTraditionalReflectiveProxies();

    public void setUsesTraditionalReflectiveProxies(boolean var1) throws NamingException;

    public String getPreferredTestQuery();

    public void setPreferredTestQuery(String var1) throws NamingException;

    public int getNumHelperThreads();

    public void setNumHelperThreads(int var1) throws NamingException;

    public String getFactoryClassLocation();

    public void setFactoryClassLocation(String var1) throws NamingException;

    public int getNumUserPools() throws SQLException;

    public int getNumConnectionsDefaultUser() throws SQLException;

    public int getNumIdleConnectionsDefaultUser() throws SQLException;

    public int getNumBusyConnectionsDefaultUser() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException;

    public int getNumConnections(String var1, String var2) throws SQLException;

    public int getNumIdleConnections(String var1, String var2) throws SQLException;

    public int getNumBusyConnections(String var1, String var2) throws SQLException;

    public int getNumUnclosedOrphanedConnections(String var1, String var2) throws SQLException;

    public float getEffectivePropertyCycle(String var1, String var2) throws SQLException;

    public int getNumBusyConnectionsAllUsers() throws SQLException;

    public int getNumIdleConnectionsAllUsers() throws SQLException;

    public int getNumConnectionsAllUsers() throws SQLException;

    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException;

    public float getEffectivePropertyCycleDefaultUser() throws SQLException;

    public void softResetDefaultUser() throws SQLException;

    public void softReset(String var1, String var2) throws SQLException;

    public void softResetAllUsers() throws SQLException;

    public void hardReset() throws SQLException;

    public void close() throws SQLException;

    public void create() throws Exception;

    public void start() throws Exception;

    public void stop();

    public void destroy();
}

