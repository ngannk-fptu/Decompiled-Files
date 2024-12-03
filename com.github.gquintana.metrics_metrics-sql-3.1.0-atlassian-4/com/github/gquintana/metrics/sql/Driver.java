/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.github.gquintana.metrics.proxy.ProxyFactory;
import com.github.gquintana.metrics.sql.DriverUrl;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.MetricNamingStrategy;
import java.lang.reflect.Constructor;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

public class Driver
implements java.sql.Driver {
    private static final Driver INSTANCE = new Driver();
    private static boolean registered = false;
    private final Logger parentLogger = Logger.getLogger("com.github.gquintana.metrics");

    private static synchronized void register() {
        try {
            if (!registered) {
                registered = true;
                DriverManager.registerDriver(INSTANCE);
            }
        }
        catch (SQLException e) {
            throw new IllegalStateException(e);
        }
    }

    private static <T> T newInstance(Class<T> clazz, Object ... params) throws SQLException {
        try {
            if (params == null || params.length == 0) {
                return clazz.newInstance();
            }
            for (Constructor<?> ctor : clazz.getConstructors()) {
                if (ctor.getParameterTypes().length != params.length) continue;
                int paramIndex = 0;
                for (Class<?> paramType : ctor.getParameterTypes()) {
                    if (!paramType.isInstance(params[paramIndex])) break;
                    ++paramIndex;
                }
                if (paramIndex != params.length) continue;
                return clazz.cast(ctor.newInstance(params));
            }
            throw new SQLException("Constructor not found for " + clazz);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            throw new SQLException(reflectiveOperationException);
        }
    }

    @Override
    public Connection connect(String url, Properties info) throws SQLException {
        if (!this.acceptsURL(url)) {
            return null;
        }
        DriverUrl driverUrl = DriverUrl.parse(url);
        Class<? extends Driver> driverClass = driverUrl.getDriverClass();
        Connection rawConnection = DriverManager.getConnection(driverUrl.getCleanUrl(), info);
        ProxyFactory factory = Driver.newInstance(driverUrl.getProxyFactoryClass(), new Object[0]);
        MetricNamingStrategy namingStrategy = Driver.newInstance(driverUrl.getNamingStrategyClass(), new Object[0]);
        JdbcProxyFactory proxyFactory = new JdbcProxyFactory(namingStrategy, factory);
        return proxyFactory.wrapConnection(driverUrl.getName(), rawConnection);
    }

    @Override
    public boolean acceptsURL(String url) throws SQLException {
        return url != null && url.startsWith("jdbc:metrics:");
    }

    @Override
    public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
        DriverUrl driverUrl = DriverUrl.parse(url);
        java.sql.Driver driver = DriverManager.getDriver(driverUrl.getCleanUrl());
        return driver.getPropertyInfo(driverUrl.getCleanUrl(), info);
    }

    @Override
    public int getMajorVersion() {
        return 3;
    }

    @Override
    public int getMinorVersion() {
        return 1;
    }

    @Override
    public boolean jdbcCompliant() {
        return true;
    }

    @Override
    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return this.parentLogger;
    }

    static {
        Driver.register();
    }
}

