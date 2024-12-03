/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.hostcontainer.HostContainer
 *  com.atlassian.plugin.util.ContextClassLoaderSettingInvocationHandler
 *  com.google.common.collect.Maps
 *  org.osgi.framework.Bundle
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.ServiceFactory
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugin.osgi.hostcomponents.impl;

import com.atlassian.plugin.hostcontainer.HostContainer;
import com.atlassian.plugin.osgi.hostcomponents.ComponentRegistrar;
import com.atlassian.plugin.osgi.hostcomponents.ContextClassLoaderStrategy;
import com.atlassian.plugin.osgi.hostcomponents.HostComponentRegistration;
import com.atlassian.plugin.osgi.hostcomponents.InstanceBuilder;
import com.atlassian.plugin.osgi.hostcomponents.impl.CallingBundleStore;
import com.atlassian.plugin.osgi.hostcomponents.impl.DefaultInstanceBuilder;
import com.atlassian.plugin.osgi.hostcomponents.impl.Registration;
import com.atlassian.plugin.util.ContextClassLoaderSettingInvocationHandler;
import com.google.common.collect.Maps;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceFactory;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultComponentRegistrar
implements ComponentRegistrar {
    private static final Logger log = LoggerFactory.getLogger(DefaultComponentRegistrar.class);
    private final List<HostComponentRegistration> registry = new CopyOnWriteArrayList<HostComponentRegistration>();

    @Override
    public InstanceBuilder register(Class<?> ... mainInterfaces) {
        Registration reg = new Registration(mainInterfaces);
        this.registry.add(reg);
        return new DefaultInstanceBuilder(reg);
    }

    public List<ServiceRegistration> writeRegistry(BundleContext ctx) {
        ArrayList<ServiceRegistration> services = new ArrayList<ServiceRegistration>();
        for (HostComponentRegistration reg : new ArrayList<HostComponentRegistration>(this.registry)) {
            ServiceRegistration sreg;
            if (Arrays.asList(reg.getMainInterfaceClasses()).contains(HostContainer.class)) {
                log.warn("Cannot register a HostContainer as a host component, skipping");
                this.registry.remove(reg);
                continue;
            }
            String[] names = reg.getMainInterfaces();
            reg.getProperties().put("plugins-host", Boolean.TRUE.toString());
            String beanName = reg.getProperties().get("bean-name");
            if (beanName == null) {
                String genKey = String.valueOf(Arrays.asList(reg.getMainInterfaces()).hashCode());
                reg.getProperties().put("bean-name", "hostComponent-" + genKey);
            }
            if (log.isDebugEnabled()) {
                log.debug("Registering: {} instance {} with properties: {}", new Object[]{Arrays.asList(names), reg.getInstance(), reg.getProperties()});
            }
            if (names.length == 0) {
                log.warn("Host component {} of instance {} has no interfaces", (Object)beanName, reg.getInstance());
            }
            Object service = reg.getInstance();
            if (!ContextClassLoaderStrategy.USE_PLUGIN.name().equals(reg.getProperties().get("context-class-loader-strategy"))) {
                service = this.createContextClassLoaderSettingProxy(reg.getMainInterfaceClasses(), service);
            }
            if (Boolean.parseBoolean(reg.getProperties().get("track-bundle"))) {
                service = this.createTrackBundleProxy(reg.getMainInterfaceClasses(), service);
            }
            if ((sreg = ctx.registerService(names, service, reg.getProperties())) == null) continue;
            services.add(sreg);
        }
        return Collections.unmodifiableList(services);
    }

    public List<HostComponentRegistration> getRegistry() {
        return Collections.unmodifiableList(this.registry);
    }

    private Object createContextClassLoaderSettingProxy(Class<?>[] interfaces, Object service) {
        final Function<Object, Object> transformer = service1 -> Proxy.newProxyInstance(DefaultComponentRegistrar.class.getClassLoader(), interfaces, (InvocationHandler)new ContextClassLoaderSettingInvocationHandler(service1));
        if (!(service instanceof ServiceFactory)) {
            return transformer.apply(service);
        }
        return new TransformingServiceFactory((ServiceFactory)service){

            @Override
            protected Object transform(Bundle bundle, ServiceRegistration registration, Object service) {
                return transformer.apply(service);
            }
        };
    }

    private ServiceFactory createTrackBundleProxy(final Class<?>[] interfaces, Object service) {
        ServiceFactory delegate = service instanceof ServiceFactory ? (ServiceFactory)service : new InstanceServiceFactory(service);
        return new TransformingServiceFactory(delegate){

            @Override
            protected Object transform(Bundle bundle, ServiceRegistration registration, Object service) {
                return Proxy.newProxyInstance(DefaultComponentRegistrar.class.getClassLoader(), interfaces, (InvocationHandler)new BundleTrackingInvocationHandler(bundle, service));
            }
        };
    }

    private static abstract class TransformingServiceFactory
    implements ServiceFactory {
        private final ServiceFactory delegate;
        private final Map<Long, Object> bundleIdToOriginalService;

        private TransformingServiceFactory(ServiceFactory delegate) {
            this.delegate = delegate;
            this.bundleIdToOriginalService = Maps.newConcurrentMap();
        }

        public final Object getService(Bundle bundle, ServiceRegistration registration) {
            Object service = this.delegate.getService(bundle, registration);
            Object transformed = this.transform(bundle, registration, service);
            this.bundleIdToOriginalService.put(bundle.getBundleId(), service);
            return transformed;
        }

        public final void ungetService(Bundle bundle, ServiceRegistration registration, Object transformed) {
            Object service = this.bundleIdToOriginalService.remove(bundle.getBundleId());
            if (service != null) {
                this.delegate.ungetService(bundle, registration, service);
            }
        }

        protected abstract Object transform(Bundle var1, ServiceRegistration var2, Object var3);
    }

    private static class InstanceServiceFactory
    implements ServiceFactory {
        private final Object service;

        private InstanceServiceFactory(Object service) {
            this.service = service;
        }

        public Object getService(Bundle bundle, ServiceRegistration registration) {
            return this.service;
        }

        public void ungetService(Bundle bundle, ServiceRegistration registration, Object service) {
        }
    }

    private static class BundleTrackingInvocationHandler
    implements InvocationHandler {
        private final Bundle bundle;
        private final Object service;

        private BundleTrackingInvocationHandler(Bundle bundle, Object service) {
            this.bundle = bundle;
            this.service = service;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            Bundle original = CallingBundleStore.get();
            try {
                CallingBundleStore.set(this.bundle);
                Object object = method.invoke(this.service, args);
                return object;
            }
            finally {
                CallingBundleStore.set(original);
            }
        }
    }
}

