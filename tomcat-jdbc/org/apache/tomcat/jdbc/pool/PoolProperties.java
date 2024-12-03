/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.juli.logging.Log
 *  org.apache.juli.logging.LogFactory
 */
package org.apache.tomcat.jdbc.pool;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;
import org.apache.tomcat.jdbc.pool.ClassLoaderUtil;
import org.apache.tomcat.jdbc.pool.DataSourceFactory;
import org.apache.tomcat.jdbc.pool.DataSourceProxy;
import org.apache.tomcat.jdbc.pool.JdbcInterceptor;
import org.apache.tomcat.jdbc.pool.PoolConfiguration;
import org.apache.tomcat.jdbc.pool.TrapException;
import org.apache.tomcat.jdbc.pool.Validator;

public class PoolProperties
implements PoolConfiguration,
Cloneable,
Serializable {
    private static final long serialVersionUID = -8519283440854213745L;
    private static final Log log = LogFactory.getLog(PoolProperties.class);
    public static final int DEFAULT_MAX_ACTIVE = 100;
    protected static final AtomicInteger poolCounter = new AtomicInteger(0);
    private volatile Properties dbProperties = new Properties();
    private volatile String url = null;
    private volatile String driverClassName = null;
    private volatile Boolean defaultAutoCommit = null;
    private volatile Boolean defaultReadOnly = null;
    private volatile int defaultTransactionIsolation = -1;
    private volatile String defaultCatalog = null;
    private volatile String connectionProperties;
    private volatile int initialSize = 10;
    private volatile int maxActive;
    private volatile int maxIdle = this.maxActive = 100;
    private volatile int minIdle = this.initialSize;
    private volatile int maxWait = 30000;
    private volatile String validationQuery;
    private volatile int validationQueryTimeout = -1;
    private volatile String validatorClassName;
    private volatile transient Validator validator;
    private volatile boolean testOnBorrow = false;
    private volatile boolean testOnReturn = false;
    private volatile boolean testWhileIdle = false;
    private volatile int timeBetweenEvictionRunsMillis = 5000;
    private volatile int numTestsPerEvictionRun;
    private volatile int minEvictableIdleTimeMillis = 60000;
    private volatile boolean accessToUnderlyingConnectionAllowed = true;
    private volatile boolean removeAbandoned = false;
    private volatile int removeAbandonedTimeout = 60;
    private volatile boolean logAbandoned = false;
    private volatile String name = "Tomcat Connection Pool[" + poolCounter.addAndGet(1) + "-" + System.identityHashCode(PoolProperties.class) + "]";
    private volatile String password;
    private volatile String username;
    private volatile long validationInterval = 3000L;
    private volatile boolean jmxEnabled = true;
    private volatile String initSQL;
    private volatile boolean testOnConnect = false;
    private volatile String jdbcInterceptors = null;
    private volatile boolean fairQueue = true;
    private volatile boolean useEquals = true;
    private volatile int abandonWhenPercentageFull = 0;
    private volatile long maxAge = 0L;
    private volatile boolean useLock = false;
    private volatile InterceptorDefinition[] interceptors = null;
    private volatile int suspectTimeout = 0;
    private volatile Object dataSource = null;
    private volatile String dataSourceJNDI = null;
    private volatile boolean alternateUsernameAllowed = false;
    private volatile boolean commitOnReturn = false;
    private volatile boolean rollbackOnReturn = false;
    private volatile boolean useDisposableConnectionFacade = true;
    private volatile boolean logValidationErrors = false;
    private volatile boolean propagateInterruptState = false;
    private volatile boolean ignoreExceptionOnPreLoad = false;
    private volatile boolean useStatementFacade = true;

    @Override
    public void setAbandonWhenPercentageFull(int percentage) {
        this.abandonWhenPercentageFull = percentage < 0 ? 0 : (percentage > 100 ? 100 : percentage);
    }

    @Override
    public int getAbandonWhenPercentageFull() {
        return this.abandonWhenPercentageFull;
    }

    @Override
    public boolean isFairQueue() {
        return this.fairQueue;
    }

    @Override
    public void setFairQueue(boolean fairQueue) {
        this.fairQueue = fairQueue;
    }

    @Override
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return this.accessToUnderlyingConnectionAllowed;
    }

    @Override
    public String getConnectionProperties() {
        return this.connectionProperties;
    }

    @Override
    public Properties getDbProperties() {
        return this.dbProperties;
    }

    @Override
    public Boolean isDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    @Override
    public String getDefaultCatalog() {
        return this.defaultCatalog;
    }

    @Override
    public Boolean isDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    @Override
    public int getDefaultTransactionIsolation() {
        return this.defaultTransactionIsolation;
    }

    @Override
    public String getDriverClassName() {
        return this.driverClassName;
    }

    @Override
    public int getInitialSize() {
        return this.initialSize;
    }

    @Override
    public boolean isLogAbandoned() {
        return this.logAbandoned;
    }

    @Override
    public int getMaxActive() {
        return this.maxActive;
    }

    @Override
    public int getMaxIdle() {
        return this.maxIdle;
    }

    @Override
    public int getMaxWait() {
        return this.maxWait;
    }

    @Override
    public int getMinEvictableIdleTimeMillis() {
        return this.minEvictableIdleTimeMillis;
    }

    @Override
    public int getMinIdle() {
        return this.minIdle;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public int getNumTestsPerEvictionRun() {
        return this.numTestsPerEvictionRun;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getPoolName() {
        return this.getName();
    }

    @Override
    public boolean isRemoveAbandoned() {
        return this.removeAbandoned;
    }

    @Override
    public int getRemoveAbandonedTimeout() {
        return this.removeAbandonedTimeout;
    }

    @Override
    public boolean isTestOnBorrow() {
        return this.testOnBorrow;
    }

    @Override
    public boolean isTestOnReturn() {
        return this.testOnReturn;
    }

    @Override
    public boolean isTestWhileIdle() {
        return this.testWhileIdle;
    }

    @Override
    public int getTimeBetweenEvictionRunsMillis() {
        return this.timeBetweenEvictionRunsMillis;
    }

    @Override
    public String getUrl() {
        return this.url;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public String getValidationQuery() {
        return this.validationQuery;
    }

    @Override
    public int getValidationQueryTimeout() {
        return this.validationQueryTimeout;
    }

    @Override
    public void setValidationQueryTimeout(int validationQueryTimeout) {
        this.validationQueryTimeout = validationQueryTimeout;
    }

    @Override
    public String getValidatorClassName() {
        return this.validatorClassName;
    }

    @Override
    public Validator getValidator() {
        return this.validator;
    }

    @Override
    public void setValidator(Validator validator) {
        this.validator = validator;
        this.validatorClassName = validator != null ? validator.getClass().getName() : null;
    }

    @Override
    public long getValidationInterval() {
        return this.validationInterval;
    }

    @Override
    public String getInitSQL() {
        return this.initSQL;
    }

    @Override
    public boolean isTestOnConnect() {
        return this.testOnConnect;
    }

    @Override
    public String getJdbcInterceptors() {
        return this.jdbcInterceptors;
    }

    @Override
    public InterceptorDefinition[] getJdbcInterceptorsAsArray() {
        if (this.interceptors == null) {
            if (this.jdbcInterceptors == null) {
                this.interceptors = new InterceptorDefinition[0];
            } else {
                String[] interceptorValues = this.jdbcInterceptors.split(";");
                InterceptorDefinition[] definitions = new InterceptorDefinition[interceptorValues.length + 1];
                definitions[0] = new InterceptorDefinition(TrapException.class);
                for (int i = 0; i < interceptorValues.length; ++i) {
                    int propIndex = interceptorValues[i].indexOf(40);
                    int endIndex = interceptorValues[i].indexOf(41);
                    if (propIndex < 0 || endIndex < 0 || endIndex <= propIndex) {
                        definitions[i + 1] = new InterceptorDefinition(interceptorValues[i].trim());
                        continue;
                    }
                    String name = interceptorValues[i].substring(0, propIndex).trim();
                    definitions[i + 1] = new InterceptorDefinition(name);
                    String propsAsString = interceptorValues[i].substring(propIndex + 1, endIndex);
                    String[] props = propsAsString.split(",");
                    for (int j = 0; j < props.length; ++j) {
                        int pidx = props[j].indexOf(61);
                        String propName = props[j].substring(0, pidx).trim();
                        String propValue = props[j].substring(pidx + 1).trim();
                        definitions[i + 1].addProperty(new InterceptorProperty(propName, propValue));
                    }
                }
                this.interceptors = definitions;
            }
        }
        return this.interceptors;
    }

    @Override
    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
    }

    @Override
    public void setConnectionProperties(String connectionProperties) {
        this.connectionProperties = connectionProperties;
        PoolProperties.getProperties(connectionProperties, this.getDbProperties());
    }

    @Override
    public void setDbProperties(Properties dbProperties) {
        this.dbProperties = dbProperties;
    }

    @Override
    public void setDefaultAutoCommit(Boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    @Override
    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    @Override
    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    @Override
    public void setDefaultTransactionIsolation(int defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    @Override
    public void setDriverClassName(String driverClassName) {
        this.driverClassName = driverClassName;
    }

    @Override
    public void setInitialSize(int initialSize) {
        this.initialSize = initialSize;
    }

    @Override
    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    @Override
    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    @Override
    public void setMaxIdle(int maxIdle) {
        this.maxIdle = maxIdle;
    }

    @Override
    public void setMaxWait(int maxWait) {
        this.maxWait = maxWait;
    }

    @Override
    public void setMinEvictableIdleTimeMillis(int minEvictableIdleTimeMillis) {
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    @Override
    public void setMinIdle(int minIdle) {
        this.minIdle = minIdle;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    @Override
    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeout = removeAbandonedTimeout;
    }

    @Override
    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    @Override
    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    @Override
    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    @Override
    public void setTimeBetweenEvictionRunsMillis(int timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public void setValidationInterval(long validationInterval) {
        this.validationInterval = validationInterval;
    }

    @Override
    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    @Override
    public void setValidatorClassName(String className) {
        this.validatorClassName = className;
        this.validator = null;
        if (className == null) {
            return;
        }
        try {
            Class<?> validatorClass = ClassLoaderUtil.loadClass(className, PoolProperties.class.getClassLoader(), Thread.currentThread().getContextClassLoader());
            this.validator = (Validator)validatorClass.getConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (ClassNotFoundException e) {
            log.warn((Object)("The class " + className + " cannot be found."), (Throwable)e);
        }
        catch (ClassCastException e) {
            log.warn((Object)("The class " + className + " does not implement the Validator interface."), (Throwable)e);
        }
        catch (IllegalAccessException e) {
            log.warn((Object)("The class " + className + " or its no-arg constructor are inaccessible."), (Throwable)e);
        }
        catch (IllegalArgumentException | ReflectiveOperationException | SecurityException e) {
            log.warn((Object)("An object of class " + className + " cannot be instantiated. Make sure that it includes an implicit or explicit no-arg constructor."), (Throwable)e);
        }
    }

    @Override
    public void setInitSQL(String initSQL) {
        this.initSQL = initSQL != null && initSQL.trim().length() > 0 ? initSQL : null;
    }

    @Override
    public void setTestOnConnect(boolean testOnConnect) {
        this.testOnConnect = testOnConnect;
    }

    @Override
    public void setJdbcInterceptors(String jdbcInterceptors) {
        this.jdbcInterceptors = jdbcInterceptors;
        this.interceptors = null;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder("ConnectionPool[");
        try {
            String[] fields;
            block4: for (String field : fields = DataSourceFactory.ALL_PROPERTIES) {
                String[] prefix = new String[]{"get", "is"};
                for (int j = 0; j < prefix.length; ++j) {
                    String name = prefix[j] + field.substring(0, 1).toUpperCase(Locale.ENGLISH) + field.substring(1);
                    Method m = null;
                    try {
                        m = this.getClass().getMethod(name, new Class[0]);
                    }
                    catch (NoSuchMethodException nm) {
                        continue;
                    }
                    buf.append(field);
                    buf.append('=');
                    if ("password".equals(field)) {
                        buf.append("********");
                    } else {
                        buf.append(m.invoke((Object)this, new Object[0]));
                    }
                    buf.append("; ");
                    continue block4;
                }
            }
            buf.append(']');
        }
        catch (Exception x) {
            log.debug((Object)"toString() call failed", (Throwable)x);
        }
        return buf.toString();
    }

    public static int getPoolCounter() {
        return poolCounter.get();
    }

    @Override
    public boolean isJmxEnabled() {
        return this.jmxEnabled;
    }

    @Override
    public void setJmxEnabled(boolean jmxEnabled) {
        this.jmxEnabled = jmxEnabled;
    }

    @Override
    public Boolean getDefaultAutoCommit() {
        return this.defaultAutoCommit;
    }

    @Override
    public Boolean getDefaultReadOnly() {
        return this.defaultReadOnly;
    }

    @Override
    public int getSuspectTimeout() {
        return this.suspectTimeout;
    }

    @Override
    public void setSuspectTimeout(int seconds) {
        this.suspectTimeout = seconds;
    }

    @Override
    public boolean isPoolSweeperEnabled() {
        boolean timer = this.getTimeBetweenEvictionRunsMillis() > 0;
        boolean result = timer && this.isRemoveAbandoned() && this.getRemoveAbandonedTimeout() > 0;
        result = result || timer && this.getSuspectTimeout() > 0;
        result = result || timer && this.isTestWhileIdle();
        result = result || timer && this.getMinEvictableIdleTimeMillis() > 0;
        result = result || timer && this.getMaxAge() > 0L;
        return result;
    }

    @Override
    public boolean isUseEquals() {
        return this.useEquals;
    }

    @Override
    public void setUseEquals(boolean useEquals) {
        this.useEquals = useEquals;
    }

    @Override
    public long getMaxAge() {
        return this.maxAge;
    }

    @Override
    public void setMaxAge(long maxAge) {
        this.maxAge = maxAge;
    }

    @Override
    public boolean getUseLock() {
        return this.useLock;
    }

    @Override
    public void setUseLock(boolean useLock) {
        this.useLock = useLock;
    }

    @Override
    public void setDataSource(Object ds) {
        if (ds instanceof DataSourceProxy) {
            throw new IllegalArgumentException("Layered pools are not allowed.");
        }
        this.dataSource = ds;
    }

    @Override
    public Object getDataSource() {
        return this.dataSource;
    }

    @Override
    public void setDataSourceJNDI(String jndiDS) {
        this.dataSourceJNDI = jndiDS;
    }

    @Override
    public String getDataSourceJNDI() {
        return this.dataSourceJNDI;
    }

    public static Properties getProperties(String propText, Properties props) {
        if (props == null) {
            props = new Properties();
        }
        if (propText != null) {
            try {
                props.load(new ByteArrayInputStream(propText.replace(';', '\n').getBytes()));
            }
            catch (IOException x) {
                throw new RuntimeException(x);
            }
        }
        return props;
    }

    @Override
    public boolean isAlternateUsernameAllowed() {
        return this.alternateUsernameAllowed;
    }

    @Override
    public void setAlternateUsernameAllowed(boolean alternateUsernameAllowed) {
        this.alternateUsernameAllowed = alternateUsernameAllowed;
    }

    @Override
    public void setCommitOnReturn(boolean commitOnReturn) {
        this.commitOnReturn = commitOnReturn;
    }

    @Override
    public boolean getCommitOnReturn() {
        return this.commitOnReturn;
    }

    @Override
    public void setRollbackOnReturn(boolean rollbackOnReturn) {
        this.rollbackOnReturn = rollbackOnReturn;
    }

    @Override
    public boolean getRollbackOnReturn() {
        return this.rollbackOnReturn;
    }

    @Override
    public void setUseDisposableConnectionFacade(boolean useDisposableConnectionFacade) {
        this.useDisposableConnectionFacade = useDisposableConnectionFacade;
    }

    @Override
    public boolean getUseDisposableConnectionFacade() {
        return this.useDisposableConnectionFacade;
    }

    @Override
    public void setLogValidationErrors(boolean logValidationErrors) {
        this.logValidationErrors = logValidationErrors;
    }

    @Override
    public boolean getLogValidationErrors() {
        return this.logValidationErrors;
    }

    @Override
    public boolean getPropagateInterruptState() {
        return this.propagateInterruptState;
    }

    @Override
    public void setPropagateInterruptState(boolean propagateInterruptState) {
        this.propagateInterruptState = propagateInterruptState;
    }

    @Override
    public boolean isIgnoreExceptionOnPreLoad() {
        return this.ignoreExceptionOnPreLoad;
    }

    @Override
    public void setIgnoreExceptionOnPreLoad(boolean ignoreExceptionOnPreLoad) {
        this.ignoreExceptionOnPreLoad = ignoreExceptionOnPreLoad;
    }

    @Override
    public boolean getUseStatementFacade() {
        return this.useStatementFacade;
    }

    @Override
    public void setUseStatementFacade(boolean useStatementFacade) {
        this.useStatementFacade = useStatementFacade;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public static class InterceptorDefinition
    implements Serializable {
        private static final long serialVersionUID = 1L;
        protected String className;
        protected Map<String, InterceptorProperty> properties = new HashMap<String, InterceptorProperty>();
        protected volatile Class<?> clazz = null;

        public InterceptorDefinition(String className) {
            this.className = className;
        }

        public InterceptorDefinition(Class<?> cl) {
            this(cl.getName());
            this.clazz = cl;
        }

        public String getClassName() {
            return this.className;
        }

        public void addProperty(String name, String value) {
            InterceptorProperty p = new InterceptorProperty(name, value);
            this.addProperty(p);
        }

        public void addProperty(InterceptorProperty p) {
            this.properties.put(p.getName(), p);
        }

        public Map<String, InterceptorProperty> getProperties() {
            return this.properties;
        }

        public Class<? extends JdbcInterceptor> getInterceptorClass() throws ClassNotFoundException {
            if (this.clazz == null) {
                if (this.getClassName().indexOf(46) < 0) {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Loading interceptor class:org.apache.tomcat.jdbc.pool.interceptor." + this.getClassName()));
                    }
                    this.clazz = ClassLoaderUtil.loadClass("org.apache.tomcat.jdbc.pool.interceptor." + this.getClassName(), PoolProperties.class.getClassLoader(), Thread.currentThread().getContextClassLoader());
                } else {
                    if (log.isDebugEnabled()) {
                        log.debug((Object)("Loading interceptor class:" + this.getClassName()));
                    }
                    this.clazz = ClassLoaderUtil.loadClass(this.getClassName(), PoolProperties.class.getClassLoader(), Thread.currentThread().getContextClassLoader());
                }
            }
            return this.clazz;
        }
    }

    public static class InterceptorProperty
    implements Serializable {
        private static final long serialVersionUID = 1L;
        String name;
        String value;

        public InterceptorProperty(String name, String value) {
            assert (name != null);
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return this.name;
        }

        public String getValue() {
            return this.value;
        }

        public boolean getValueAsBoolean(boolean def) {
            if (this.value == null) {
                return def;
            }
            if ("true".equals(this.value)) {
                return true;
            }
            if ("false".equals(this.value)) {
                return false;
            }
            return def;
        }

        public int getValueAsInt(int def) {
            if (this.value == null) {
                return def;
            }
            try {
                int v = Integer.parseInt(this.value);
                return v;
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public long getValueAsLong(long def) {
            if (this.value == null) {
                return def;
            }
            try {
                return Long.parseLong(this.value);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public byte getValueAsByte(byte def) {
            if (this.value == null) {
                return def;
            }
            try {
                return Byte.parseByte(this.value);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public short getValueAsShort(short def) {
            if (this.value == null) {
                return def;
            }
            try {
                return Short.parseShort(this.value);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public float getValueAsFloat(float def) {
            if (this.value == null) {
                return def;
            }
            try {
                return Float.parseFloat(this.value);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public double getValueAsDouble(double def) {
            if (this.value == null) {
                return def;
            }
            try {
                return Double.parseDouble(this.value);
            }
            catch (NumberFormatException nfe) {
                return def;
            }
        }

        public char getValueAschar(char def) {
            if (this.value == null) {
                return def;
            }
            try {
                return this.value.charAt(0);
            }
            catch (StringIndexOutOfBoundsException nfe) {
                return def;
            }
        }

        public int hashCode() {
            return this.name.hashCode();
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            }
            if (o instanceof InterceptorProperty) {
                InterceptorProperty other = (InterceptorProperty)o;
                return other.name.equals(this.name);
            }
            return false;
        }
    }
}

