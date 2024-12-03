/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.hibernate.engine.jdbc.connections.spi.ConnectionProvider
 *  org.springframework.aop.Advisor
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor
 */
package com.atlassian.confluence.impl.core.persistence.hibernate;

import com.atlassian.confluence.impl.core.persistence.hibernate.ExceptionMonitor;
import com.atlassian.confluence.impl.profiling.ThreadLocalMethodHooks;
import java.lang.reflect.Method;
import org.aopalliance.aop.Advice;
import org.hibernate.engine.jdbc.connections.spi.ConnectionProvider;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.StaticMethodMatcherPointcutAdvisor;

final class MonitoringConnectionProviderFactory {
    private final ExceptionMonitor exceptionMonitor;
    private final Method getConnection;

    MonitoringConnectionProviderFactory(ExceptionMonitor exceptionMonitor) throws NoSuchMethodException {
        this.exceptionMonitor = exceptionMonitor;
        this.getConnection = ConnectionProvider.class.getMethod("getConnection", new Class[0]);
    }

    ConnectionProvider proxy(ConnectionProvider connectionProvider) {
        return MonitoringConnectionProviderFactory.createProxy(connectionProvider, this.createMethodAdvisor(this.getConnection, (Advice)this.exceptionMonitor.exceptionCapturingAdvice()), this.createMethodAdvisor(this.getConnection, (Advice)ThreadLocalMethodHooks.advice()));
    }

    private Advisor createMethodAdvisor(final Method targetMethod, Advice advice) {
        return new StaticMethodMatcherPointcutAdvisor(advice){

            public boolean matches(Method method, Class<?> targetClass) {
                return method.equals(targetMethod);
            }
        };
    }

    private static <T> T createProxy(T target, Advisor ... advisors) {
        ProxyFactory proxyFactory = new ProxyFactory(target);
        for (Advisor advisor : advisors) {
            proxyFactory.addAdvisor(advisor);
        }
        return (T)proxyFactory.getProxy();
    }
}

