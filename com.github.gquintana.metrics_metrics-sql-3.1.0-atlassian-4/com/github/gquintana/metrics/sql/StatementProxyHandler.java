/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.sql.AbstractStatementProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import com.github.gquintana.metrics.sql.StatementTimerContext;
import java.sql.Statement;

public class StatementProxyHandler
extends AbstractStatementProxyHandler<Statement> {
    public StatementProxyHandler(Statement delegate, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
        super(delegate, Statement.class, name, proxyFactory, lifeTimerContext);
    }

    @Override
    protected Object execute(MethodInvocation<Statement> methodInvocation) throws Throwable {
        Object result;
        if (methodInvocation.getArgCount() > 0) {
            String sql = methodInvocation.getArgAt(0, String.class);
            StatementTimerContext timerContext = this.proxyFactory.startStatementExecuteTimer(this.name, sql);
            result = methodInvocation.proceed();
            result = this.stopTimer(timerContext, result);
        } else {
            result = methodInvocation.proceed();
        }
        return result;
    }
}

