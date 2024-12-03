/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.jdbc.pool;

import java.util.Properties;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.Validator;

public interface PoolConfiguration {
    public static final String PKG_PREFIX = "org.apache.tomcat.jdbc.pool.interceptor.";

    public void setAbandonWhenPercentageFull(int var1);

    public int getAbandonWhenPercentageFull();

    public boolean isFairQueue();

    public void setFairQueue(boolean var1);

    public boolean isAccessToUnderlyingConnectionAllowed();

    public void setAccessToUnderlyingConnectionAllowed(boolean var1);

    public String getConnectionProperties();

    public void setConnectionProperties(String var1);

    public Properties getDbProperties();

    public void setDbProperties(Properties var1);

    public Boolean isDefaultAutoCommit();

    public Boolean getDefaultAutoCommit();

    public void setDefaultAutoCommit(Boolean var1);

    public String getDefaultCatalog();

    public void setDefaultCatalog(String var1);

    public Boolean isDefaultReadOnly();

    public Boolean getDefaultReadOnly();

    public void setDefaultReadOnly(Boolean var1);

    public int getDefaultTransactionIsolation();

    public void setDefaultTransactionIsolation(int var1);

    public String getDriverClassName();

    public void setDriverClassName(String var1);

    public int getInitialSize();

    public void setInitialSize(int var1);

    public boolean isLogAbandoned();

    public void setLogAbandoned(boolean var1);

    public int getMaxActive();

    public void setMaxActive(int var1);

    public int getMaxIdle();

    public void setMaxIdle(int var1);

    public int getMaxWait();

    public void setMaxWait(int var1);

    public int getMinEvictableIdleTimeMillis();

    public void setMinEvictableIdleTimeMillis(int var1);

    public int getMinIdle();

    public void setMinIdle(int var1);

    public String getName();

    public void setName(String var1);

    public int getNumTestsPerEvictionRun();

    public void setNumTestsPerEvictionRun(int var1);

    public String getPassword();

    public void setPassword(String var1);

    public String getPoolName();

    public String getUsername();

    public void setUsername(String var1);

    public boolean isRemoveAbandoned();

    public void setRemoveAbandoned(boolean var1);

    public void setRemoveAbandonedTimeout(int var1);

    public int getRemoveAbandonedTimeout();

    public boolean isTestOnBorrow();

    public void setTestOnBorrow(boolean var1);

    public boolean isTestOnReturn();

    public void setTestOnReturn(boolean var1);

    public boolean isTestWhileIdle();

    public void setTestWhileIdle(boolean var1);

    public int getTimeBetweenEvictionRunsMillis();

    public void setTimeBetweenEvictionRunsMillis(int var1);

    public String getUrl();

    public void setUrl(String var1);

    public String getValidationQuery();

    public void setValidationQuery(String var1);

    public int getValidationQueryTimeout();

    public void setValidationQueryTimeout(int var1);

    public String getValidatorClassName();

    public void setValidatorClassName(String var1);

    public Validator getValidator();

    public void setValidator(Validator var1);

    public long getValidationInterval();

    public void setValidationInterval(long var1);

    public String getInitSQL();

    public void setInitSQL(String var1);

    public boolean isTestOnConnect();

    public void setTestOnConnect(boolean var1);

    public String getJdbcInterceptors();

    public void setJdbcInterceptors(String var1);

    public PoolProperties.InterceptorDefinition[] getJdbcInterceptorsAsArray();

    public boolean isJmxEnabled();

    public void setJmxEnabled(boolean var1);

    public boolean isPoolSweeperEnabled();

    public boolean isUseEquals();

    public void setUseEquals(boolean var1);

    public long getMaxAge();

    public void setMaxAge(long var1);

    public boolean getUseLock();

    public void setUseLock(boolean var1);

    public void setSuspectTimeout(int var1);

    public int getSuspectTimeout();

    public void setDataSource(Object var1);

    public Object getDataSource();

    public void setDataSourceJNDI(String var1);

    public String getDataSourceJNDI();

    public boolean isAlternateUsernameAllowed();

    public void setAlternateUsernameAllowed(boolean var1);

    public void setCommitOnReturn(boolean var1);

    public boolean getCommitOnReturn();

    public void setRollbackOnReturn(boolean var1);

    public boolean getRollbackOnReturn();

    public void setUseDisposableConnectionFacade(boolean var1);

    public boolean getUseDisposableConnectionFacade();

    public void setLogValidationErrors(boolean var1);

    public boolean getLogValidationErrors();

    public boolean getPropagateInterruptState();

    public void setPropagateInterruptState(boolean var1);

    public void setIgnoreExceptionOnPreLoad(boolean var1);

    public boolean isIgnoreExceptionOnPreLoad();

    public void setUseStatementFacade(boolean var1);

    public boolean getUseStatementFacade();
}

