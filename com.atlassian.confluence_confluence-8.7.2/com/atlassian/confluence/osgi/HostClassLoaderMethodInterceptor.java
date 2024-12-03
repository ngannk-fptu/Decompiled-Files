/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 */
package com.atlassian.confluence.osgi;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class HostClassLoaderMethodInterceptor
implements MethodInterceptor {
    private final ClassLoader hostContextClassLoader = this.getClass().getClassLoader();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        Thread thread = Thread.currentThread();
        ClassLoader ccl = thread.getContextClassLoader();
        try {
            thread.setContextClassLoader(this.hostContextClassLoader);
            Object object = methodInvocation.proceed();
            return object;
        }
        finally {
            thread.setContextClassLoader(ccl);
        }
    }
}

