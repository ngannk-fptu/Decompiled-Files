/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.spring.interceptor.spi.ExportableInterceptor
 *  com.atlassian.plugins.spring.interceptor.spi.ExportableMethodInvocation
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 */
package com.atlassian.plugins.spring.interceptor.plugin;

import com.atlassian.plugins.spring.interceptor.spi.ExportableInterceptor;
import com.atlassian.plugins.spring.interceptor.spi.ExportableMethodInvocation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ExportableInterceptorAdapter
implements MethodInterceptor {
    private final ExportableInterceptor delegate;

    public ExportableInterceptorAdapter(ExportableInterceptor delegate) {
        this.delegate = delegate;
    }

    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        return this.delegate.invoke((ExportableMethodInvocation)new WrappedMethodInvocation(methodInvocation));
    }

    private class WrappedMethodInvocation
    implements ExportableMethodInvocation {
        private final MethodInvocation delegate;

        public WrappedMethodInvocation(MethodInvocation methodInvocation) {
            this.delegate = methodInvocation;
        }

        public Method getMethod() {
            return this.delegate.getMethod();
        }

        public Object[] getArguments() {
            return this.delegate.getArguments();
        }

        public Object proceed() throws Throwable {
            return this.delegate.proceed();
        }

        public Object getThis() {
            return this.delegate.getThis();
        }

        public AccessibleObject getStaticPart() {
            return this.delegate.getStaticPart();
        }
    }
}

