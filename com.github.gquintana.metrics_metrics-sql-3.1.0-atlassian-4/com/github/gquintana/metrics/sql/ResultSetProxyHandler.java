/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import java.sql.ResultSet;

public class ResultSetProxyHandler<T extends ResultSet>
extends JdbcProxyHandler<T> {
    private static final ProxyHandler.InvocationFilter THIS_INVOCATION_FILTER = new ProxyHandler.MethodNamesInvocationFilter("isWrapperFor", "unwrap", "close");

    public ResultSetProxyHandler(T delegate, Class<T> delegateType, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
        super(delegate, delegateType, name, proxyFactory, lifeTimerContext);
    }

    @Override
    protected Object invoke(MethodInvocation<T> delegatingMethodInvocation) throws Throwable {
        Object result;
        String methodName;
        switch (methodName = delegatingMethodInvocation.getMethodName()) {
            case "isWrapperFor": {
                result = this.isWrapperFor(delegatingMethodInvocation);
                break;
            }
            case "unwrap": {
                result = this.unwrap(delegatingMethodInvocation);
                break;
            }
            case "close": {
                result = this.close(delegatingMethodInvocation);
                break;
            }
            default: {
                result = delegatingMethodInvocation.proceed();
            }
        }
        return result;
    }

    @Override
    public ProxyHandler.InvocationFilter getInvocationFilter() {
        return THIS_INVOCATION_FILTER;
    }
}

