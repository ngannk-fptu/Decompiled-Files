/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.dbcp.dbcp2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.naming.Context;
import javax.naming.Name;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.apache.tomcat.dbcp.pool2.impl.BaseObjectPoolConfig;

public class BasicDataSourceFactory
implements ObjectFactory {
    private static final Log log = LogFactory.getLog(BasicDataSourceFactory.class);
    private static final String PROP_DEFAULT_AUTO_COMMIT = "defaultAutoCommit";
    private static final String PROP_DEFAULT_READ_ONLY = "defaultReadOnly";
    private static final String PROP_DEFAULT_TRANSACTION_ISOLATION = "defaultTransactionIsolation";
    private static final String PROP_DEFAULT_CATALOG = "defaultCatalog";
    private static final String PROP_DEFAULT_SCHEMA = "defaultSchema";
    private static final String PROP_CACHE_STATE = "cacheState";
    private static final String PROP_DRIVER_CLASS_NAME = "driverClassName";
    private static final String PROP_LIFO = "lifo";
    private static final String PROP_MAX_TOTAL = "maxTotal";
    private static final String PROP_MAX_IDLE = "maxIdle";
    private static final String PROP_MIN_IDLE = "minIdle";
    private static final String PROP_INITIAL_SIZE = "initialSize";
    private static final String PROP_MAX_WAIT_MILLIS = "maxWaitMillis";
    private static final String PROP_TEST_ON_CREATE = "testOnCreate";
    private static final String PROP_TEST_ON_BORROW = "testOnBorrow";
    private static final String PROP_TEST_ON_RETURN = "testOnReturn";
    private static final String PROP_TIME_BETWEEN_EVICTION_RUNS_MILLIS = "timeBetweenEvictionRunsMillis";
    private static final String PROP_NUM_TESTS_PER_EVICTION_RUN = "numTestsPerEvictionRun";
    private static final String PROP_MIN_EVICTABLE_IDLE_TIME_MILLIS = "minEvictableIdleTimeMillis";
    private static final String PROP_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS = "softMinEvictableIdleTimeMillis";
    private static final String PROP_EVICTION_POLICY_CLASS_NAME = "evictionPolicyClassName";
    private static final String PROP_TEST_WHILE_IDLE = "testWhileIdle";
    private static final String PROP_PASSWORD = "password";
    private static final String PROP_URL = "url";
    private static final String PROP_USER_NAME = "username";
    private static final String PROP_VALIDATION_QUERY = "validationQuery";
    private static final String PROP_VALIDATION_QUERY_TIMEOUT = "validationQueryTimeout";
    private static final String PROP_JMX_NAME = "jmxName";
    private static final String PROP_REGISTER_CONNECTION_MBEAN = "registerConnectionMBean";
    private static final String PROP_CONNECTION_FACTORY_CLASS_NAME = "connectionFactoryClassName";
    private static final String PROP_CONNECTION_INIT_SQLS = "connectionInitSqls";
    private static final String PROP_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED = "accessToUnderlyingConnectionAllowed";
    private static final String PROP_REMOVE_ABANDONED_ON_BORROW = "removeAbandonedOnBorrow";
    private static final String PROP_REMOVE_ABANDONED_ON_MAINTENANCE = "removeAbandonedOnMaintenance";
    private static final String PROP_REMOVE_ABANDONED_TIMEOUT = "removeAbandonedTimeout";
    private static final String PROP_LOG_ABANDONED = "logAbandoned";
    private static final String PROP_ABANDONED_USAGE_TRACKING = "abandonedUsageTracking";
    private static final String PROP_POOL_PREPARED_STATEMENTS = "poolPreparedStatements";
    private static final String PROP_CLEAR_STATEMENT_POOL_ON_RETURN = "clearStatementPoolOnReturn";
    private static final String PROP_MAX_OPEN_PREPARED_STATEMENTS = "maxOpenPreparedStatements";
    private static final String PROP_CONNECTION_PROPERTIES = "connectionProperties";
    private static final String PROP_MAX_CONN_LIFETIME_MILLIS = "maxConnLifetimeMillis";
    private static final String PROP_LOG_EXPIRED_CONNECTIONS = "logExpiredConnections";
    private static final String PROP_ROLLBACK_ON_RETURN = "rollbackOnReturn";
    private static final String PROP_ENABLE_AUTO_COMMIT_ON_RETURN = "enableAutoCommitOnReturn";
    private static final String PROP_DEFAULT_QUERY_TIMEOUT = "defaultQueryTimeout";
    private static final String PROP_FAST_FAIL_VALIDATION = "fastFailValidation";
    private static final String PROP_DISCONNECTION_SQL_CODES = "disconnectionSqlCodes";
    private static final String NUPROP_MAX_ACTIVE = "maxActive";
    private static final String NUPROP_REMOVE_ABANDONED = "removeAbandoned";
    private static final String NUPROP_MAXWAIT = "maxWait";
    private static final String SILENT_PROP_FACTORY = "factory";
    private static final String SILENT_PROP_SCOPE = "scope";
    private static final String SILENT_PROP_SINGLETON = "singleton";
    private static final String SILENT_PROP_AUTH = "auth";
    private static final List<String> ALL_PROPERTY_NAMES = Arrays.asList("defaultAutoCommit", "defaultReadOnly", "defaultTransactionIsolation", "defaultCatalog", "defaultSchema", "cacheState", "driverClassName", "lifo", "maxTotal", "maxIdle", "minIdle", "initialSize", "maxWaitMillis", "testOnCreate", "testOnBorrow", "testOnReturn", "timeBetweenEvictionRunsMillis", "numTestsPerEvictionRun", "minEvictableIdleTimeMillis", "softMinEvictableIdleTimeMillis", "evictionPolicyClassName", "testWhileIdle", "password", "url", "username", "validationQuery", "validationQueryTimeout", "connectionInitSqls", "accessToUnderlyingConnectionAllowed", "removeAbandonedOnBorrow", "removeAbandonedOnMaintenance", "removeAbandonedTimeout", "logAbandoned", "abandonedUsageTracking", "poolPreparedStatements", "clearStatementPoolOnReturn", "maxOpenPreparedStatements", "connectionProperties", "maxConnLifetimeMillis", "logExpiredConnections", "rollbackOnReturn", "enableAutoCommitOnReturn", "defaultQueryTimeout", "fastFailValidation", "disconnectionSqlCodes", "jmxName", "registerConnectionMBean", "connectionFactoryClassName");
    private static final Map<String, String> NUPROP_WARNTEXT = new LinkedHashMap<String, String>();
    private static final List<String> SILENT_PROPERTIES;

    private static <V> void accept(Properties properties, String name, Function<String, V> parser, Consumer<V> consumer) {
        BasicDataSourceFactory.getOptional(properties, name).ifPresent(v -> consumer.accept(parser.apply((String)v)));
    }

    private static void acceptBoolean(Properties properties, String name, Consumer<Boolean> consumer) {
        BasicDataSourceFactory.accept(properties, name, Boolean::parseBoolean, consumer);
    }

    private static void acceptDurationOfMillis(Properties properties, String name, Consumer<Duration> consumer) {
        BasicDataSourceFactory.accept(properties, name, s -> Duration.ofMillis(Long.parseLong(s)), consumer);
    }

    private static void acceptDurationOfSeconds(Properties properties, String name, Consumer<Duration> consumer) {
        BasicDataSourceFactory.accept(properties, name, s -> Duration.ofSeconds(Long.parseLong(s)), consumer);
    }

    private static void acceptInt(Properties properties, String name, Consumer<Integer> consumer) {
        BasicDataSourceFactory.accept(properties, name, Integer::parseInt, consumer);
    }

    private static void acceptString(Properties properties, String name, Consumer<String> consumer) {
        BasicDataSourceFactory.accept(properties, name, Function.identity(), consumer);
    }

    public static BasicDataSource createDataSource(Properties properties) throws SQLException {
        BasicDataSource dataSource = new BasicDataSource();
        BasicDataSourceFactory.acceptBoolean(properties, PROP_DEFAULT_AUTO_COMMIT, dataSource::setDefaultAutoCommit);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_DEFAULT_READ_ONLY, dataSource::setDefaultReadOnly);
        BasicDataSourceFactory.getOptional(properties, PROP_DEFAULT_TRANSACTION_ISOLATION).ifPresent(value -> {
            value = value.toUpperCase(Locale.ROOT);
            int level = -1;
            if ("NONE".equals(value)) {
                level = 0;
            } else if ("READ_COMMITTED".equals(value)) {
                level = 2;
            } else if ("READ_UNCOMMITTED".equals(value)) {
                level = 1;
            } else if ("REPEATABLE_READ".equals(value)) {
                level = 4;
            } else if ("SERIALIZABLE".equals(value)) {
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
            dataSource.setDefaultTransactionIsolation(level);
        });
        BasicDataSourceFactory.acceptString(properties, PROP_DEFAULT_SCHEMA, dataSource::setDefaultSchema);
        BasicDataSourceFactory.acceptString(properties, PROP_DEFAULT_CATALOG, dataSource::setDefaultCatalog);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_CACHE_STATE, dataSource::setCacheState);
        BasicDataSourceFactory.acceptString(properties, PROP_DRIVER_CLASS_NAME, dataSource::setDriverClassName);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_LIFO, dataSource::setLifo);
        BasicDataSourceFactory.acceptInt(properties, PROP_MAX_TOTAL, dataSource::setMaxTotal);
        BasicDataSourceFactory.acceptInt(properties, PROP_MAX_IDLE, dataSource::setMaxIdle);
        BasicDataSourceFactory.acceptInt(properties, PROP_MIN_IDLE, dataSource::setMinIdle);
        BasicDataSourceFactory.acceptInt(properties, PROP_INITIAL_SIZE, dataSource::setInitialSize);
        BasicDataSourceFactory.acceptDurationOfMillis(properties, PROP_MAX_WAIT_MILLIS, dataSource::setMaxWait);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_TEST_ON_CREATE, dataSource::setTestOnCreate);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_TEST_ON_BORROW, dataSource::setTestOnBorrow);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_TEST_ON_RETURN, dataSource::setTestOnReturn);
        BasicDataSourceFactory.acceptDurationOfMillis(properties, PROP_TIME_BETWEEN_EVICTION_RUNS_MILLIS, dataSource::setDurationBetweenEvictionRuns);
        BasicDataSourceFactory.acceptInt(properties, PROP_NUM_TESTS_PER_EVICTION_RUN, dataSource::setNumTestsPerEvictionRun);
        BasicDataSourceFactory.acceptDurationOfMillis(properties, PROP_MIN_EVICTABLE_IDLE_TIME_MILLIS, dataSource::setMinEvictableIdle);
        BasicDataSourceFactory.acceptDurationOfMillis(properties, PROP_SOFT_MIN_EVICTABLE_IDLE_TIME_MILLIS, dataSource::setSoftMinEvictableIdle);
        BasicDataSourceFactory.acceptString(properties, PROP_EVICTION_POLICY_CLASS_NAME, dataSource::setEvictionPolicyClassName);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_TEST_WHILE_IDLE, dataSource::setTestWhileIdle);
        BasicDataSourceFactory.acceptString(properties, PROP_PASSWORD, dataSource::setPassword);
        BasicDataSourceFactory.acceptString(properties, PROP_URL, dataSource::setUrl);
        BasicDataSourceFactory.acceptString(properties, PROP_USER_NAME, dataSource::setUsername);
        BasicDataSourceFactory.acceptString(properties, PROP_VALIDATION_QUERY, dataSource::setValidationQuery);
        BasicDataSourceFactory.acceptDurationOfSeconds(properties, PROP_VALIDATION_QUERY_TIMEOUT, dataSource::setValidationQueryTimeout);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_ACCESS_TO_UNDERLYING_CONNECTION_ALLOWED, dataSource::setAccessToUnderlyingConnectionAllowed);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_REMOVE_ABANDONED_ON_BORROW, dataSource::setRemoveAbandonedOnBorrow);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_REMOVE_ABANDONED_ON_MAINTENANCE, dataSource::setRemoveAbandonedOnMaintenance);
        BasicDataSourceFactory.acceptDurationOfSeconds(properties, PROP_REMOVE_ABANDONED_TIMEOUT, dataSource::setRemoveAbandonedTimeout);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_LOG_ABANDONED, dataSource::setLogAbandoned);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_ABANDONED_USAGE_TRACKING, dataSource::setAbandonedUsageTracking);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_POOL_PREPARED_STATEMENTS, dataSource::setPoolPreparedStatements);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_CLEAR_STATEMENT_POOL_ON_RETURN, dataSource::setClearStatementPoolOnReturn);
        BasicDataSourceFactory.acceptInt(properties, PROP_MAX_OPEN_PREPARED_STATEMENTS, dataSource::setMaxOpenPreparedStatements);
        BasicDataSourceFactory.getOptional(properties, PROP_CONNECTION_INIT_SQLS).ifPresent(v -> dataSource.setConnectionInitSqls(BasicDataSourceFactory.parseList(v, ';')));
        String value2 = properties.getProperty(PROP_CONNECTION_PROPERTIES);
        if (value2 != null) {
            for (Object key : BasicDataSourceFactory.getProperties(value2).keySet()) {
                String propertyName = Objects.toString(key, null);
                dataSource.addConnectionProperty(propertyName, BasicDataSourceFactory.getProperties(value2).getProperty(propertyName));
            }
        }
        BasicDataSourceFactory.acceptDurationOfMillis(properties, PROP_MAX_CONN_LIFETIME_MILLIS, dataSource::setMaxConn);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_LOG_EXPIRED_CONNECTIONS, dataSource::setLogExpiredConnections);
        BasicDataSourceFactory.acceptString(properties, PROP_JMX_NAME, dataSource::setJmxName);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_REGISTER_CONNECTION_MBEAN, dataSource::setRegisterConnectionMBean);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_ENABLE_AUTO_COMMIT_ON_RETURN, dataSource::setAutoCommitOnReturn);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_ROLLBACK_ON_RETURN, dataSource::setRollbackOnReturn);
        BasicDataSourceFactory.acceptDurationOfSeconds(properties, PROP_DEFAULT_QUERY_TIMEOUT, dataSource::setDefaultQueryTimeout);
        BasicDataSourceFactory.acceptBoolean(properties, PROP_FAST_FAIL_VALIDATION, dataSource::setFastFailValidation);
        BasicDataSourceFactory.getOptional(properties, PROP_DISCONNECTION_SQL_CODES).ifPresent(v -> dataSource.setDisconnectionSqlCodes(BasicDataSourceFactory.parseList(v, ',')));
        BasicDataSourceFactory.acceptString(properties, PROP_CONNECTION_FACTORY_CLASS_NAME, dataSource::setConnectionFactoryClassName);
        if (dataSource.getInitialSize() > 0) {
            dataSource.getLogWriter();
        }
        return dataSource;
    }

    private static Optional<String> getOptional(Properties properties, String name) {
        return Optional.ofNullable(properties.getProperty(name));
    }

    private static Properties getProperties(String propText) throws SQLException {
        Properties p = new Properties();
        if (propText != null) {
            try {
                p.load(new ByteArrayInputStream(propText.replace(';', '\n').getBytes(StandardCharsets.ISO_8859_1)));
            }
            catch (IOException e) {
                throw new SQLException(propText, e);
            }
        }
        return p;
    }

    private static Collection<String> parseList(String value, char delimiter) {
        StringTokenizer tokenizer = new StringTokenizer(value, Character.toString(delimiter));
        ArrayList<String> tokens = new ArrayList<String>(tokenizer.countTokens());
        while (tokenizer.hasMoreTokens()) {
            tokens.add(tokenizer.nextToken());
        }
        return tokens;
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws SQLException {
        if (obj == null || !(obj instanceof Reference)) {
            return null;
        }
        Reference ref = (Reference)obj;
        if (!"javax.sql.DataSource".equals(ref.getClassName())) {
            return null;
        }
        ArrayList<String> warnMessages = new ArrayList<String>();
        ArrayList<String> infoMessages = new ArrayList<String>();
        this.validatePropertyNames(ref, name, warnMessages, infoMessages);
        warnMessages.forEach(arg_0 -> ((Log)log).warn(arg_0));
        infoMessages.forEach(arg_0 -> ((Log)log).info(arg_0));
        Properties properties = new Properties();
        ALL_PROPERTY_NAMES.forEach(propertyName -> {
            RefAddr ra = ref.get((String)propertyName);
            if (ra != null) {
                properties.setProperty((String)propertyName, Objects.toString(ra.getContent(), null));
            }
        });
        return BasicDataSourceFactory.createDataSource(properties);
    }

    private void validatePropertyNames(Reference ref, Name name, List<String> warnMessages, List<String> infoMessages) {
        String nameString;
        String string = nameString = name != null ? "Name = " + name.toString() + " " : "";
        if (NUPROP_WARNTEXT != null && !NUPROP_WARNTEXT.isEmpty()) {
            NUPROP_WARNTEXT.forEach((propertyName, value) -> {
                RefAddr ra = ref.get((String)propertyName);
                if (ra != null && !ALL_PROPERTY_NAMES.contains(ra.getType())) {
                    StringBuilder stringBuilder = new StringBuilder(nameString);
                    String propertyValue = Objects.toString(ra.getContent(), null);
                    stringBuilder.append((String)value).append(" You have set value of \"").append(propertyValue).append("\" for \"").append((String)propertyName).append("\" property, which is being ignored.");
                    warnMessages.add(stringBuilder.toString());
                }
            });
        }
        Enumeration<RefAddr> allRefAddrs = ref.getAll();
        while (allRefAddrs.hasMoreElements()) {
            RefAddr ra = allRefAddrs.nextElement();
            String propertyName2 = ra.getType();
            if (ALL_PROPERTY_NAMES.contains(propertyName2) || NUPROP_WARNTEXT.containsKey(propertyName2) || SILENT_PROPERTIES.contains(propertyName2)) continue;
            String propertyValue = Objects.toString(ra.getContent(), null);
            StringBuilder stringBuilder = new StringBuilder(nameString);
            stringBuilder.append("Ignoring unknown property: ").append("value of \"").append(propertyValue).append("\" for \"").append(propertyName2).append("\" property");
            infoMessages.add(stringBuilder.toString());
        }
    }

    static {
        NUPROP_WARNTEXT.put(NUPROP_MAX_ACTIVE, "Property maxActive is not used in DBCP2, use maxTotal instead. maxTotal default value is 8.");
        NUPROP_WARNTEXT.put(NUPROP_REMOVE_ABANDONED, "Property removeAbandoned is not used in DBCP2, use one or both of removeAbandonedOnBorrow or removeAbandonedOnMaintenance instead. Both have default value set to false.");
        NUPROP_WARNTEXT.put(NUPROP_MAXWAIT, "Property maxWait is not used in DBCP2 , use maxWaitMillis instead. maxWaitMillis default value is " + BaseObjectPoolConfig.DEFAULT_MAX_WAIT + ".");
        SILENT_PROPERTIES = new ArrayList<String>();
        SILENT_PROPERTIES.add(SILENT_PROP_FACTORY);
        SILENT_PROPERTIES.add(SILENT_PROP_SCOPE);
        SILENT_PROPERTIES.add(SILENT_PROP_SINGLETON);
        SILENT_PROPERTIES.add(SILENT_PROP_AUTH);
    }
}

