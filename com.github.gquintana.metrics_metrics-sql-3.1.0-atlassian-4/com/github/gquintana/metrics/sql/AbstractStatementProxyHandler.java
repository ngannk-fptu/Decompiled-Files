/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.JdbcProxyHandler;
import com.github.gquintana.metrics.sql.StatementTimerContext;
import java.sql.ResultSet;
import java.sql.Statement;

public abstract class AbstractStatementProxyHandler<T extends Statement>
extends JdbcProxyHandler<T> {
    private static final ProxyHandler.InvocationFilter THIS_INVOCATION_FILTER = new ProxyHandler.MethodNamesInvocationFilter("isWrapperFor", "unwrap", "close", "execute", "executeQuery", "executeUpdate");

    public AbstractStatementProxyHandler(T delegate, Class<T> delegateType, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
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
            case "execute": 
            case "executeQuery": 
            case "executeUpdate": {
                result = this.execute(delegatingMethodInvocation);
                break;
            }
            default: {
                result = delegatingMethodInvocation.proceed();
            }
        }
        return result;
    }

    protected abstract Object execute(MethodInvocation<T> var1) throws Throwable;

    @Override
    public ProxyHandler.InvocationFilter getInvocationFilter() {
        return THIS_INVOCATION_FILTER;
    }

    protected Object stopTimer(StatementTimerContext timerContext, Object result) {
        if (timerContext != null) {
            this.stopTimer(timerContext.getTimerContext());
            if (result instanceof ResultSet) {
                result = this.proxyFactory.wrapResultSet(this.name, (ResultSet)result, timerContext.getSql(), timerContext.getSqlId());
            }
        }
        return result;
    }
}

