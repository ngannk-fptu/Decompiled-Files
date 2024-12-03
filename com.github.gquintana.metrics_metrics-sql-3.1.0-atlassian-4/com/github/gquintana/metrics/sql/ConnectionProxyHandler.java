/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;

public class ConnectionProxyHandler
extends JdbcProxyHandler<Connection> {
    public ConnectionProxyHandler(Connection delegate, String connectionFactoryName, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
        super(delegate, Connection.class, connectionFactoryName, proxyFactory, lifeTimerContext);
    }

    @Override
    protected Object invoke(MethodInvocation<Connection> delegatingMethodInvocation) throws Throwable {
        String methodName = delegatingMethodInvocation.getMethodName();
        Object result = methodName.equals("isWrapperFor") ? this.isWrapperFor(delegatingMethodInvocation) : (methodName.equals("unwrap") ? this.unwrap(delegatingMethodInvocation) : (methodName.equals("close") ? this.close(delegatingMethodInvocation) : (methodName.equals("createStatement") ? this.createStatement(delegatingMethodInvocation) : (methodName.equals("prepareStatement") ? this.prepareStatement(delegatingMethodInvocation) : (methodName.equals("prepareCall") ? this.prepareCall(delegatingMethodInvocation) : delegatingMethodInvocation.proceed())))));
        return result;
    }

    private Statement createStatement(MethodInvocation<Connection> methodInvocation) throws Throwable {
        Statement result = (Statement)methodInvocation.proceed();
        result = this.proxyFactory.wrapStatement(this.name, result);
        return result;
    }

    private PreparedStatement prepareStatement(MethodInvocation<Connection> methodInvocation) throws Throwable {
        PreparedStatement result = (PreparedStatement)methodInvocation.proceed();
        result = this.proxyFactory.wrapPreparedStatement(this.name, result, methodInvocation.getArgAt(0, String.class));
        return result;
    }

    private CallableStatement prepareCall(MethodInvocation<Connection> methodInvocation) throws Throwable {
        CallableStatement result = (CallableStatement)methodInvocation.proceed();
        result = this.proxyFactory.wrapCallableStatement(this.name, result, methodInvocation.getArgAt(0, String.class));
        return result;
    }
}

