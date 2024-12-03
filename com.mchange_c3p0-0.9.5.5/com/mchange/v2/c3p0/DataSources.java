/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mchange.v2.beans.BeansUtils
 *  com.mchange.v2.log.MLevel
 *  com.mchange.v2.log.MLog
 *  com.mchange.v2.log.MLogger
 *  com.mchange.v2.sql.SqlUtils
 */
package com.mchange.v2.c3p0;

import com.mchange.v2.beans.BeansUtils;
import com.mchange.v2.c3p0.DriverManagerDataSource;
import com.mchange.v2.c3p0.PoolBackedDataSource;
import com.mchange.v2.c3p0.PoolConfig;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.WrapperConnectionPoolDataSource;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.MLogger;
import com.mchange.v2.sql.SqlUtils;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;

public final class DataSources {
    static final MLogger logger = MLog.getLogger(DataSources.class);
    static final Set WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS;
    static final Set POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS;

    public static DataSource unpooledDataSource() throws SQLException {
        DriverManagerDataSource out = new DriverManagerDataSource();
        return out;
    }

    public static DataSource unpooledDataSource(String jdbcUrl) throws SQLException {
        DriverManagerDataSource out = new DriverManagerDataSource();
        out.setJdbcUrl(jdbcUrl);
        return out;
    }

    public static DataSource unpooledDataSource(String jdbcUrl, String user, String password) throws SQLException {
        Properties props = new Properties();
        props.put("user", user);
        props.put("password", password);
        return DataSources.unpooledDataSource(jdbcUrl, props);
    }

    public static DataSource unpooledDataSource(String jdbcUrl, Properties driverProps) throws SQLException {
        DriverManagerDataSource out = new DriverManagerDataSource();
        out.setJdbcUrl(jdbcUrl);
        out.setProperties(driverProps);
        return out;
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource) throws SQLException {
        return DataSources.pooledDataSource(unpooledDataSource, null, null);
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, int statement_cache_size) throws SQLException {
        HashMap<String, Integer> overrideProps = new HashMap<String, Integer>();
        overrideProps.put("maxStatements", new Integer(statement_cache_size));
        return DataSources.pooledDataSource(unpooledDataSource, null, overrideProps);
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, PoolConfig pcfg) throws SQLException {
        try {
            WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource();
            wcpds.setNestedDataSource(unpooledDataSource);
            BeansUtils.overwriteSpecificAccessibleProperties((Object)pcfg, (Object)wcpds, (Collection)WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS);
            PoolBackedDataSource nascent_pbds = new PoolBackedDataSource();
            nascent_pbds.setConnectionPoolDataSource(wcpds);
            BeansUtils.overwriteSpecificAccessibleProperties((Object)pcfg, (Object)nascent_pbds, (Collection)POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS);
            return nascent_pbds;
        }
        catch (Exception e) {
            SQLException sqle = SqlUtils.toSQLException((String)("Exception configuring pool-backed DataSource: " + e), (Throwable)e);
            if (logger.isLoggable(MLevel.FINE) && e != sqle) {
                logger.log(MLevel.FINE, "Converted exception to throwable SQLException", (Throwable)e);
            }
            throw sqle;
        }
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, String configName) throws SQLException {
        return DataSources.pooledDataSource(unpooledDataSource, configName, null);
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, Map overrideProps) throws SQLException {
        return DataSources.pooledDataSource(unpooledDataSource, null, overrideProps);
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, String configName, Map overrideProps) throws SQLException {
        try {
            WrapperConnectionPoolDataSource wcpds = new WrapperConnectionPoolDataSource(configName);
            wcpds.setNestedDataSource(unpooledDataSource);
            if (overrideProps != null) {
                BeansUtils.overwriteAccessiblePropertiesFromMap((Map)overrideProps, (Object)wcpds, (boolean)false, null, (boolean)true, (MLevel)MLevel.WARNING, (MLevel)MLevel.WARNING, (boolean)false);
            }
            PoolBackedDataSource nascent_pbds = new PoolBackedDataSource(configName);
            nascent_pbds.setConnectionPoolDataSource(wcpds);
            if (overrideProps != null) {
                BeansUtils.overwriteAccessiblePropertiesFromMap((Map)overrideProps, (Object)nascent_pbds, (boolean)false, null, (boolean)true, (MLevel)MLevel.WARNING, (MLevel)MLevel.WARNING, (boolean)false);
            }
            return nascent_pbds;
        }
        catch (Exception e) {
            SQLException sqle = SqlUtils.toSQLException((String)("Exception configuring pool-backed DataSource: " + e), (Throwable)e);
            if (logger.isLoggable(MLevel.FINE) && e != sqle) {
                logger.log(MLevel.FINE, "Converted exception to throwable SQLException", (Throwable)e);
            }
            throw sqle;
        }
    }

    public static DataSource pooledDataSource(DataSource unpooledDataSource, Properties props) throws SQLException {
        Properties peeledProps = new Properties();
        Enumeration<?> e = props.propertyNames();
        while (e.hasMoreElements()) {
            String propKey = (String)e.nextElement();
            String propVal = props.getProperty(propKey);
            String peeledKey = propKey.startsWith("c3p0.") ? propKey.substring(5) : propKey;
            peeledProps.put(peeledKey, propVal);
        }
        return DataSources.pooledDataSource(unpooledDataSource, null, peeledProps);
    }

    public static void destroy(DataSource pooledDataSource) throws SQLException {
        DataSources.destroy(pooledDataSource, false);
    }

    public static void forceDestroy(DataSource pooledDataSource) throws SQLException {
        DataSources.destroy(pooledDataSource, true);
    }

    private static void destroy(DataSource pooledDataSource, boolean force) throws SQLException {
        ConnectionPoolDataSource cpds;
        if (pooledDataSource instanceof PoolBackedDataSource && (cpds = ((PoolBackedDataSource)pooledDataSource).getConnectionPoolDataSource()) instanceof WrapperConnectionPoolDataSource) {
            DataSources.destroy(((WrapperConnectionPoolDataSource)cpds).getNestedDataSource(), force);
        }
        if (pooledDataSource instanceof PooledDataSource) {
            ((PooledDataSource)pooledDataSource).close(force);
        }
    }

    private DataSources() {
    }

    static {
        String[] props = new String[]{"checkoutTimeout", "acquireIncrement", "acquireRetryAttempts", "acquireRetryDelay", "autoCommitOnClose", "connectionTesterClassName", "forceIgnoreUnresolvedTransactions", "idleConnectionTestPeriod", "initialPoolSize", "maxIdleTime", "maxPoolSize", "maxStatements", "maxStatementsPerConnection", "minPoolSize", "propertyCycle", "breakAfterAcquireFailure", "testConnectionOnCheckout", "testConnectionOnCheckin", "usesTraditionalReflectiveProxies", "preferredTestQuery", "automaticTestTable", "factoryClassLocation"};
        WRAPPER_CXN_POOL_DATA_SOURCE_OVERWRITE_PROPS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(props)));
        props = new String[]{"numHelperThreads", "factoryClassLocation"};
        POOL_BACKED_DATA_SOURCE_OVERWRITE_PROPS = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(props)));
    }
}

