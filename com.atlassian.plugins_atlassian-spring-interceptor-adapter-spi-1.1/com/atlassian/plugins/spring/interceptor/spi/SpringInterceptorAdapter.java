/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 */
package com.atlassian.plugins.spring.interceptor.spi;

import com.atlassian.plugins.spring.interceptor.spi.ExportableInterceptor;
import com.atlassian.plugins.spring.interceptor.spi.ExportableMethodInvocation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class SpringInterceptorAdapter
implements ExportableInterceptor {
    private final MethodInterceptor delegate;

    public SpringInterceptorAdapter(MethodInterceptor delegate) {
        this.delegate = delegate;
    }

    @Override
    public Object invoke(ExportableMethodInvocation methodInvocation) throws Throwable {
        return this.delegate.invoke((MethodInvocation)new SpringMethodInvocationAdapter(methodInvocation));
    }

    private class SpringMethodInvocationAdapter
    implements MethodInvocation {
        private ExportableMethodInvocation delegate;

        public SpringMethodInvocationAdapter(ExportableMethodInvocation methodInvocation) {
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

