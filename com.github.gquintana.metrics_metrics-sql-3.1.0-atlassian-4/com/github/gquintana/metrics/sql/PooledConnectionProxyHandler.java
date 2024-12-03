/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import java.sql.Connection;
import javax.sql.PooledConnection;

public class PooledConnectionProxyHandler<T extends PooledConnection>
extends JdbcProxyHandler<T> {
    public PooledConnectionProxyHandler(T delegate, Class<T> delegateType, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
        super(delegate, delegateType, name, proxyFactory, lifeTimerContext);
    }

    @Override
    protected Object invoke(MethodInvocation<T> methodInvocation) throws Throwable {
        String methodName = methodInvocation.getMethodName();
        Object result = methodName.equals("getConnection") ? this.getConnection(methodInvocation) : methodInvocation.proceed();
        return result;
    }

    private Connection getConnection(MethodInvocation<T> methodInvocation) throws Throwable {
        Connection connection = (Connection)methodInvocation.proceed();
        connection = this.proxyFactory.wrapConnection(this.name, connection);
        return connection;
    }
}

