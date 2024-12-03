/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.jdbc.datasource;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.IsolationLevelDataSourceAdapter;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class WebSphereDataSourceAdapter
extends IsolationLevelDataSourceAdapter {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private Class<?> wsDataSourceClass;
    private Method newJdbcConnSpecMethod;
    private Method wsDataSourceGetConnectionMethod;
    private Method setTransactionIsolationMethod;
    private Method setReadOnlyMethod;
    private Method setUserNameMethod;
    private Method setPasswordMethod;

    public WebSphereDataSourceAdapter() {
        try {
            this.wsDataSourceClass = this.getClass().getClassLoader().loadClass("com.ibm.websphere.rsadapter.WSDataSource");
            Class<?> jdbcConnSpecClass = this.getClass().getClassLoader().loadClass("com.ibm.websphere.rsadapter.JDBCConnectionSpec");
            Class<?> wsrraFactoryClass = this.getClass().getClassLoader().loadClass("com.ibm.websphere.rsadapter.WSRRAFactory");
            this.newJdbcConnSpecMethod = wsrraFactoryClass.getMethod("createJDBCConnectionSpec", new Class[0]);
            this.wsDataSourceGetConnectionMethod = this.wsDataSourceClass.getMethod("getConnection", jdbcConnSpecClass);
            this.setTransactionIsolationMethod = jdbcConnSpecClass.getMethod("setTransactionIsolation", Integer.TYPE);
            this.setReadOnlyMethod = jdbcConnSpecClass.getMethod("setReadOnly", Boolean.class);
            this.setUserNameMethod = jdbcConnSpecClass.getMethod("setUserName", String.class);
            this.setPasswordMethod = jdbcConnSpecClass.getMethod("setPassword", String.class);
        }
        catch (Exception ex) {
            throw new IllegalStateException("Could not initialize WebSphereDataSourceAdapter because WebSphere API classes are not available: " + ex);
        }
    }

    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (!this.wsDataSourceClass.isInstance(this.getTargetDataSource())) {
            throw new IllegalStateException("Specified 'targetDataSource' is not a WebSphere WSDataSource: " + this.getTargetDataSource());
        }
    }

    @Override
    protected Connection doGetConnection(@Nullable String username, @Nullable String password) throws SQLException {
        Connection con;
        Object connSpec = this.createConnectionSpec(this.getCurrentIsolationLevel(), this.getCurrentReadOnlyFlag(), username, password);
        if (this.logger.isDebugEnabled()) {
            this.logger.debug((Object)("Obtaining JDBC Connection from WebSphere DataSource [" + this.getTargetDataSource() + "], using ConnectionSpec [" + connSpec + "]"));
        }
        Assert.state(((con = (Connection)WebSphereDataSourceAdapter.invokeJdbcMethod(this.wsDataSourceGetConnectionMethod, this.obtainTargetDataSource(), connSpec)) != null ? 1 : 0) != 0, (String)"No Connection");
        return con;
    }

    protected Object createConnectionSpec(@Nullable Integer isolationLevel, @Nullable Boolean readOnlyFlag, @Nullable String username, @Nullable String password) throws SQLException {
        Object connSpec = WebSphereDataSourceAdapter.invokeJdbcMethod(this.newJdbcConnSpecMethod, null, new Object[0]);
        Assert.state((connSpec != null ? 1 : 0) != 0, (String)"No JDBCConnectionSpec");
        if (isolationLevel != null) {
            WebSphereDataSourceAdapter.invokeJdbcMethod(this.setTransactionIsolationMethod, connSpec, isolationLevel);
        }
        if (readOnlyFlag != null) {
            WebSphereDataSourceAdapter.invokeJdbcMethod(this.setReadOnlyMethod, connSpec, readOnlyFlag);
        }
        if (StringUtils.hasLength((String)username)) {
            WebSphereDataSourceAdapter.invokeJdbcMethod(this.setUserNameMethod, connSpec, username);
            WebSphereDataSourceAdapter.invokeJdbcMethod(this.setPasswordMethod, connSpec, password);
        }
        return connSpec;
    }

    @Nullable
    private static Object invokeJdbcMethod(Method method, @Nullable Object target, Object ... args) throws SQLException {
        try {
            return method.invoke(target, args);
        }
        catch (IllegalAccessException ex) {
            ReflectionUtils.handleReflectionException((Exception)ex);
        }
        catch (InvocationTargetException ex) {
            if (ex.getTargetException() instanceof SQLException) {
                throw (SQLException)ex.getTargetException();
            }
            ReflectionUtils.handleInvocationTargetException((InvocationTargetException)ex);
        }
        throw new IllegalStateException("Should never get here");
    }
}

