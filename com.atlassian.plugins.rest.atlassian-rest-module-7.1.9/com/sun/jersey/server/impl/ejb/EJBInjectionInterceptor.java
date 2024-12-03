/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ManagedBean
 *  javax.annotation.PostConstruct
 *  javax.interceptor.InvocationContext
 */
package com.sun.jersey.server.impl.ejb;

import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessor;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProcessorFactory;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.interceptor.InvocationContext;
import javax.ws.rs.ext.Provider;

final class EJBInjectionInterceptor {
    private IoCComponentProcessorFactory cpf;
    private final ConcurrentMap<Class, IoCComponentProcessor> componentProcessorMap = new ConcurrentHashMap<Class, IoCComponentProcessor>();
    private final AtomicBoolean initializing = new AtomicBoolean(false);
    private static final IoCComponentProcessor NULL_COMPONENT_PROCESSOR = new IoCComponentProcessor(){

        @Override
        public void preConstruct() {
        }

        @Override
        public void postConstruct(Object o) {
        }
    };

    EJBInjectionInterceptor() {
    }

    public void setFactory(IoCComponentProcessorFactory cpf) {
        this.cpf = cpf;
    }

    @PostConstruct
    private void init(InvocationContext context) throws Exception {
        if (this.cpf == null) {
            return;
        }
        boolean setInitializing = this.initializing.compareAndSet(false, true);
        if (!setInitializing) {
            context.proceed();
            return;
        }
        Object beanInstance = context.getTarget();
        IoCComponentProcessor icp = this.get(beanInstance.getClass());
        if (icp != null) {
            icp.postConstruct(beanInstance);
        }
        context.proceed();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private IoCComponentProcessor get(Class c) {
        IoCComponentProcessor cp = (IoCComponentProcessor)this.componentProcessorMap.get(c);
        if (cp != null) {
            return cp == NULL_COMPONENT_PROCESSOR ? null : cp;
        }
        ConcurrentMap<Class, IoCComponentProcessor> concurrentMap = this.componentProcessorMap;
        synchronized (concurrentMap) {
            cp = (IoCComponentProcessor)this.componentProcessorMap.get(c);
            if (cp != null) {
                return cp == NULL_COMPONENT_PROCESSOR ? null : cp;
            }
            ComponentScope cs = c.isAnnotationPresent(ManagedBean.class) ? (c.isAnnotationPresent(Provider.class) ? ComponentScope.Singleton : this.cpf.getScope(c)) : ComponentScope.Singleton;
            cp = this.cpf.get(c, cs);
            if (cp != null) {
                this.componentProcessorMap.put(c, cp);
            } else {
                this.componentProcessorMap.put(c, NULL_COMPONENT_PROCESSOR);
            }
        }
        return cp;
    }
}

