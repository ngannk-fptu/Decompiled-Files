/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.spring.interceptor.spi.ExportableInterceptor
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.osgi.framework.BundleContext
 *  org.osgi.framework.InvalidSyntaxException
 *  org.osgi.framework.ServiceEvent
 *  org.osgi.framework.ServiceListener
 *  org.osgi.framework.ServiceReference
 *  org.osgi.framework.ServiceRegistration
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.plugins.spring.interceptor.plugin;

import com.atlassian.plugins.spring.interceptor.plugin.ExportableInterceptorAdapter;
import com.atlassian.plugins.spring.interceptor.spi.ExportableInterceptor;
import java.util.Hashtable;
import java.util.concurrent.ConcurrentHashMap;
import org.aopalliance.intercept.MethodInterceptor;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InterceptorRegistry
implements ServiceListener {
    private static final Logger log = LoggerFactory.getLogger(InterceptorRegistry.class);
    public static final String FILTER_STRING = "(objectclass=" + ExportableInterceptor.class.getName() + ")";
    public static final String BEAN_NAME_PREFIX = "exported";
    private final BundleContext bundleContext;
    private final ConcurrentHashMap<Long, ServiceRegistration> registrations = new ConcurrentHashMap();

    public InterceptorRegistry(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void start() {
        try {
            ServiceReference[] existingReferences;
            this.bundleContext.addServiceListener((ServiceListener)this, FILTER_STRING);
            for (ServiceReference reference : existingReferences = this.bundleContext.getServiceReferences(null, FILTER_STRING)) {
                this.register(reference);
            }
        }
        catch (InvalidSyntaxException e) {
            throw new RuntimeException("Unable to start registry, " + FILTER_STRING + " is an invalid filter syntax. " + (Object)((Object)e), e);
        }
    }

    public void stop() {
        this.bundleContext.removeServiceListener((ServiceListener)this);
    }

    public void serviceChanged(ServiceEvent event) {
        switch (event.getType()) {
            case 1: {
                this.register(event.getServiceReference());
                break;
            }
            case 2: {
                this.unregister(event.getServiceReference());
                this.register(event.getServiceReference());
                break;
            }
            case 4: {
                this.unregister(event.getServiceReference());
            }
        }
    }

    private void unregister(ServiceReference serviceReference) {
        Long serviceId = (Long)serviceReference.getProperty("service.id");
        ServiceRegistration previousRegistration = this.registrations.remove(serviceId);
        log.info("InterceptorRegistry#unregister: unregistering " + previousRegistration);
        if (previousRegistration != null) {
            previousRegistration.unregister();
        }
    }

    private void register(ServiceReference serviceReference) {
        Long serviceId = (Long)serviceReference.getProperty("service.id");
        Object service = this.bundleContext.getService(serviceReference);
        if (service == null) {
            log.warn("InterceptorRegistry#register: unable to locate service for reference: " + serviceReference);
        } else if (service instanceof ExportableInterceptor) {
            ExportableInterceptor exportableInterceptor = (ExportableInterceptor)service;
            Object beanName = serviceReference.getProperty("bean-name");
            if (!(beanName instanceof String) || !((String)beanName).startsWith(BEAN_NAME_PREFIX) || ((String)beanName).length() <= BEAN_NAME_PREFIX.length()) {
                log.warn("InterceptorRegistry#register: can only export interceptors with String bean-name property starting with 'exported'. Bean name was: " + beanName);
                return;
            }
            Hashtable<String, String> properties = new Hashtable<String, String>();
            properties.put("bean-name", this.makeBeanName((String)beanName));
            log.info("Registering method interceptor with bean name " + properties.get("bean-name"));
            ServiceRegistration newRegistration = this.bundleContext.registerService(MethodInterceptor.class.getName(), (Object)new ExportableInterceptorAdapter(exportableInterceptor), properties);
            ServiceRegistration existingRegistration = this.registrations.putIfAbsent(serviceId, newRegistration);
            if (existingRegistration != null) {
                log.warn("InterceptorRegistry#register: interceptor already registered for ID: " + serviceId + ". Unregistering duplicate service");
                newRegistration.unregister();
            }
        } else {
            log.warn("InterceptorRegistry#register: expected class ExportableInterceptor, was : " + service.getClass());
        }
    }

    private String makeBeanName(String beanName) {
        int prefixIndex = BEAN_NAME_PREFIX.length();
        char firstLetter = Character.toLowerCase(beanName.charAt(prefixIndex));
        if (beanName.length() == prefixIndex + 1) {
            return Character.toString(firstLetter);
        }
        return Character.toString(firstLetter) + beanName.substring(prefixIndex + 1);
    }
}

