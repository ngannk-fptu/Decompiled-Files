/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.beans.BeansUtils
 *  com.mchange.v2.lang.ObjectUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
import com.mchange.v2.c3p0.impl.AbstractPoolBackedDataSource;
import com.mchange.v2.lang.ObjectUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.beans.VetoableChangeListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.naming.Referenceable;
import javax.sql.DataSource;

public abstract class AbstractComboPooledDataSource
extends AbstractPoolBackedDataSource
implements PooledDataSource,
Serializable,
Referenceable {
    static final MLogger logger = MLog.getLogger(AbstractComboPooledDataSource.class);
    static final Set TO_STRING_IGNORE_PROPS = new HashSet<String>(Arrays.asList("connection", "lastAcquisitionFailureDefaultUser", "lastCheckinFailureDefaultUser", "lastCheckoutFailureDefaultUser", "lastConnectionTestFailureDefaultUser", "lastIdleTestFailureDefaultUser", "logWriter", "loginTimeout", "numBusyConnections", "numBusyConnectionsAllUsers", "numBusyConnectionsDefaultUser", "numConnections", "numConnectionsAllUsers", "numConnectionsDefaultUser", "numFailedCheckinsDefaultUser", "numFailedCheckoutsDefaultUser", "numFailedIdleTestsDefaultUser", "numIdleConnections", "numIdleConnectionsAllUsers", "numThreadsAwaitingCheckoutDefaultUser", "numIdleConnectionsDefaultUser", "numUnclosedOrphanedConnections", "numUnclosedOrphanedConnectionsAllUsers", "numUnclosedOrphanedConnectionsDefaultUser", "numUserPools", "effectivePropertyCycleDefaultUser", "parentLogger", "startTimeMillisDefaultUser", "statementCacheNumCheckedOutDefaultUser", "statementCacheNumCheckedOutStatementsAllUsers", "statementCacheNumConnectionsWithCachedStatementsAllUsers", "statementCacheNumConnectionsWithCachedStatementsDefaultUser", "statementCacheNumStatementsAllUsers", "statementCacheNumStatementsDefaultUser", "statementDestroyerNumConnectionsInUseAllUsers", "statementDestroyerNumConnectionsWithDeferredDestroyStatementsAllUsers", "statementDestroyerNumDeferredDestroyStatementsAllUsers", "statementDestroyerNumConnectionsInUseDefaultUser", "statementDestroyerNumConnectionsWithDeferredDestroyStatementsDefaultUser", "statementDestroyerNumDeferredDestroyStatementsDefaultUser", "statementDestroyerNumThreads", "statementDestroyerNumActiveThreads", "statementDestroyerNumIdleThreads", "statementDestroyerNumTasksPending", "threadPoolSize", "threadPoolNumActiveThreads", "threadPoolNumIdleThreads", "threadPoolNumTasksPending", "threadPoolStackTraces", "threadPoolStatus", "overrideDefaultUser", "overrideDefaultPassword", "password", "reference", "upTimeMillisDefaultUser", "user", "userOverridesAsString", "allUsers", "connectionPoolDataSource", "propertyChangeListeners", "vetoableChangeListeners"));
    transient DriverManagerDataSource dmds = new DriverManagerDataSource();
    transient WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource();
    private static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    private static boolean diff(int a, int b) {
        return a != b;
    }

    private static boolean diff(boolean a, boolean b) {
        return a != b;
    }

    private static boolean diff(Object a, Object b) {
        return !ObjectUtils.eqOrBothNull((Object)a, (Object)b);
    }

    public AbstractComboPooledDataSource() {
        this(true);
    }

    public AbstractComboPooledDataSource(boolean autoregister) {
        super(autoregister);
        this.wcpds.setNestedDataSource(this.dmds);
        try {
            this.setConnectionPoolDataSource(this.wcpds);
        }
        catch (PropertyVetoException e) {
            logger.log(MLevel.WARNING, "Hunh??? This can't happen. We haven't set up any listeners to veto the property change yet!", (Throwable)e);
            throw new RuntimeException("Hunh??? This can't happen. We haven't set up any listeners to veto the property change yet! " + e);
        }
        this.setUpPropertyEvents();
    }

    private void setUpPropertyEvents() {
        VetoableChangeListener wcpdsConsistencyEnforcer = new VetoableChangeListener(){

            @Override
            public void vetoableChange(PropertyChangeEvent evt) throws PropertyVetoException {
                String propName = evt.getPropertyName();
                Object val = evt.getNewValue();
                if ("connectionPoolDataSource".equals(propName)) {
                    if (val instanceof WrapperConnectionPoolDataSource) {
                        DataSource nested = ((WrapperConnectionPoolDataSource)val).getNestedDataSource();
                        if (!(nested instanceof DriverManagerDataSource)) {
                            throw new PropertyVetoException(this.getClass().getName() + " requires that its unpooled DataSource  be set at all times, and that it be a com.mchange.v2.c3p0.DriverManagerDataSource. Bad: " + nested, evt);
                        }
                    } else {
                        throw new PropertyVetoException(this.getClass().getName() + " requires that its ConnectionPoolDataSource  be set at all times, and that it be a com.mchange.v2.c3p0.WrapperConnectionPoolDataSource. Bad: " + val, evt);
                    }
                }
            }
        };
        this.addVetoableChangeListener(wcpdsConsistencyEnforcer);
        PropertyChangeListener wcpdsStateUpdater = new PropertyChangeListener(){

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                String propName = evt.getPropertyName();
                Object val = evt.getNewValue();
                if ("connectionPoolDataSource".equals(propName)) {
                    AbstractComboPooledDataSource.this.updateLocalVarsFromCpdsProp();
                }
            }
        };
        this.addPropertyChangeListener(wcpdsStateUpdater);
    }

    private void updateLocalVarsFromCpdsProp() {
        this.wcpds = (WrapperConnectionPoolDataSource)this.getConnectionPoolDataSource();
        this.dmds = (DriverManagerDataSource)this.wcpds.getNestedDataSource();
    }

    public AbstractComboPooledDataSource(String configName) {
        this();
        this.initializeNamedConfig(configName, true);
    }

    public String getDescription() {
        return this.dmds.getDescription();
    }

    public void setDescription(String description) {
        this.dmds.setDescription(description);
    }

    public String getDriverClass() {
        return this.dmds.getDriverClass();
    }

    public void setDriverClass(String driverClass) throws PropertyVetoException {
        this.dmds.setDriverClass(driverClass);
    }

    public boolean isForceUseNamedDriverClass() {
        return this.dmds.isForceUseNamedDriverClass();
    }

    public void setForceUseNamedDriverClass(boolean forceUseNamedDriverClass) {
        this.dmds.setForceUseNamedDriverClass(forceUseNamedDriverClass);
    }

    public String getJdbcUrl() {
        return this.dmds.getJdbcUrl();
    }

    public void setJdbcUrl(String jdbcUrl) {
        if (AbstractComboPooledDataSource.diff(this.dmds.getJdbcUrl(), jdbcUrl)) {
            this.dmds.setJdbcUrl(jdbcUrl);
            this.resetPoolManager(false);
        }
    }

    public Properties getProperties() {
        return this.dmds.getProperties();
    }

    public void setProperties(Properties properties) {
        if (AbstractComboPooledDataSource.diff(this.dmds.getProperties(), properties)) {
            this.dmds.setProperties(properties);
            this.resetPoolManager(false);
        }
    }

    public String getUser() {
        return this.dmds.getUser();
    }

    public void setUser(String user) {
        if (AbstractComboPooledDataSource.diff(this.dmds.getUser(), user)) {
            this.dmds.setUser(user);
            this.resetPoolManager(false);
        }
    }

    public String getPassword() {
        return this.dmds.getPassword();
    }

    public void setPassword(String password) {
        if (AbstractComboPooledDataSource.diff(this.dmds.getPassword(), password)) {
            this.dmds.setPassword(password);
            this.resetPoolManager(false);
        }
    }

    public int getCheckoutTimeout() {
        return this.wcpds.getCheckoutTimeout();
    }

    public void setCheckoutTimeout(int checkoutTimeout) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getCheckoutTimeout(), checkoutTimeout)) {
            this.wcpds.setCheckoutTimeout(checkoutTimeout);
            this.resetPoolManager(false);
        }
    }

    public int getAcquireIncrement() {
        return this.wcpds.getAcquireIncrement();
    }

    public void setAcquireIncrement(int acquireIncrement) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getAcquireIncrement(), acquireIncrement)) {
            this.wcpds.setAcquireIncrement(acquireIncrement);
            this.resetPoolManager(false);
        }
    }

    public int getAcquireRetryAttempts() {
        return this.wcpds.getAcquireRetryAttempts();
    }

    public void setAcquireRetryAttempts(int acquireRetryAttempts) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getAcquireRetryAttempts(), acquireRetryAttempts)) {
            this.wcpds.setAcquireRetryAttempts(acquireRetryAttempts);
            this.resetPoolManager(false);
        }
    }

    public int getAcquireRetryDelay() {
        return this.wcpds.getAcquireRetryDelay();
    }

    public void setAcquireRetryDelay(int acquireRetryDelay) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getAcquireRetryDelay(), acquireRetryDelay)) {
            this.wcpds.setAcquireRetryDelay(acquireRetryDelay);
            this.resetPoolManager(false);
        }
    }

    public boolean isAutoCommitOnClose() {
        return this.wcpds.isAutoCommitOnClose();
    }

    public void setAutoCommitOnClose(boolean autoCommitOnClose) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isAutoCommitOnClose(), autoCommitOnClose)) {
            this.wcpds.setAutoCommitOnClose(autoCommitOnClose);
            this.resetPoolManager(false);
        }
    }

    public String getContextClassLoaderSource() {
        return this.wcpds.getContextClassLoaderSource();
    }

    public void setContextClassLoaderSource(String contextClassLoaderSource) throws PropertyVetoException {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getContextClassLoaderSource(), contextClassLoaderSource)) {
            this.wcpds.setContextClassLoaderSource(contextClassLoaderSource);
            this.resetPoolManager(false);
        }
    }

    public String getConnectionTesterClassName() {
        return this.wcpds.getConnectionTesterClassName();
    }

    public void setConnectionTesterClassName(String connectionTesterClassName) throws PropertyVetoException {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getConnectionTesterClassName(), connectionTesterClassName)) {
            this.wcpds.setConnectionTesterClassName(connectionTesterClassName);
            this.resetPoolManager(false);
        }
    }

    public String getAutomaticTestTable() {
        return this.wcpds.getAutomaticTestTable();
    }

    public void setAutomaticTestTable(String automaticTestTable) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getAutomaticTestTable(), automaticTestTable)) {
            this.wcpds.setAutomaticTestTable(automaticTestTable);
            this.resetPoolManager(false);
        }
    }

    public boolean isForceIgnoreUnresolvedTransactions() {
        return this.wcpds.isForceIgnoreUnresolvedTransactions();
    }

    public void setForceIgnoreUnresolvedTransactions(boolean forceIgnoreUnresolvedTransactions) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isForceIgnoreUnresolvedTransactions(), forceIgnoreUnresolvedTransactions)) {
            this.wcpds.setForceIgnoreUnresolvedTransactions(forceIgnoreUnresolvedTransactions);
            this.resetPoolManager(false);
        }
    }

    public boolean isPrivilegeSpawnedThreads() {
        return this.wcpds.isPrivilegeSpawnedThreads();
    }

    public void setPrivilegeSpawnedThreads(boolean privilegeSpawnedThreads) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isPrivilegeSpawnedThreads(), privilegeSpawnedThreads)) {
            this.wcpds.setPrivilegeSpawnedThreads(privilegeSpawnedThreads);
            this.resetPoolManager(false);
        }
    }

    public int getIdleConnectionTestPeriod() {
        return this.wcpds.getIdleConnectionTestPeriod();
    }

    public void setIdleConnectionTestPeriod(int idleConnectionTestPeriod) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getIdleConnectionTestPeriod(), idleConnectionTestPeriod)) {
            this.wcpds.setIdleConnectionTestPeriod(idleConnectionTestPeriod);
            this.resetPoolManager(false);
        }
    }

    public int getInitialPoolSize() {
        return this.wcpds.getInitialPoolSize();
    }

    public void setInitialPoolSize(int initialPoolSize) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getInitialPoolSize(), initialPoolSize)) {
            this.wcpds.setInitialPoolSize(initialPoolSize);
            this.resetPoolManager(false);
        }
    }

    public int getMaxIdleTime() {
        return this.wcpds.getMaxIdleTime();
    }

    public void setMaxIdleTime(int maxIdleTime) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxIdleTime(), maxIdleTime)) {
            this.wcpds.setMaxIdleTime(maxIdleTime);
            this.resetPoolManager(false);
        }
    }

    public int getMaxPoolSize() {
        return this.wcpds.getMaxPoolSize();
    }

    public void setMaxPoolSize(int maxPoolSize) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxPoolSize(), maxPoolSize)) {
            this.wcpds.setMaxPoolSize(maxPoolSize);
            this.resetPoolManager(false);
        }
    }

    public int getMaxStatements() {
        return this.wcpds.getMaxStatements();
    }

    public void setMaxStatements(int maxStatements) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxStatements(), maxStatements)) {
            this.wcpds.setMaxStatements(maxStatements);
            this.resetPoolManager(false);
        }
    }

    public int getMaxStatementsPerConnection() {
        return this.wcpds.getMaxStatementsPerConnection();
    }

    public void setMaxStatementsPerConnection(int maxStatementsPerConnection) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxStatementsPerConnection(), maxStatementsPerConnection)) {
            this.wcpds.setMaxStatementsPerConnection(maxStatementsPerConnection);
            this.resetPoolManager(false);
        }
    }

    public int getMinPoolSize() {
        return this.wcpds.getMinPoolSize();
    }

    public void setMinPoolSize(int minPoolSize) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMinPoolSize(), minPoolSize)) {
            this.wcpds.setMinPoolSize(minPoolSize);
            this.resetPoolManager(false);
        }
    }

    public String getOverrideDefaultUser() {
        return this.wcpds.getOverrideDefaultUser();
    }

    public void setOverrideDefaultUser(String overrideDefaultUser) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getOverrideDefaultUser(), overrideDefaultUser)) {
            this.wcpds.setOverrideDefaultUser(overrideDefaultUser);
            this.resetPoolManager(false);
        }
    }

    public String getOverrideDefaultPassword() {
        return this.wcpds.getOverrideDefaultPassword();
    }

    public void setOverrideDefaultPassword(String overrideDefaultPassword) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getOverrideDefaultPassword(), overrideDefaultPassword)) {
            this.wcpds.setOverrideDefaultPassword(overrideDefaultPassword);
            this.resetPoolManager(false);
        }
    }

    public int getPropertyCycle() {
        return this.wcpds.getPropertyCycle();
    }

    public void setPropertyCycle(int propertyCycle) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getPropertyCycle(), propertyCycle)) {
            this.wcpds.setPropertyCycle(propertyCycle);
            this.resetPoolManager(false);
        }
    }

    public boolean isBreakAfterAcquireFailure() {
        return this.wcpds.isBreakAfterAcquireFailure();
    }

    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isBreakAfterAcquireFailure(), breakAfterAcquireFailure)) {
            this.wcpds.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
            this.resetPoolManager(false);
        }
    }

    public boolean isTestConnectionOnCheckout() {
        return this.wcpds.isTestConnectionOnCheckout();
    }

    public void setTestConnectionOnCheckout(boolean testConnectionOnCheckout) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isTestConnectionOnCheckout(), testConnectionOnCheckout)) {
            this.wcpds.setTestConnectionOnCheckout(testConnectionOnCheckout);
            this.resetPoolManager(false);
        }
    }

    public boolean isTestConnectionOnCheckin() {
        return this.wcpds.isTestConnectionOnCheckin();
    }

    public void setTestConnectionOnCheckin(boolean testConnectionOnCheckin) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isTestConnectionOnCheckin(), testConnectionOnCheckin)) {
            this.wcpds.setTestConnectionOnCheckin(testConnectionOnCheckin);
            this.resetPoolManager(false);
        }
    }

    public boolean isUsesTraditionalReflectiveProxies() {
        return this.wcpds.isUsesTraditionalReflectiveProxies();
    }

    public void setUsesTraditionalReflectiveProxies(boolean usesTraditionalReflectiveProxies) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isUsesTraditionalReflectiveProxies(), usesTraditionalReflectiveProxies)) {
            this.wcpds.setUsesTraditionalReflectiveProxies(usesTraditionalReflectiveProxies);
            this.resetPoolManager(false);
        }
    }

    public String getPreferredTestQuery() {
        return this.wcpds.getPreferredTestQuery();
    }

    public void setPreferredTestQuery(String preferredTestQuery) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getPreferredTestQuery(), preferredTestQuery)) {
            this.wcpds.setPreferredTestQuery(preferredTestQuery);
            this.resetPoolManager(false);
        }
    }

    public int getMaxAdministrativeTaskTime() {
        return this.wcpds.getMaxAdministrativeTaskTime();
    }

    public void setMaxAdministrativeTaskTime(int maxAdministrativeTaskTime) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxAdministrativeTaskTime(), maxAdministrativeTaskTime)) {
            this.wcpds.setMaxAdministrativeTaskTime(maxAdministrativeTaskTime);
            this.resetPoolManager(false);
        }
    }

    public int getMaxIdleTimeExcessConnections() {
        return this.wcpds.getMaxIdleTimeExcessConnections();
    }

    public void setMaxIdleTimeExcessConnections(int maxIdleTimeExcessConnections) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxIdleTimeExcessConnections(), maxIdleTimeExcessConnections)) {
            this.wcpds.setMaxIdleTimeExcessConnections(maxIdleTimeExcessConnections);
            this.resetPoolManager(false);
        }
    }

    public int getMaxConnectionAge() {
        return this.wcpds.getMaxConnectionAge();
    }

    public void setMaxConnectionAge(int maxConnectionAge) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getMaxConnectionAge(), maxConnectionAge)) {
            this.wcpds.setMaxConnectionAge(maxConnectionAge);
            this.resetPoolManager(false);
        }
    }

    public String getConnectionCustomizerClassName() {
        return this.wcpds.getConnectionCustomizerClassName();
    }

    public void setConnectionCustomizerClassName(String connectionCustomizerClassName) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getConnectionCustomizerClassName(), connectionCustomizerClassName)) {
            this.wcpds.setConnectionCustomizerClassName(connectionCustomizerClassName);
            this.resetPoolManager(false);
        }
    }

    public int getUnreturnedConnectionTimeout() {
        return this.wcpds.getUnreturnedConnectionTimeout();
    }

    public void setUnreturnedConnectionTimeout(int unreturnedConnectionTimeout) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getUnreturnedConnectionTimeout(), unreturnedConnectionTimeout)) {
            this.wcpds.setUnreturnedConnectionTimeout(unreturnedConnectionTimeout);
            this.resetPoolManager(false);
        }
    }

    public String getUserOverridesAsString() {
        return this.wcpds.getUserOverridesAsString();
    }

    public void setUserOverridesAsString(String uoas) throws PropertyVetoException {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getUserOverridesAsString(), uoas)) {
            this.wcpds.setUserOverridesAsString(uoas);
            this.resetPoolManager(false);
        }
    }

    public Map getUserOverrides() {
        return this.wcpds.getUserOverrides();
    }

    public boolean isDebugUnreturnedConnectionStackTraces() {
        return this.wcpds.isDebugUnreturnedConnectionStackTraces();
    }

    public void setDebugUnreturnedConnectionStackTraces(boolean debugUnreturnedConnectionStackTraces) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isDebugUnreturnedConnectionStackTraces(), debugUnreturnedConnectionStackTraces)) {
            this.wcpds.setDebugUnreturnedConnectionStackTraces(debugUnreturnedConnectionStackTraces);
            this.resetPoolManager(false);
        }
    }

    public boolean isForceSynchronousCheckins() {
        return this.wcpds.isForceSynchronousCheckins();
    }

    public void setForceSynchronousCheckins(boolean forceSynchronousCheckins) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.isForceSynchronousCheckins(), forceSynchronousCheckins)) {
            this.wcpds.setForceSynchronousCheckins(forceSynchronousCheckins);
            this.resetPoolManager(false);
        }
    }

    public int getStatementCacheNumDeferredCloseThreads() {
        return this.wcpds.getStatementCacheNumDeferredCloseThreads();
    }

    public void setStatementCacheNumDeferredCloseThreads(int statementCacheNumDeferredCloseThreads) {
        if (AbstractComboPooledDataSource.diff(this.wcpds.getStatementCacheNumDeferredCloseThreads(), statementCacheNumDeferredCloseThreads)) {
            this.wcpds.setStatementCacheNumDeferredCloseThreads(statementCacheNumDeferredCloseThreads);
            this.resetPoolManager(false);
        }
    }

    @Override
    public String getFactoryClassLocation() {
        return super.getFactoryClassLocation();
    }

    @Override
    public void setFactoryClassLocation(String factoryClassLocation) {
        if (AbstractComboPooledDataSource.diff(this.dmds.getFactoryClassLocation(), factoryClassLocation) || AbstractComboPooledDataSource.diff(this.wcpds.getFactoryClassLocation(), factoryClassLocation) || AbstractComboPooledDataSource.diff(super.getFactoryClassLocation(), factoryClassLocation)) {
            this.dmds.setFactoryClassLocation(factoryClassLocation);
            this.wcpds.setFactoryClassLocation(factoryClassLocation);
            super.setFactoryClassLocation(factoryClassLocation);
        }
    }

    @Override
    public String toString() {
        return this.toString(false);
    }

    @Override
    public String toString(boolean show_config) {
        if (show_config) {
            StringBuffer sb = new StringBuffer(512);
            sb.append(this.getClass().getName());
            sb.append(" [ ");
            try {
                BeansUtils.appendPropNamesAndValues((StringBuffer)sb, (Object)this, (Collection)TO_STRING_IGNORE_PROPS);
            }
            catch (Exception e) {
                sb.append(e.toString());
            }
            sb.append(" ]");
            return sb.toString();
        }
        return this.getClass().getName() + "[ identityToken -> " + this.getIdentityToken() + ", dataSourceName -> " + this.getDataSourceName() + " ]";
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.writeShort(1);
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        short version = ois.readShort();
        switch (version) {
            case 1: {
                this.updateLocalVarsFromCpdsProp();
                this.setUpPropertyEvents();
                break;
            }
            default: {
                throw new IOException("Unsupported Serialized Version: " + version);
            }
        }
    }

    private boolean isWrapperForDmds(Class<?> iface) {
        return iface.isAssignableFrom(this.dmds.getClass());
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return this.isWrapperForDmds(iface) || this.isWrapperForThis(iface);
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (this.isWrapperForDmds(iface)) {
            return this.dmds.unwrap(iface);
        }
        if (this.isWrapperForThis(iface)) {
            return (T)this;
        }
        throw new SQLException(this + " is not a wrapper for or implementation of " + iface.getName());
    }
}

