/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import javax.sql.XAConnection;
import javax.sql.XADataSource;

public class XADataSourceProxyHandler
extends JdbcProxyHandler<XADataSource> {
    public XADataSourceProxyHandler(XADataSource delegate, String name, JdbcProxyFactory proxyFactory) {
        super(delegate, XADataSource.class, name, proxyFactory, null);
    }

    @Override
    protected Object invoke(MethodInvocation<XADataSource> methodInvocation) throws Throwable {
        String methodName = methodInvocation.getMethodName();
        Object result = methodName.equals("getXAConnection") ? this.getXAConnection(methodInvocation) : methodInvocation.proceed();
        return result;
    }

    private XAConnection getXAConnection(MethodInvocation<XADataSource> methodInvocation) throws Throwable {
        XAConnection connection = (XAConnection)methodInvocation.proceed();
        connection = this.proxyFactory.wrapXAConnection(this.name, connection);
        return connection;
    }
}

