/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.osgi.framework.Bundle
 *  org.springframework.util.ObjectUtils
 */
package org.eclipse.gemini.blueprint.service.importer.support.internal.aop;

import java.util.Map;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.eclipse.gemini.blueprint.context.support.internal.classloader.ClassLoaderFactory;
import org.eclipse.gemini.blueprint.service.importer.ImportedOsgiServiceProxy;
import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.eclipse.gemini.blueprint.util.internal.PrivilegedUtils;
import org.osgi.framework.Bundle;
import org.springframework.util.ObjectUtils;

public class ServiceProviderTCCLInterceptor
implements MethodInterceptor {
    private static final int hashCode = ServiceProviderTCCLInterceptor.class.hashCode() * 13;
    private final Object lock = new Object();
    private Bundle serviceBundle;
    private ClassLoader serviceClassLoader;

    public Object invoke(MethodInvocation invocation) throws Throwable {
        if (System.getSecurityManager() != null) {
            return this.invokePrivileged(invocation);
        }
        return this.invokeUnprivileged(invocation);
    }

    private Object invokePrivileged(final MethodInvocation invocation) throws Throwable {
        return PrivilegedUtils.executeWithCustomTCCL(this.getServiceProvidedClassLoader(), new PrivilegedUtils.UnprivilegedThrowableExecution(){

            public Object run() throws Throwable {
                return invocation.proceed();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private Object invokeUnprivileged(MethodInvocation invocation) throws Throwable {
        ClassLoader current = this.getServiceProvidedClassLoader();
        ClassLoader previous = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(current);
            Object object = invocation.proceed();
            return object;
        }
        finally {
            Thread.currentThread().setContextClassLoader(previous);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private ClassLoader getServiceProvidedClassLoader() {
        Object object = this.lock;
        synchronized (object) {
            return this.serviceClassLoader;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void setServiceProvidedClassLoader(Bundle serviceBundle) {
        Object object = this.lock;
        synchronized (object) {
            this.serviceBundle = serviceBundle;
            this.serviceClassLoader = serviceBundle != null ? ClassLoaderFactory.getBundleClassLoaderFor(serviceBundle) : null;
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof ServiceProviderTCCLInterceptor) {
            ServiceProviderTCCLInterceptor oth = (ServiceProviderTCCLInterceptor)other;
            return ObjectUtils.nullSafeEquals((Object)this.serviceBundle, (Object)oth.serviceBundle);
        }
        return false;
    }

    public int hashCode() {
        return hashCode;
    }

    public class ServiceProviderTCCLListener
    implements OsgiServiceLifecycleListener {
        @Override
        public void bind(Object service, Map properties) throws Exception {
            if (service instanceof ImportedOsgiServiceProxy) {
                ServiceProviderTCCLInterceptor.this.setServiceProvidedClassLoader(((ImportedOsgiServiceProxy)service).getServiceReference().getBundle());
            }
        }

        @Override
        public void unbind(Object service, Map properties) throws Exception {
        }
    }
}

