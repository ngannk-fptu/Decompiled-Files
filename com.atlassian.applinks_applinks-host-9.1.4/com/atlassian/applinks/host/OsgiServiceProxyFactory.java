/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.OsgiContainerManager
 *  com.google.common.cache.CacheBuilder
 *  com.google.common.cache.CacheLoader
 *  com.google.common.cache.LoadingCache
 *  org.osgi.util.tracker.ServiceTracker
 */
package com.atlassian.applinks.host;

import com.atlassian.plugin.osgi.container.OsgiContainerManager;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.osgi.util.tracker.ServiceTracker;

public class OsgiServiceProxyFactory {
    private final LoadingCache<Class<?>, ServiceTracker> serviceTrackers;

    public OsgiServiceProxyFactory(final OsgiContainerManager osgiContainerManager) {
        this(new ServiceTrackerFactory(){

            @Override
            public ServiceTracker create(String name) {
                return osgiContainerManager.getServiceTracker(name);
            }
        });
    }

    public OsgiServiceProxyFactory(final ServiceTrackerFactory serviceTrackerFactory) {
        this.serviceTrackers = CacheBuilder.newBuilder().build(new CacheLoader<Class<?>, ServiceTracker>(){

            public ServiceTracker load(Class<?> key) throws Exception {
                return serviceTrackerFactory.create(key.getName());
            }
        });
    }

    public <T> T createProxy(Class<T> apiClass, long timeoutInMillis) {
        return (T)Proxy.newProxyInstance(apiClass.getClassLoader(), new Class[]{apiClass}, (InvocationHandler)new DynamicServiceInvocationHandler(this.serviceTrackers, apiClass, timeoutInMillis));
    }

    static class DynamicServiceInvocationHandler
    implements InvocationHandler {
        private final LoadingCache<Class<?>, ServiceTracker> serviceTrackers;
        private final Class clazz;
        private final long timeoutInMillis;

        DynamicServiceInvocationHandler(LoadingCache<Class<?>, ServiceTracker> serviceTrackers, Class clazz, long timeoutInMillis) {
            this.serviceTrackers = serviceTrackers;
            this.clazz = clazz;
            this.timeoutInMillis = timeoutInMillis;
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
            try {
                Object service = ((ServiceTracker)this.serviceTrackers.get((Object)this.clazz)).waitForService(this.timeoutInMillis);
                if (service == null) {
                    throw new ServiceTimeoutExceeded("Timeout exceeded waiting for service - " + this.clazz.getName());
                }
                Thread.currentThread().setContextClassLoader(service.getClass().getClassLoader());
                Object object = method.invoke(service, objects);
                return object;
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
            finally {
                Thread.currentThread().setContextClassLoader(oldContextClassLoader);
            }
        }
    }

    public static class ServiceTimeoutExceeded
    extends RuntimeException {
        public ServiceTimeoutExceeded(String message) {
            super(message);
        }
    }

    public static interface ServiceTrackerFactory {
        public ServiceTracker create(String var1);
    }
}

