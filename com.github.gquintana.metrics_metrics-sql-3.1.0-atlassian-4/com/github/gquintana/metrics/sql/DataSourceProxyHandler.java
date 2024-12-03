/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import java.sql.Connection;
import javax.sql.DataSource;

public class DataSourceProxyHandler
extends JdbcProxyHandler<DataSource> {
    public DataSourceProxyHandler(DataSource delegate, String name, JdbcProxyFactory proxyFactory) {
        super(delegate, DataSource.class, name, proxyFactory, null);
    }

    @Override
    protected Object invoke(MethodInvocation<DataSource> methodInvocation) throws Throwable {
        String methodName = methodInvocation.getMethodName();
        Object result = methodName.equals("getConnection") ? this.getConnection(methodInvocation) : methodInvocation.proceed();
        return result;
    }

    private Connection getConnection(MethodInvocation<DataSource> methodInvocation) throws Throwable {
        Connection connection = (Connection)methodInvocation.proceed();
        connection = this.proxyFactory.wrapConnection(this.name, connection);
        return connection;
    }
}

