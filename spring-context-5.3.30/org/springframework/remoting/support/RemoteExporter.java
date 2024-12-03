/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.aop.framework.adapter.AdvisorAdapterRegistry
 *  org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.remoting.support;

import org.aopalliance.aop.Advice;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.adapter.AdvisorAdapterRegistry;
import org.springframework.aop.framework.adapter.GlobalAdvisorAdapterRegistry;
import org.springframework.remoting.support.RemoteInvocationTraceInterceptor;
import org.springframework.remoting.support.RemotingSupport;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class RemoteExporter
extends RemotingSupport {
    private Object service;
    private Class<?> serviceInterface;
    private Boolean registerTraceInterceptor;
    private Object[] interceptors;

    public void setService(Object service) {
        this.service = service;
    }

    public Object getService() {
        return this.service;
    }

    public void setServiceInterface(Class<?> serviceInterface) {
        Assert.notNull(serviceInterface, (String)"'serviceInterface' must not be null");
        Assert.isTrue((boolean)serviceInterface.isInterface(), (String)"'serviceInterface' must be an interface");
        this.serviceInterface = serviceInterface;
    }

    public Class<?> getServiceInterface() {
        return this.serviceInterface;
    }

    public void setRegisterTraceInterceptor(boolean registerTraceInterceptor) {
        this.registerTraceInterceptor = registerTraceInterceptor;
    }

    public void setInterceptors(Object[] interceptors) {
        this.interceptors = interceptors;
    }

    protected void checkService() throws IllegalArgumentException {
        Assert.notNull((Object)this.getService(), (String)"Property 'service' is required");
    }

    protected void checkServiceInterface() throws IllegalArgumentException {
        Class<?> serviceInterface = this.getServiceInterface();
        Assert.notNull(serviceInterface, (String)"Property 'serviceInterface' is required");
        Object service = this.getService();
        if (service instanceof String) {
            throw new IllegalArgumentException("Service [" + service + "] is a String rather than an actual service reference: Have you accidentally specified the service bean name as value instead of as reference?");
        }
        if (!serviceInterface.isInstance(service)) {
            throw new IllegalArgumentException("Service interface [" + serviceInterface.getName() + "] needs to be implemented by service [" + service + "] of class [" + service.getClass().getName() + "]");
        }
    }

    protected Object getProxyForService() {
        this.checkService();
        this.checkServiceInterface();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addInterface(this.getServiceInterface());
        if (this.registerTraceInterceptor != null ? this.registerTraceInterceptor != false : this.interceptors == null) {
            proxyFactory.addAdvice((Advice)new RemoteInvocationTraceInterceptor(this.getExporterName()));
        }
        if (this.interceptors != null) {
            AdvisorAdapterRegistry adapterRegistry = GlobalAdvisorAdapterRegistry.getInstance();
            for (Object interceptor : this.interceptors) {
                proxyFactory.addAdvisor(adapterRegistry.wrap(interceptor));
            }
        }
        proxyFactory.setTarget(this.getService());
        proxyFactory.setOpaque(true);
        return proxyFactory.getProxy(this.getBeanClassLoader());
    }

    protected String getExporterName() {
        return ClassUtils.getShortName(this.getClass());
    }
}

