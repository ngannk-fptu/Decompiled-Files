/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.eclipse.gemini.blueprint.context.BundleContextAware
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceReference
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.FactoryBean
 *  org.springframework.beans.factory.InitializingBean
 */
package com.atlassian.plugin.osgi.bridge.external;

import com.google.common.base.Preconditions;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import org.eclipse.gemini.blueprint.context.BundleContextAware;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;

public class HostComponentFactoryBean
implements FactoryBean,
InitializingBean,
BundleContextAware {
    private BundleContext bundleContext;
    private String filter;
    private Object service;
    private Class<?>[] interfaces;

    public Object getObject() {
        return this.findService();
    }

    public Class getObjectType() {
        return this.findService() != null ? this.findService().getClass() : null;
    }

    public boolean isSingleton() {
        return true;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public void setInterfaces(Class<?>[] interfaces) {
        this.interfaces = interfaces;
    }

    private Object findService() {
        return this.service;
    }

    private Object createHostComponentProxy() {
        return Proxy.newProxyInstance(this.bundleContext.getClass().getClassLoader(), this.interfaces, (InvocationHandler)new DynamicServiceInvocationHandler(this.bundleContext, this.filter));
    }

    public void afterPropertiesSet() {
        Preconditions.checkNotNull((Object)this.bundleContext);
        Preconditions.checkNotNull(this.interfaces);
        this.service = this.createHostComponentProxy();
    }

    static class DynamicServiceInvocationHandler
    implements InvocationHandler {
        private static final Logger log = LoggerFactory.getLogger(DynamicServiceInvocationHandler.class);
        private volatile Object service;
        private final String filter;

        DynamicServiceInvocationHandler(BundleContext bundleContext, String filter) {
            this.filter = filter;
            try {
                ServiceReference[] refs = bundleContext.getServiceReferences((String)null, filter);
                if (refs != null && refs.length > 0) {
                    this.service = bundleContext.getService(refs[0]);
                }
                bundleContext.addServiceListener(serviceEvent -> {
                    if (1 == serviceEvent.getType()) {
                        if (log.isDebugEnabled()) {
                            log.debug("Updating the host component matching filter: {}", (Object)filter);
                        }
                        this.service = bundleContext.getService(serviceEvent.getServiceReference());
                    }
                }, filter);
            }
            catch (InvalidSyntaxException e) {
                throw new IllegalArgumentException("Invalid filter string: " + filter, e);
            }
        }

        @Override
        public Object invoke(Object o, Method method, Object[] objects) throws Throwable {
            if (this.service == null) {
                throw new IllegalStateException("Unable to locate host component with filter: " + this.filter);
            }
            try {
                return method.invoke(this.service, objects);
            }
            catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }
    }
}

