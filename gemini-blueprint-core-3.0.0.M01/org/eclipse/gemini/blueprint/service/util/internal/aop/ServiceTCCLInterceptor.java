/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.util.internal.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.gemini.blueprint.util.internal.PrivilegedUtils;
import org.springframework.util.ObjectUtils;

public class ServiceTCCLInterceptor
implements MethodInterceptor {
    private static final int hashCode = ServiceTCCLInterceptor.class.hashCode() * 13;
    private final ClassLoader loader;

    public ServiceTCCLInterceptor(ClassLoader loader) {
        this.loader = loader;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (System.getSecurityManager() != null) {
            return this.invokePrivileged(invocation);
        }
        return this.invokeUnprivileged(invocation);
    }

    private Object invokePrivileged(final MethodInvocation invocation) throws Throwable {
        return PrivilegedUtils.executeWithCustomTCCL(this.loader, new PrivilegedUtils.UnprivilegedThrowableExecution(){

            public Object run() throws Throwable {
                return invocation.proceed();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object invokeUnprivileged(MethodInvocation invocation) throws Throwable {
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(this.loader);
            Object object = invocation.proceed();
            return object;
        }
        finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ServiceTCCLInterceptor) {
            ServiceTCCLInterceptor oth = (ServiceTCCLInterceptor)other;
            return ObjectUtils.nullSafeEquals((Object)this.loader, (Object)oth.loader);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }
}

