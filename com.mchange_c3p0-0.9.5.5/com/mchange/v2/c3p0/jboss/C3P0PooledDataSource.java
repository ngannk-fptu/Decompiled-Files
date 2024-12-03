/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0.jboss;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.jboss.C3P0PooledDataSourceMBean;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.PropertyVetoException;
import java.sql.SQLException;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NameAlreadyBoundException;
import javax.naming.NamingException;

public class C3P0PooledDataSource
implements C3P0PooledDataSourceMBean {
    private static final MLogger logger = MLog.getLogger(C3P0PooledDataSource.class);
    String jndiName;
    ComboPooledDataSource combods = new ComboPooledDataSource();

    private void rebind() throws NamingException {
        this.rebind(null);
    }

    private void rebind(String unbindName) throws NamingException {
        InitialContext ictx = new InitialContext();
        if (unbindName != null) {
            ictx.unbind(unbindName);
        }
        if (this.jndiName != null) {
            Name name = ictx.getNameParser(this.jndiName).parse(this.jndiName);
            Context ctx = ictx;
            int max = name.size() - 1;
            for (int i = 0; i < max; ++i) {
                try {
                    ctx = ctx.createSubcontext(name.get(i));
                    continue;
                }
                catch (NameAlreadyBoundException ignore) {
                    ctx = (Context)ctx.lookup(name.get(i));
                }
            }
            ictx.rebind(this.jndiName, (Object)this.combods);
        }
    }

    @Override
    public void setJndiName(String jndiName) throws NamingException {
        String unbindName = this.jndiName;
        this.jndiName = jndiName;
        this.rebind(unbindName);
    }

    @Override
    public String getJndiName() {
        return this.jndiName;
    }

    @Override
    public String getDescription() {
        return this.combods.getDescription();
    }

    @Override
    public void setDescription(String description) throws NamingException {
        this.combods.setDescription(description);
        this.rebind();
    }

    @Override
    public String getDriverClass() {
        return this.combods.getDriverClass();
    }

    @Override
    public void setDriverClass(String driverClass) throws PropertyVetoException, NamingException {
        this.combods.setDriverClass(driverClass);
        this.rebind();
    }

    @Override
    public String getJdbcUrl() {
        return this.combods.getJdbcUrl();
    }

    @Override
    public void setJdbcUrl(String jdbcUrl) throws NamingException {
        this.combods.setJdbcUrl(jdbcUrl);
        this.rebind();
    }

    @Override
    public String getUser() {
        return this.combods.getUser();
    }

    @Override
    public void setUser(String user) throws NamingException {
        this.combods.setUser(user);
        this.rebind();
    }

    @Override
    public String getPassword() {
        return this.combods.getPassword();
    }

    @Override
    public void setPassword(String password) throws NamingException {
        this.combods.setPassword(password);
        this.rebind();
    }

    @Override
    public int getCheckoutTimeout() {
        return this.combods.getCheckoutTimeout();
    }

    @Override
    public void setCheckoutTimeout(int checkoutTimeout) throws NamingException {
        this.combods.setCheckoutTimeout(checkoutTimeout);
        this.rebind();
    }

    @Override
    public int getAcquireIncrement() {
        return this.combods.getAcquireIncrement();
    }

    @Override
    public void setAcquireIncrement(int acquireIncrement) throws NamingException {
        this.combods.setAcquireIncrement(acquireIncrement);
        this.rebind();
    }

    @Override
    public int getAcquireRetryAttempts() {
        return this.combods.getAcquireRetryAttempts();
    }

    @Override
    public void setAcquireRetryAttempts(int acquireRetryAttempts) throws NamingException {
        this.combods.setAcquireRetryAttempts(acquireRetryAttempts);
        this.rebind();
    }

    @Override
    public int getAcquireRetryDelay() {
        return this.combods.getAcquireRetryDelay();
    }

    @Override
    public void setAcquireRetryDelay(int acquireRetryDelay) throws NamingException {
        this.combods.setAcquireRetryDelay(acquireRetryDelay);
        this.rebind();
    }

    @Override
    public boolean isAutoCommitOnClose() {
        return this.combods.isAutoCommitOnClose();
    }

    @Override
    public void setAutoCommitOnClose(boolean autoCommitOnClose) throws NamingException {
        this.combods.setAutoCommitOnClose(autoCommitOnClose);
        this.rebind();
    }

    @Override
    public String getConnectionTesterClassName() {
        return this.combods.getConnectionTesterClassName();
    }

    @Override
    public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException, NamingException {
        this.combods.setConnectionTesterClassName(connectionTesterClassName);
        this.rebind();
    }

    @Override
    public String getAutomaticTestTable() {
        return this.combods.getAutomaticTestTable();
    }

    @Override
    public void setAutomaticTestTable(String automaticTestTable) throws NamingException {
        this.combods.setAutomaticTestTable(automaticTestTable);
        this.rebind();
    }

    @Override
    public boolean isForceIgnoreUnresolvedTransactions() {
        return this.combods.isForceIgnoreUnresolvedTransactions();
    }

    @Override
    public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) throws NamingException {
        this.combods.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
        this.rebind();
    }

    @Override
    public int getIdleConnectionTestPeriod() {
        return this.combods.getIdleConnectionTestPeriod();
    }

    @Override
    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) throws NamingException {
        this.combods.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
        this.rebind();
    }

    @Override
    public int getInitialPoolSize() {
        return this.combods.getInitialPoolSize();
    }

    @Override
    public void setInitialPoolSize(int initialPoolSize) throws NamingException {
        this.combods.setInitialPoolSize(initialPoolSize);
        this.rebind();
    }

    @Override
    public int getMaxIdleTime() {
        return this.combods.getMaxIdleTime();
    }

    @Override
    public void setMaxIdleTime(int maxIdleTime) throws NamingException {
        this.combods.setMaxIdleTime(maxIdleTime);
        this.rebind();
    }

    @Override
    public int getMaxPoolSize() {
        return this.combods.getMaxPoolSize();
    }

    @Override
    public void setMaxPoolSize(int maxPoolSize) throws NamingException {
        this.combods.setMaxPoolSize(maxPoolSize);
        this.rebind();
    }

    @Override
    public int getMaxStatements() {
        return this.combods.getMaxStatements();
    }

    @Override
    public void setMaxStatements(int maxStatements) throws NamingException {
        this.combods.setMaxStatements(maxStatements);
        this.rebind();
    }

    @Override
    public int getMaxStatementsPerConnection() {
        return this.combods.getMaxStatementsPerConnection();
    }

    @Override
    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) throws NamingException {
        this.combods.setMaxStatementsPerConnection(maxStatementsPerConnection);
        this.rebind();
    }

    @Override
    public int getMinPoolSize() {
        return this.combods.getMinPoolSize();
    }

    @Override
    public void setMinPoolSize(int minPoolSize) throws NamingException {
        this.combods.setMinPoolSize(minPoolSize);
        this.rebind();
    }

    @Override
    public int getPropertyCycle() {
        return this.combods.getPropertyCycle();
    }

    @Override
    public void setPropertyCycle(int propertyCycle) throws NamingException {
        this.combods.setPropertyCycle(propertyCycle);
        this.rebind();
    }

    @Override
    public boolean isBreakAfterAcquireFailure() {
        return this.combods.isBreakAfterAcquireFailure();
    }

    @Override
    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) throws NamingException {
        this.combods.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
        this.rebind();
    }

    @Override
    public boolean isTestConnectionOnCheckout() {
        return this.combods.isTestConnectionOnCheckout();
    }

    @Override
    public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) throws NamingException {
        this.combods.setTestConnectionOnCheckout(testConnectionOnCheckout);
        this.rebind();
    }

    @Override
    public boolean isTestConnectionOnCheckin() {
        return this.combods.isTestConnectionOnCheckin();
    }

    @Override
    public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) throws NamingException {
        this.combods.setTestConnectionOnCheckin(testConnectionOnCheckin);
        this.rebind();
    }

    @Override
    public boolean isUsesTraditionalReflectiveProxies() {
        return this.combods.isUsesTraditionalReflectiveProxies();
    }

    @Override
    public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) throws NamingException {
        this.combods.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
        this.rebind();
    }

    @Override
    public String getPreferredTestQuery() {
        return this.combods.getPreferredTestQuery();
    }

    @Override
    public void setPreferredTestQuery(String preferredTestQuery) throws NamingException {
        this.combods.setPreferredTestQuery(preferredTestQuery);
        this.rebind();
    }

    public String getDataSourceName() {
        return this.combods.getDataSourceName();
    }

    public void setDataSourceName(String name) throws NamingException {
        this.combods.setDataSourceName(name);
        this.rebind();
    }

    @Override
    public int getNumHelperThreads() {
        return this.combods.getNumHelperThreads();
    }

    @Override
    public void setNumHelperThreads(int numHelperThreads) throws NamingException {
        this.combods.setNumHelperThreads(numHelperThreads);
        this.rebind();
    }

    @Override
    public String getFactoryClassLocation() {
        return this.combods.getFactoryClassLocation();
    }

    @Override
    public void setFactoryClassLocation(String factoryClassLocation) throws NamingException {
        this.combods.setFactoryClassLocation(factoryClassLocation);
        this.rebind();
    }

    @Override
    public int getNumUserPools() throws SQLException {
        return this.combods.getNumUserPools();
    }

    @Override
    public int getNumConnectionsDefaultUser() throws SQLException {
        return this.combods.getNumConnectionsDefaultUser();
    }

    @Override
    public int getNumIdleConnectionsDefaultUser() throws SQLException {
        return this.combods.getNumIdleConnectionsDefaultUser();
    }

    @Override
    public int getNumBusyConnectionsDefaultUser() throws SQLException {
        return this.combods.getNumBusyConnectionsDefaultUser();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsDefaultUser() throws SQLException {
        return this.combods.getNumUnclosedOrphanedConnectionsDefaultUser();
    }

    @Override
    public int getNumConnections(String username, String password) throws SQLException {
        return this.combods.getNumConnections(username, password);
    }

    @Override
    public int getNumIdleConnections(String username, String password) throws SQLException {
        return this.combods.getNumIdleConnections(username, password);
    }

    @Override
    public int getNumBusyConnections(String username, String password) throws SQLException {
        return this.combods.getNumBusyConnections(username, password);
    }

    @Override
    public int getNumUnclosedOrphanedConnections(String username, String password) throws SQLException {
        return this.combods.getNumUnclosedOrphanedConnections(username, password);
    }

    @Override
    public int getNumConnectionsAllUsers() throws SQLException {
        return this.combods.getNumConnectionsAllUsers();
    }

    @Override
    public int getNumIdleConnectionsAllUsers() throws SQLException {
        return this.combods.getNumIdleConnectionsAllUsers();
    }

    @Override
    public int getNumBusyConnectionsAllUsers() throws SQLException {
        return this.combods.getNumBusyConnectionsAllUsers();
    }

    @Override
    public int getNumUnclosedOrphanedConnectionsAllUsers() throws SQLException {
        return this.combods.getNumUnclosedOrphanedConnectionsAllUsers();
    }

    @Override
    public void softResetDefaultUser() throws SQLException {
        this.combods.softResetDefaultUser();
    }

    @Override
    public void softReset(String username, String password) throws SQLException {
        this.combods.softReset(username, password);
    }

    @Override
    public void softResetAllUsers() throws SQLException {
        this.combods.softResetAllUsers();
    }

    @Override
    public void hardReset() throws SQLException {
        this.combods.hardReset();
    }

    @Override
    public void close() throws SQLException {
        this.combods.close();
    }

    @Override
    public void create() throws Exception {
    }

    @Override
    public void start() throws Exception {
        logger.log(MLevel.INFO, "Bound C3P0 PooledDataSource to name ''{0}''. Starting...", (Object)this.jndiName);
        this.combods.getNumBusyConnectionsDefaultUser();
    }

    @Override
    public void stop() {
    }

    @Override
    public void destroy() {
        try {
            this.combods.close();
            logger.log(MLevel.INFO, "Destroyed C3P0 PooledDataSource with name ''{0}''.", (Object)this.jndiName);
        }
        catch (Exception e) {
            logger.log(MLevel.INFO, "Failed to destroy C3P0 PooledDataSource.", (Throwable)e);
        }
    }

    @Override
    public String getConnectionCustomizerClassName() {
        return this.combods.getConnectionCustomizerClassName();
    }

    @Override
    public float getEffectivePropertyCycle(String username, String password) throws SQLException {
        return this.combods.getEffectivePropertyCycle(username, password);
    }

    @Override
    public float getEffectivePropertyCycleDefaultUser() throws SQLException {
        return this.combods.getEffectivePropertyCycleDefaultUser();
    }

    @Override
    public int getMaxAdministrativeTaskTime() {
        return this.combods.getMaxAdministrativeTaskTime();
    }

    @Override
    public int getMaxConnectionAge() {
        return this.combods.getMaxConnectionAge();
    }

    @Override
    public int getMaxIdleTimeExcessConnections() {
        return this.combods.getMaxIdleTimeExcessConnections();
    }

    @Override
    public int getUnreturnedConnectionTimeout() {
        return this.combods.getUnreturnedConnectionTimeout();
    }

    @Override
    public boolean isDebugUnreturnedConnectionStackTraces() {
        return this.combods.isDebugUnreturnedConnectionStackTraces();
    }

    @Override
    public boolean isForceSynchronousCheckins() {
        return this.combods.isForceSynchronousCheckins();
    }

    @Override
    public void setConnectionCustomizerClassName(String connectionCustomizerClassName) throws NamingException {
        this.combods.setConnectionCustomizerClassName(connectionCustomizerClassName);
        this.rebind();
    }

    @Override
    public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) throws NamingException {
        this.combods.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
        this.rebind();
    }

    @Override
    public void setForceSynchronousCheckins(boolean forceSynchronousCheckins) throws NamingException {
        this.combods.setForceSynchronousCheckins(forceSynchronousCheckins);
        this.rebind();
    }

    @Override
    public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) throws NamingException {
        this.combods.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
        this.rebind();
    }

    @Override
    public void setMaxConnectionAge(int maxConnectionAge) throws NamingException {
        this.combods.setMaxConnectionAge(maxConnectionAge);
        this.rebind();
    }

    @Override
    public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) throws NamingException {
        this.combods.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
        this.rebind();
    }

    @Override
    public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) throws NamingException {
        this.combods.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
        this.rebind();
    }
}

