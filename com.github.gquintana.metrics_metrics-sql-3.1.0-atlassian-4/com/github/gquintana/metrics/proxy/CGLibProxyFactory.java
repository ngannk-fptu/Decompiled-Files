/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.cglib.proxy.Callback
 *  net.sf.cglib.proxy.CallbackFilter
 *  net.sf.cglib.proxy.Enhancer
 *  net.sf.cglib.proxy.Factory
 *  net.sf.cglib.proxy.LazyLoader
 *  net.sf.cglib.proxy.MethodInterceptor
 *  net.sf.cglib.proxy.MethodProxy
 */
package com.github.gquintana.metrics.proxy;

import com.github.gquintana.metrics.proxy.AbstractProxyFactory;
import com.github.gquintana.metrics.proxy.ProxyClass;
import com.github.gquintana.metrics.proxy.ProxyException;
import com.github.gquintana.metrics.proxy.ProxyHandler;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.CallbackFilter;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.Factory;
import net.sf.cglib.proxy.LazyLoader;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

public class CGLibProxyFactory
extends AbstractProxyFactory {
    private final Map<ProxyClass, Class> proxyClasses = new ConcurrentHashMap<ProxyClass, Class>();
    private static final Class[] ADAPTER_CALLBACK_TYPES = new Class[]{AdapterMethodInterceptor.class, AdapterLazyLoader.class};

    private Class getProxyClass(ProxyHandler<?> proxyHandler, ProxyClass proxyClass) {
        Class clazz = this.proxyClasses.get(proxyClass);
        if (clazz == null) {
            Enhancer enhancer = new Enhancer();
            enhancer.setCallbackFilter((CallbackFilter)new AdapterCallbackFilter(proxyHandler.getInvocationFilter()));
            enhancer.setCallbackTypes(ADAPTER_CALLBACK_TYPES);
            enhancer.setClassLoader(proxyClass.getClassLoader());
            enhancer.setInterfaces((Class[])proxyClass.getInterfaces());
            clazz = enhancer.createClass();
            this.proxyClasses.put(proxyClass, clazz);
        }
        return clazz;
    }

    @Override
    public <T> T newProxy(ProxyHandler<T> proxyHandler, ProxyClass proxyClass) {
        try {
            Object proxy = this.getProxyClass(proxyHandler, proxyClass).newInstance();
            ((Factory)proxy).setCallbacks(new Callback[]{new AdapterMethodInterceptor(proxyHandler), new AdapterLazyLoader(proxyHandler.getDelegate())});
            return proxy;
        }
        catch (ReflectiveOperationException e) {
            throw new ProxyException(e);
        }
    }

    private static class AdapterLazyLoader<T>
    implements LazyLoader {
        private final T delegate;

        private AdapterLazyLoader(T delegate) {
            this.delegate = delegate;
        }

        public T loadObject() {
            return this.delegate;
        }
    }

    private static class AdapterMethodInterceptor
    implements MethodInterceptor {
        private final ProxyHandler proxyHandler;

        private AdapterMethodInterceptor(ProxyHandler proxyHandler) {
            this.proxyHandler = proxyHandler;
        }

        public Object intercept(Object proxy, Method method, Object[] arguments, MethodProxy methodProxy) throws Throwable {
            return this.proxyHandler.invoke(proxy, method, arguments);
        }
    }

    private static class AdapterCallbackFilter
    implements CallbackFilter {
        private final ProxyHandler.InvocationFilter invocationFilter;

        private AdapterCallbackFilter(ProxyHandler.InvocationFilter invocationFilter) {
            this.invocationFilter = invocationFilter;
        }

        public int accept(Method method) {
            return this.invocationFilter.isIntercepted(method) ? 0 : 1;
        }
    }
}

