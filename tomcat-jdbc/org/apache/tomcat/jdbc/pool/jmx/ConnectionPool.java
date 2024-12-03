/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool.jmx;

import java.util.Properties;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.PoolUtilities;
import org.apache.tomcat.jdbc.pool.Validator;
import org.apache.tomcat.jdbc.pool.jmx.ConnectionPoolMBean;

public class ConnectionPool
extends NotificationBroadcasterSupport
implements ConnectionPoolMBean,
MBeanRegistration {
    private static final Log log = LogFactory.getLog(ConnectionPool.class);
    protected org.apache.tomcat.jdbc.pool.ConnectionPool pool = null;
    protected AtomicInteger sequence = new AtomicInteger(0);
    protected ConcurrentLinkedQueue<NotificationListener> listeners = new ConcurrentLinkedQueue();
    private ObjectName oname = null;
    public static final String NOTIFY_INIT = "INIT FAILED";
    public static final String NOTIFY_CONNECT = "CONNECTION FAILED";
    public static final String NOTIFY_ABANDON = "CONNECTION ABANDONED";
    public static final String SLOW_QUERY_NOTIFICATION = "SLOW QUERY";
    public static final String FAILED_QUERY_NOTIFICATION = "FAILED QUERY";
    public static final String SUSPECT_ABANDONED_NOTIFICATION = "SUSPECT CONNECTION ABANDONED";
    public static final String POOL_EMPTY = "POOL EMPTY";
    public static final String SUSPECT_RETURNED_NOTIFICATION = "SUSPECT CONNECTION RETURNED";

    public ConnectionPool(org.apache.tomcat.jdbc.pool.ConnectionPool pool) {
        this.pool = pool;
    }

    public org.apache.tomcat.jdbc.pool.ConnectionPool getPool() {
        return this.pool;
    }

    public PoolConfiguration getPoolProperties() {
        return this.pool.getPoolProperties();
    }

    public ObjectName getObjectName() {
        return this.oname;
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        this.oname = name;
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
    }

    @Override
    public MBeanNotificationInfo[] getNotificationInfo() {
        MBeanNotificationInfo[] pres = super.getNotificationInfo();
        MBeanNotificationInfo[] loc = ConnectionPool.getDefaultNotificationInfo();
        MBeanNotificationInfo[] aug = new MBeanNotificationInfo[pres.length + loc.length];
        if (pres.length > 0) {
            System.arraycopy(pres, 0, aug, 0, pres.length);
        }
        if (loc.length > 0) {
            System.arraycopy(loc, 0, aug, pres.length, loc.length);
        }
        return aug;
    }

    public static MBeanNotificationInfo[] getDefaultNotificationInfo() {
        String[] types = new String[]{NOTIFY_INIT, NOTIFY_CONNECT, NOTIFY_ABANDON, SLOW_QUERY_NOTIFICATION, FAILED_QUERY_NOTIFICATION, SUSPECT_ABANDONED_NOTIFICATION, POOL_EMPTY, SUSPECT_RETURNED_NOTIFICATION};
        String name = Notification.class.getName();
        String description = "A connection pool error condition was met.";
        MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
        return new MBeanNotificationInfo[]{info};
    }

    public boolean notify(String type, String message) {
        try {
            Notification n = new Notification(type, this, this.sequence.incrementAndGet(), System.currentTimeMillis(), "[" + type + "] " + message);
            this.sendNotification(n);
            for (NotificationListener listener : this.listeners) {
                listener.handleNotification(n, this);
            }
            return true;
        }
        catch (Exception x) {
            if (log.isDebugEnabled()) {
                log.debug((Object)("Notify failed. Type=" + type + "; Message=" + message), (Throwable)x);
            }
            return false;
        }
    }

    public void addListener(NotificationListener list) {
        this.listeners.add(list);
    }

    public boolean removeListener(NotificationListener list) {
        return this.listeners.remove(list);
    }

    @Override
    public int getSize() {
        return this.pool.getSize();
    }

    @Override
    public int getIdle() {
        return this.pool.getIdle();
    }

    @Override
    public int getActive() {
        return this.pool.getActive();
    }

    @Override
    public int getNumIdle() {
        return this.getIdle();
    }

    @Override
    public int getNumActive() {
        return this.getActive();
    }

    @Override
    public int getWaitCount() {
        return this.pool.getWaitCount();
    }

    @Override
    public long getBorrowedCount() {
        return this.pool.getBorrowedCount();
    }

    @Override
    public long getReturnedCount() {
        return this.pool.getReturnedCount();
    }

    @Override
    public long getCreatedCount() {
        return this.pool.getCreatedCount();
    }

    @Override
    public long getReleasedCount() {
        return this.pool.getReleasedCount();
    }

    @Override
    public long getReconnectedCount() {
        return this.pool.getReconnectedCount();
    }

    @Override
    public long getRemoveAbandonedCount() {
        return this.pool.getRemoveAbandonedCount();
    }

    @Override
    public long getReleasedIdleCount() {
        return this.pool.getReleasedIdleCount();
    }

    @Override
    public void checkIdle() {
        this.pool.checkIdle();
    }

    @Override
    public void checkAbandoned() {
        this.pool.checkAbandoned();
    }

    @Override
    public void testIdle() {
        this.pool.testAllIdle();
    }

    @Override
    public void resetStats() {
        this.pool.resetStats();
    }

    @Override
    public String getConnectionProperties() {
        return this.getPoolProperties().getConnectionProperties();
    }

    @Override
    public Properties getDbProperties() {
        return PoolUtilities.cloneWithoutPassword(this.getPoolProperties().getDbProperties());
    }

    @Override
    public String getDefaultCatalog() {
        return this.getPoolProperties().getDefaultCatalog();
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return this.getPoolProperties().getDefaultTransactionIsolation();
    }

    @Override
    public String getDriverClassName() {
        return this.getPoolProperties().getDriverClassName();
    }

    @Override
    public int getInitialSize() {
        return this.getPoolProperties().getInitialSize();
    }

    @Override
    public String getInitSQL() {
        return this.getPoolProperties().getInitSQL();
    }

    @Override
    public String getJdbcInterceptors() {
        return this.getPoolProperties().getJdbcInterceptors();
    }

    @Override
    public int getMaxActive() {
        return this.getPoolProperties().getMaxActive();
    }

    @Override
    public int getMaxIdle() {
        return this.getPoolProperties().getMaxIdle();
    }

    @Override
    public int getMaxWait() {
        return this.getPoolProperties().getMaxWait();
    }

    @Override
    public int getMinEvictableIdleTimeMillis() {
        return this.getPoolProperties().getMinEvictableIdleTimeMillis();
    }

    @Override
    public int getMinIdle() {
        return this.getPoolProperties().getMinIdle();
    }

    @Override
    public long getMaxAge() {
        return this.getPoolProperties().getMaxAge();
    }

    @Override
    public String getName() {
        return this.getPoolName();
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return this.getPoolProperties().getNumTestsPerEvictionRun();
    }

    @Override
    public String getPassword() {
        return "Password not available as DataSource/JMX operation.";
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        return this.getPoolProperties().getRemoveAbandonedTimeout();
    }

    @Override
    public int getTimeBetweenEvictionRunsMillis() {
        return this.getPoolProperties().getTimeBetweenEvictionRunsMillis();
    }

    @Override
    public String getUrl() {
        return this.getPoolProperties().getUrl();
    }

    @Override
    public String getUsername() {
        return this.getPoolProperties().getUsername();
    }

    @Override
    public long getValidationInterval() {
        return this.getPoolProperties().getValidationInterval();
    }

    @Override
    public String getValidationQuery() {
        return this.getPoolProperties().getValidationQuery();
    }

    @Override
    public int getValidationQueryTimeout() {
        return this.getPoolProperties().getValidationQueryTimeout();
    }

    @Override
    public String getValidatorClassName() {
        return this.getPoolProperties().getValidatorClassName();
    }

    @Override
    public Validator getValidator() {
        return this.getPoolProperties().getValidator();
    }

    @Override
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.getPoolProperties().isAccessToUnderlyingConnectionAllowed();
    }

    @Override
    public Boolean isDefaultAutoCommit() {
        return this.getPoolProperties().isDefaultAutoCommit();
    }

    @Override
    public Boolean isDefaultReadOnly() {
        return this.getPoolProperties().isDefaultReadOnly();
    }

    @Override
    public boolean isLogAbandoned() {
        return this.getPoolProperties().isLogAbandoned();
    }

    @Override
    public boolean isPoolSweeperEnabled() {
        return this.getPoolProperties().isPoolSweeperEnabled();
    }

    @Override
    public boolean isRemoveAbandoned() {
        return this.getPoolProperties().isRemoveAbandoned();
    }

    @Override
    public int getAbandonWhenPercentageFull() {
        return this.getPoolProperties().getAbandonWhenPercentageFull();
    }

    @Override
    public boolean isTestOnBorrow() {
        return this.getPoolProperties().isTestOnBorrow();
    }

    @Override
    public boolean isTestOnConnect() {
        return this.getPoolProperties().isTestOnConnect();
    }

    @Override
    public boolean isTestOnReturn() {
        return this.getPoolProperties().isTestOnReturn();
    }

    @Override
    public boolean isTestWhileIdle() {
        return this.getPoolProperties().isTestWhileIdle();
    }

    @Override
    public Boolean getDefaultAutoCommit() {
        return this.getPoolProperties().getDefaultAutoCommit();
    }

    @Override
    public Boolean getDefaultReadOnly() {
        return this.getPoolProperties().getDefaultReadOnly();
    }

    @Override
    public PoolProperties.InterceptorDefinition[] getJdbcInterceptorsAsArray() {
        return this.getPoolProperties().getJdbcInterceptorsAsArray();
    }

    @Override
    public boolean getUseLock() {
        return this.getPoolProperties().getUseLock();
    }

    @Override
    public boolean isFairQueue() {
        return this.getPoolProperties().isFairQueue();
    }

    @Override
    public boolean isJmxEnabled() {
        return this.getPoolProperties().isJmxEnabled();
    }

    @Override
    public boolean isUseEquals() {
        return this.getPoolProperties().isUseEquals();
    }

    @Override
    public void setAbandonWhenPercentageFull(int percentage) {
        this.getPoolProperties().setAbandonWhenPercentageFull(percentage);
    }

    @Override
    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.getPoolProperties().setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
    }

    @Override
    public void setDbProperties(Properties dbProperties) {
        this.getPoolProperties().setDbProperties(dbProperties);
    }

    @Override
    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.getPoolProperties().setDefaultReadOnly(defaultReadOnly);
    }

    @Override
    public void setMaxAge(long maxAge) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setMaxAge(maxAge);
        this.pool.checkPoolConfiguration(this.getPoolProperties());
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    @Override
    public void setName(String name) {
        this.getPoolProperties().setName(name);
    }

    @Override
    public String getPoolName() {
        return this.getPoolProperties().getName();
    }

    @Override
    public void setConnectionProperties(String connectionProperties) {
        this.getPoolProperties().setConnectionProperties(connectionProperties);
    }

    @Override
    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.getPoolProperties().setDefaultAutoCommit(defaultAutoCommit);
    }

    @Override
    public void setDefaultCatalog(String defaultCatalog) {
        this.getPoolProperties().setDefaultCatalog(defaultCatalog);
    }

    @Override
    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.getPoolProperties().setDefaultTransactionIsolation(defaultTransactionIsolation);
    }

    @Override
    public void setDriverClassName(String driverClassName) {
        this.getPoolProperties().setDriverClassName(driverClassName);
    }

    @Override
    public void setFairQueue(boolean fairQueue) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInitialSize(int initialSize) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInitSQL(String initSQL) {
        this.getPoolProperties().setInitSQL(initSQL);
    }

    @Override
    public void setJdbcInterceptors(String jdbcInterceptors) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setJmxEnabled(boolean jmxEnabled) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        this.getPoolProperties().setLogAbandoned(logAbandoned);
    }

    @Override
    public void setMaxActive(int maxActive) {
        this.getPoolProperties().setMaxActive(maxActive);
        this.pool.checkPoolConfiguration(this.getPoolProperties());
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.getPoolProperties().setMaxIdle(maxIdle);
        this.pool.checkPoolConfiguration(this.getPoolProperties());
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.getPoolProperties().setMaxWait(maxWait);
    }

    @Override
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.getPoolProperties().setMinIdle(minIdle);
        this.pool.checkPoolConfiguration(this.getPoolProperties());
    }

    @Override
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.getPoolProperties().setNumTestsPerEvictionRun(numTestsPerEvictionRun);
    }

    @Override
    public void setPassword(String password) {
        this.getPoolProperties().setPassword(password);
    }

    @Override
    public void setRemoveAbandoned(boolean removeAbandoned) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setRemoveAbandoned(removeAbandoned);
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    @Override
    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setRemoveAbandonedTimeout(removeAbandonedTimeout);
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.getPoolProperties().setTestOnBorrow(testOnBorrow);
    }

    @Override
    public void setTestOnConnect(boolean testOnConnect) {
        this.getPoolProperties().setTestOnConnect(testOnConnect);
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        this.getPoolProperties().setTestOnReturn(testOnReturn);
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setTestWhileIdle(testWhileIdle);
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    @Override
    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        boolean wasEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        this.getPoolProperties().setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        this.pool.checkPoolConfiguration(this.getPoolProperties());
        this.poolCleanerAttributeUpdated(wasEnabled);
    }

    private void poolCleanerAttributeUpdated(boolean wasEnabled) {
        boolean shouldBeEnabled = this.getPoolProperties().isPoolSweeperEnabled();
        if (!wasEnabled && shouldBeEnabled) {
            this.pool.initializePoolCleaner(this.getPoolProperties());
        } else if (wasEnabled) {
            this.pool.terminatePoolCleaner();
            if (shouldBeEnabled) {
                this.pool.initializePoolCleaner(this.getPoolProperties());
            }
        }
    }

    @Override
    public void setUrl(String url) {
        this.getPoolProperties().setUrl(url);
    }

    @Override
    public void setUseEquals(boolean useEquals) {
        this.getPoolProperties().setUseEquals(useEquals);
    }

    @Override
    public void setUseLock(boolean useLock) {
        this.getPoolProperties().setUseLock(useLock);
    }

    @Override
    public void setUsername(String username) {
        this.getPoolProperties().setUsername(username);
    }

    @Override
    public void setValidationInterval(long validationInterval) {
        this.getPoolProperties().setValidationInterval(validationInterval);
    }

    @Override
    public void setValidationQuery(String validationQuery) {
        this.getPoolProperties().setValidationQuery(validationQuery);
    }

    @Override
    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.getPoolProperties().setValidationQueryTimeout(validationQueryTimeout);
    }

    @Override
    public void setValidatorClassName(String className) {
        this.getPoolProperties().setValidatorClassName(className);
    }

    @Override
    public int getSuspectTimeout() {
        return this.getPoolProperties().getSuspectTimeout();
    }

    @Override
    public void setSuspectTimeout(int seconds) {
        this.getPoolProperties().setSuspectTimeout(seconds);
    }

    @Override
    public void setDataSource(Object ds) {
        this.getPoolProperties().setDataSource(ds);
    }

    @Override
    public Object getDataSource() {
        return this.getPoolProperties().getDataSource();
    }

    @Override
    public void setDataSourceJNDI(String jndiDS) {
        this.getPoolProperties().setDataSourceJNDI(jndiDS);
    }

    @Override
    public String getDataSourceJNDI() {
        return this.getPoolProperties().getDataSourceJNDI();
    }

    @Override
    public boolean isAlternateUsernameAllowed() {
        return this.getPoolProperties().isAlternateUsernameAllowed();
    }

    @Override
    public void setAlternateUsernameAllowed(boolean alternateUsernameAllowed) {
        this.getPoolProperties().setAlternateUsernameAllowed(alternateUsernameAllowed);
    }

    @Override
    public void setValidator(Validator validator) {
        this.getPoolProperties().setValidator(validator);
    }

    @Override
    public void setCommitOnReturn(boolean commitOnReturn) {
        this.getPoolProperties().setCommitOnReturn(commitOnReturn);
    }

    @Override
    public boolean getCommitOnReturn() {
        return this.getPoolProperties().getCommitOnReturn();
    }

    @Override
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.getPoolProperties().setRollbackOnReturn(rollbackOnReturn);
    }

    @Override
    public boolean getRollbackOnReturn() {
        return this.getPoolProperties().getRollbackOnReturn();
    }

    @Override
    public void setUseDisposableConnectionFacade(boolean useDisposableConnectionFacade) {
        this.getPoolProperties().setUseDisposableConnectionFacade(useDisposableConnectionFacade);
    }

    @Override
    public boolean getUseDisposableConnectionFacade() {
        return this.getPoolProperties().getUseDisposableConnectionFacade();
    }

    @Override
    public void setLogValidationErrors(boolean logValidationErrors) {
        this.getPoolProperties().setLogValidationErrors(logValidationErrors);
    }

    @Override
    public boolean getLogValidationErrors() {
        return this.getPoolProperties().getLogValidationErrors();
    }

    @Override
    public boolean getPropagateInterruptState() {
        return this.getPoolProperties().getPropagateInterruptState();
    }

    @Override
    public void setPropagateInterruptState(boolean propagateInterruptState) {
        this.getPoolProperties().setPropagateInterruptState(propagateInterruptState);
    }

    @Override
    public boolean isIgnoreExceptionOnPreLoad() {
        return this.getPoolProperties().isIgnoreExceptionOnPreLoad();
    }

    @Override
    public void setIgnoreExceptionOnPreLoad(boolean ignoreExceptionOnPreLoad) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean getUseStatementFacade() {
        return this.getPoolProperties().getUseStatementFacade();
    }

    @Override
    public void setUseStatementFacade(boolean useStatementFacade) {
        this.getPoolProperties().setUseStatementFacade(useStatementFacade);
    }

    @Override
    public void purge() {
        this.pool.purge();
    }

    @Override
    public void purgeOnReturn() {
        this.pool.purgeOnReturn();
    }
}

