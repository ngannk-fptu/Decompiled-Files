/*
 * Decompiled with CFR 0.152.
 */
package com.github.gquintana.metrics.sql;

import com.atlassian.instrumentation.instruments.Timer;
import com.github.gquintana.metrics.proxy.MethodInvocation;
import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import com.github.gquintana.metrics.sql.JdbcProxyFactory;
import java.sql.SQLException;
import java.sql.Wrapper;

public abstract class JdbcProxyHandler<T>
extends ProxyHandler<T> {
    private final Class<T> delegateType;
    private final Timer lifeTimerContext;
    protected final String name;
    protected final JdbcProxyFactory proxyFactory;

    protected JdbcProxyHandler(T delegate, Class<T> delegateType, String name, JdbcProxyFactory proxyFactory, Timer lifeTimerContext) {
        super(delegate);
        this.delegateType = delegateType;
        this.proxyFactory = proxyFactory;
        this.name = name;
        this.lifeTimerContext = lifeTimerContext;
    }

    private boolean isDelegateType(Class<?> iface) {
        return this.delegateType.equals(iface);
    }

    private Class getClassArg(MethodInvocation methodInvocation) {
        return methodInvocation.getArgAt(0, Class.class);
    }

    protected Object isWrapperFor(MethodInvocation methodInvocation) throws Throwable {
        Class iface = this.getClassArg(methodInvocation);
        return this.isDelegateType(iface) ? Boolean.valueOf(true) : methodInvocation.proceed();
    }

    protected Object close(MethodInvocation methodInvocation) throws Throwable {
        this.stopTimer(this.lifeTimerContext);
        return methodInvocation.proceed();
    }

    protected final void stopTimer(Timer timerContext) {
        if (timerContext != null) {
            timerContext.stop();
        }
    }

    protected Object unwrap(MethodInvocation<T> methodInvocation) throws SQLException {
        Class iface = this.getClassArg(methodInvocation);
        Wrapper delegateWrapper = (Wrapper)this.delegate;
        Object result = this.isDelegateType(iface) ? (delegateWrapper.isWrapperFor(iface) ? delegateWrapper.unwrap(iface) : iface.cast(delegateWrapper)) : delegateWrapper.unwrap(iface);
        return result;
    }

    public ProxyClass getProxyClass() {
        return new ProxyClass(this.delegate.getClass().getClassLoader(), this.delegateType);
    }

    protected Timer getLifeTimerContext() {
        return this.lifeTimerContext;
    }
}

