/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.util.concurrent.LazyReference
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.confluence.impl.osgi;

import com.atlassian.confluence.impl.osgi.OsgiNoServiceAvailableException;
import com.atlassian.util.concurrent.LazyReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.function.Supplier;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiProxyFactory {
    public static <T> T createProxy(Class<T> interfaceType, final Supplier<ServiceTracker> serviceTrackerSupplier) {
        return new ServiceTrackingProxy<T>((LazyReference<ServiceTracker>)new LazyReference<ServiceTracker>(){

            protected ServiceTracker create() {
                return (ServiceTracker)serviceTrackerSupplier.get();
            }
        }, interfaceType).proxy;
    }

    private static final class ServiceTrackingProxy<T>
    implements InvocationHandler {
        final LazyReference<ServiceTracker> serviceTrackerSupplier;
        final T proxy;
        final String className;

        private ServiceTrackingProxy(LazyReference<ServiceTracker> serviceTrackerSupplier, Class<T> serviceInterface) {
            this.serviceTrackerSupplier = serviceTrackerSupplier;
            this.className = serviceInterface.getName();
            this.proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[]{serviceInterface}, (InvocationHandler)this);
        }

        private ServiceTracker getServiceTracker() {
            return (ServiceTracker)this.serviceTrackerSupplier.get();
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Method objectHashCode = Object.class.getMethod("hashCode", new Class[0]);
            Method objectEquals = Object.class.getMethod("equals", Object.class);
            if (objectHashCode.equals(method)) {
                return this.hashCode();
            }
            if (objectEquals.equals(method)) {
                try {
                    return method.invoke((Object)this, args);
                }
                catch (InvocationTargetException e) {
                    throw e.getCause();
                }
            }
            Object rawService = this.getServiceTracker().getService();
            if (rawService == null) {
                throw new OsgiNoServiceAvailableException(this.className);
            }
            try {
                return method.invoke(rawService, args);
            }
            catch (InvocationTargetException e) {
                throw e.getCause();
            }
        }
    }
}

