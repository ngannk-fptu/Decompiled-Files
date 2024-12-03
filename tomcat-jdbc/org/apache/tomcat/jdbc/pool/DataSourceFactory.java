/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.util.Hashtable;
import java.util.Properties;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.PoolProperties;
import org.apache.tomcat.jdbc.pool.XADataSource;

public class DataSourceFactory
implements ObjectFactory {
    private static final Log log = LogFactory.getLog(DataSourceFactory.class);
    protected static final String PROP_DEFAULTAUTOCOMMIT = "defaultAutoCommit";
    protected static final String PROP_DEFAULTREADONLY = "defaultReadOnly";
    protected static final String PROP_DEFAULTTRANSACTIONISOLATION = "defaultTransactionIsolation";
    protected static final String PROP_DEFAULTCATALOG = "defaultCatalog";
    protected static final String PROP_DRIVERCLASSNAME = "driverClassName";
    protected static final String PROP_PASSWORD = "password";
    protected static final String PROP_URL = "url";
    protected static final String PROP_USERNAME = "username";
    protected static final String PROP_MAXACTIVE = "maxActive";
    protected static final String PROP_MAXIDLE = "maxIdle";
    protected static final String PROP_MINIDLE = "minIdle";
    protected static final String PROP_INITIALSIZE = "initialSize";
    protected static final String PROP_MAXWAIT = "maxWait";
    protected static final String PROP_MAXAGE = "maxAge";
    protected static final String PROP_TESTONBORROW = "testOnBorrow";
    protected static final String PROP_TESTONRETURN = "testOnReturn";
    protected static final String PROP_TESTWHILEIDLE = "testWhileIdle";
    protected static final String PROP_TESTONCONNECT = "testOnConnect";
    protected static final String PROP_VALIDATIONQUERY = "validationQuery";
    protected static final String PROP_VALIDATIONQUERY_TIMEOUT = "validationQueryTimeout";
    protected static final String PROP_VALIDATOR_CLASS_NAME = "validatorClassName";
    protected static final String PROP_NUMTESTSPEREVICTIONRUN = "numTestsPerEvictionRun";
    protected static final String PROP_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    protected static final String PROP_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    protected static final String PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED = "accessToUnderlyingConnectionAllowed";
    protected static final String PROP_REMOVEABANDONED = "removeAbandoned";
    protected static final String PROP_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    protected static final String PROP_LOGABANDONED = "logAbandoned";
    protected static final String PROP_ABANDONWHENPERCENTAGEFULL = "abandonWhenPercentageFull";
    protected static final String PROP_POOLPREPAREDSTATEMENTS = "poolPreparedStatements";
    protected static final String PROP_MAXOPENPREPAREDSTATEMENTS = "maxOpenPreparedStatements";
    protected static final String PROP_CONNECTIONPROPERTIES = "connectionProperties";
    protected static final String PROP_INITSQL = "initSQL";
    protected static final String PROP_INTERCEPTORS = "jdbcInterceptors";
    protected static final String PROP_VALIDATIONINTERVAL = "validationInterval";
    protected static final String PROP_JMX_ENABLED = "jmxEnabled";
    protected static final String PROP_FAIR_QUEUE = "fairQueue";
    protected static final String PROP_USE_EQUALS = "useEquals";
    protected static final String PROP_USE_CON_LOCK = "useLock";
    protected static final String PROP_DATASOURCE = "dataSource";
    protected static final String PROP_DATASOURCE_JNDI = "dataSourceJNDI";
    protected static final String PROP_SUSPECT_TIMEOUT = "suspectTimeout";
    protected static final String PROP_ALTERNATE_USERNAME_ALLOWED = "alternateUsernameAllowed";
    protected static final String PROP_COMMITONRETURN = "commitOnReturn";
    protected static final String PROP_ROLLBACKONRETURN = "rollbackOnReturn";
    protected static final String PROP_USEDISPOSABLECONNECTIONFACADE = "useDisposableConnectionFacade";
    protected static final String PROP_LOGVALIDATIONERRORS = "logValidationErrors";
    protected static final String PROP_PROPAGATEINTERRUPTSTATE = "propagateInterruptState";
    protected static final String PROP_IGNOREEXCEPTIONONPRELOAD = "ignoreExceptionOnPreLoad";
    protected static final String PROP_USESTATEMENTFACADE = "useStatementFacade";
    public static final int UNKNOWN_TRANSACTIONISOLATION = -1;
    public static final String OBJECT_NAME = "object_name";
    protected static final String[] ALL_PROPERTIES = new String[]{"defaultAutoCommit", "defaultReadOnly", "defaultTransactionIsolation", "defaultCatalog", "driverClassName", "maxActive", "maxIdle", "minIdle", "initialSize", "maxWait", "testOnBorrow", "testOnReturn", "timeBetweenEvictionRunsMillis", "numTestsPerEvictionRun", "minEvictableIdleTimeMillis", "testWhileIdle", "testOnConnect", "password", "url", "username", "validationQuery", "validationQueryTimeout", "validatorClassName", "validationInterval", "accessToUnderlyingConnectionAllowed", "removeAbandoned", "removeAbandonedTimeout", "logAbandoned", "poolPreparedStatements", "maxOpenPreparedStatements", "connectionProperties", "initSQL", "jdbcInterceptors", "jmxEnabled", "fairQueue", "useEquals", "object_name", "abandonWhenPercentageFull", "maxAge", "useLock", "dataSource", "dataSourceJNDI", "suspectTimeout", "alternateUsernameAllowed", "commitOnReturn", "rollbackOnReturn", "useDisposableConnectionFacade", "logValidationErrors", "propagateInterruptState", "ignoreExceptionOnPreLoad", "useStatementFacade"};

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference)obj;
        boolean XA = false;
        boolean ok = false;
        if ("javax.sql.DataSource".equals(ref.getClassName())) {
            ok = true;
        }
        if ("javax.sql.XADataSource".equals(ref.getClassName())) {
            ok = true;
            XA = true;
        }
        if (DataSource.class.getName().equals(ref.getClassName())) {
            ok = true;
        }
        if (!ok) {
            log.warn((Object)(ref.getClassName() + " is not a valid class name/type for this JNDI factory."));
            return null;
        }
        Properties properties = new Properties();
        for (int i = 0; i < ALL_PROPERTIES.length; ++i) {
            String propertyName = ALL_PROPERTIES[i];
            RefAddr ra = ref.get(propertyName);
            if (ra == null) continue;
            String propertyValue = ra.getContent().toString();
            properties.setProperty(propertyName, propertyValue);
        }
        return this.createDataSource(properties, nameCtx, XA);
    }

    public static PoolConfiguration parsePoolProperties(Properties properties) {
        PoolProperties poolProperties = new PoolProperties();
        String value = null;
        value = properties.getProperty(PROP_DEFAULTAUTOCOMMIT);
        if (value != null) {
            poolProperties.setDefaultAutoCommit(Boolean.valueOf(value));
        }
        if ((value = properties.getProperty(PROP_DEFAULTREADONLY)) != null) {
            poolProperties.setDefaultReadOnly(Boolean.valueOf(value));
        }
        if ((value = properties.getProperty(PROP_DEFAULTTRANSACTIONISOLATION)) != null) {
            int level = -1;
            if ("NONE".equalsIgnoreCase(value)) {
                level = 0;
            } else if ("READ_COMMITTED".equalsIgnoreCase(value)) {
                level = 2;
            } else if ("READ_UNCOMMITTED".equalsIgnoreCase(value)) {
                level = 1;
            } else if ("REPEATABLE_READ".equalsIgnoreCase(value)) {
                level = 4;
            } else if ("SERIALIZABLE".equalsIgnoreCase(value)) {
                level = 8;
            } else {
                try {
                    level = Integer.parseInt(value);
                }
                catch (NumberFormatException e) {
                    System.err.println("Could not parse defaultTransactionIsolation: " + value);
                    System.err.println("WARNING: defaultTransactionIsolation not set");
                    System.err.println("using default value of database driver");
                    level = -1;
                }
            }
            poolProperties.setDefaultTransactionIsolation(level);
        }
        if ((value = properties.getProperty(PROP_DEFAULTCATALOG)) != null) {
            poolProperties.setDefaultCatalog(value);
        }
        if ((value = properties.getProperty(PROP_DRIVERCLASSNAME)) != null) {
            poolProperties.setDriverClassName(value);
        }
        if ((value = properties.getProperty(PROP_MAXACTIVE)) != null) {
            poolProperties.setMaxActive(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_MAXIDLE)) != null) {
            poolProperties.setMaxIdle(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_MINIDLE)) != null) {
            poolProperties.setMinIdle(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_INITIALSIZE)) != null) {
            poolProperties.setInitialSize(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_MAXWAIT)) != null) {
            poolProperties.setMaxWait(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_TESTONBORROW)) != null) {
            poolProperties.setTestOnBorrow(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_TESTONRETURN)) != null) {
            poolProperties.setTestOnReturn(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_TESTONCONNECT)) != null) {
            poolProperties.setTestOnConnect(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_TIMEBETWEENEVICTIONRUNSMILLIS)) != null) {
            poolProperties.setTimeBetweenEvictionRunsMillis(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_NUMTESTSPEREVICTIONRUN)) != null) {
            poolProperties.setNumTestsPerEvictionRun(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_MINEVICTABLEIDLETIMEMILLIS)) != null) {
            poolProperties.setMinEvictableIdleTimeMillis(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_TESTWHILEIDLE)) != null) {
            poolProperties.setTestWhileIdle(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_PASSWORD)) != null) {
            poolProperties.setPassword(value);
        }
        if ((value = properties.getProperty(PROP_URL)) != null) {
            poolProperties.setUrl(value);
        }
        if ((value = properties.getProperty(PROP_USERNAME)) != null) {
            poolProperties.setUsername(value);
        }
        if ((value = properties.getProperty(PROP_VALIDATIONQUERY)) != null) {
            poolProperties.setValidationQuery(value);
        }
        if ((value = properties.getProperty(PROP_VALIDATIONQUERY_TIMEOUT)) != null) {
            poolProperties.setValidationQueryTimeout(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_VALIDATOR_CLASS_NAME)) != null) {
            poolProperties.setValidatorClassName(value);
        }
        if ((value = properties.getProperty(PROP_VALIDATIONINTERVAL)) != null) {
            poolProperties.setValidationInterval(Long.parseLong(value));
        }
        if ((value = properties.getProperty(PROP_ACCESSTOUNDERLYINGCONNECTIONALLOWED)) != null) {
            poolProperties.setAccessToUnderlyingConnectionAllowed(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_REMOVEABANDONED)) != null) {
            poolProperties.setRemoveAbandoned(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_REMOVEABANDONEDTIMEOUT)) != null) {
            poolProperties.setRemoveAbandonedTimeout(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_LOGABANDONED)) != null) {
            poolProperties.setLogAbandoned(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_POOLPREPAREDSTATEMENTS)) != null) {
            log.warn((Object)"poolPreparedStatements is not a valid setting, it will have no effect.");
        }
        if ((value = properties.getProperty(PROP_MAXOPENPREPAREDSTATEMENTS)) != null) {
            log.warn((Object)"maxOpenPreparedStatements is not a valid setting, it will have no effect.");
        }
        if ((value = properties.getProperty(PROP_CONNECTIONPROPERTIES)) != null) {
            Properties p = DataSourceFactory.getProperties(value);
            poolProperties.setDbProperties(p);
        } else {
            poolProperties.setDbProperties(new Properties());
        }
        if (poolProperties.getUsername() != null) {
            poolProperties.getDbProperties().setProperty("user", poolProperties.getUsername());
        }
        if (poolProperties.getPassword() != null) {
            poolProperties.getDbProperties().setProperty(PROP_PASSWORD, poolProperties.getPassword());
        }
        if ((value = properties.getProperty(PROP_INITSQL)) != null) {
            poolProperties.setInitSQL(value);
        }
        if ((value = properties.getProperty(PROP_INTERCEPTORS)) != null) {
            poolProperties.setJdbcInterceptors(value);
        }
        if ((value = properties.getProperty(PROP_JMX_ENABLED)) != null) {
            poolProperties.setJmxEnabled(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_FAIR_QUEUE)) != null) {
            poolProperties.setFairQueue(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_USE_EQUALS)) != null) {
            poolProperties.setUseEquals(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(OBJECT_NAME)) != null) {
            poolProperties.setName(ObjectName.quote(value));
        }
        if ((value = properties.getProperty(PROP_ABANDONWHENPERCENTAGEFULL)) != null) {
            poolProperties.setAbandonWhenPercentageFull(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_MAXAGE)) != null) {
            poolProperties.setMaxAge(Long.parseLong(value));
        }
        if ((value = properties.getProperty(PROP_USE_CON_LOCK)) != null) {
            poolProperties.setUseLock(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_DATASOURCE)) != null) {
            throw new IllegalArgumentException("Can't set dataSource property as a string, this must be a javax.sql.DataSource object.");
        }
        value = properties.getProperty(PROP_DATASOURCE_JNDI);
        if (value != null) {
            poolProperties.setDataSourceJNDI(value);
        }
        if ((value = properties.getProperty(PROP_SUSPECT_TIMEOUT)) != null) {
            poolProperties.setSuspectTimeout(Integer.parseInt(value));
        }
        if ((value = properties.getProperty(PROP_ALTERNATE_USERNAME_ALLOWED)) != null) {
            poolProperties.setAlternateUsernameAllowed(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_COMMITONRETURN)) != null) {
            poolProperties.setCommitOnReturn(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_ROLLBACKONRETURN)) != null) {
            poolProperties.setRollbackOnReturn(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_USEDISPOSABLECONNECTIONFACADE)) != null) {
            poolProperties.setUseDisposableConnectionFacade(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_LOGVALIDATIONERRORS)) != null) {
            poolProperties.setLogValidationErrors(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_PROPAGATEINTERRUPTSTATE)) != null) {
            poolProperties.setPropagateInterruptState(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_IGNOREEXCEPTIONONPRELOAD)) != null) {
            poolProperties.setIgnoreExceptionOnPreLoad(Boolean.parseBoolean(value));
        }
        if ((value = properties.getProperty(PROP_USESTATEMENTFACADE)) != null) {
            poolProperties.setUseStatementFacade(Boolean.parseBoolean(value));
        }
        return poolProperties;
    }

    public javax.sql.DataSource createDataSource(Properties properties) throws Exception {
        return this.createDataSource(properties, null, false);
    }

    public javax.sql.DataSource createDataSource(Properties properties, Context context, boolean XA) throws Exception {
        PoolConfiguration poolProperties = DataSourceFactory.parsePoolProperties(properties);
        if (poolProperties.getDataSourceJNDI() != null && poolProperties.getDataSource() == null) {
            this.performJNDILookup(context, poolProperties);
        }
        DataSource dataSource = XA ? new XADataSource(poolProperties) : new DataSource(poolProperties);
        dataSource.createPool();
        return dataSource;
    }

    public void performJNDILookup(Context context, PoolConfiguration poolProperties) {
        Object jndiDS = null;
        try {
            if (context != null) {
                jndiDS = context.lookup(poolProperties.getDataSourceJNDI());
            } else {
                log.warn((Object)"dataSourceJNDI property is configured, but local JNDI context is null.");
            }
        }
        catch (NamingException e) {
            log.debug((Object)("The name \"" + poolProperties.getDataSourceJNDI() + "\" cannot be found in the local context."));
        }
        if (jndiDS == null) {
            try {
                context = new InitialContext();
                jndiDS = context.lookup(poolProperties.getDataSourceJNDI());
            }
            catch (NamingException e) {
                log.warn((Object)("The name \"" + poolProperties.getDataSourceJNDI() + "\" cannot be found in the InitialContext."));
            }
        }
        if (jndiDS != null) {
            poolProperties.setDataSource(jndiDS);
        }
    }

    protected static Properties getProperties(String propText) {
        return PoolProperties.getProperties(propText, null);
    }
}

