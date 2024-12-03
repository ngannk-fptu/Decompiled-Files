/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ManagedBean
 *  javax.enterprise.context.ApplicationScoped
 *  javax.enterprise.context.Dependent
 *  javax.enterprise.context.RequestScoped
 *  javax.enterprise.context.spi.Contextual
 *  javax.enterprise.context.spi.CreationalContext
 *  javax.enterprise.inject.spi.Bean
 *  javax.enterprise.inject.spi.BeanManager
 */
package com.sun.jersey.server.impl.cdi;

import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.server.impl.cdi.CDIExtension;
import com.sun.jersey.server.impl.cdi.Utils;
import com.sun.jersey.spi.container.WebApplication;
import com.sun.jersey.spi.container.WebApplicationListener;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.context.spi.Contextual;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;

public class CDIComponentProviderFactory
implements IoCComponentProviderFactory,
WebApplicationListener {
    private static final Logger LOGGER = Logger.getLogger(CDIComponentProviderFactory.class.getName());
    private final BeanManager beanManager;
    private final CDIExtension extension;
    private final Map<Class<? extends Annotation>, ComponentScope> scopeMap = this.createScopeMap();

    public CDIComponentProviderFactory(Object bm, ResourceConfig rc, WebApplication wa) {
        this.beanManager = (BeanManager)bm;
        this.extension = CDIExtension.lookupExtensionInBeanManager ? CDIExtension.getInitializedExtensionFromBeanManager(this.beanManager) : CDIExtension.getInitializedExtension();
        this.extension.setWebApplication(wa);
        this.extension.setResourceConfig(rc);
    }

    @Override
    public void onWebApplicationReady() {
        this.extension.lateInitialize();
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        return this.getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, final Class<?> c) {
        final Bean<?> b = Utils.getBean(this.beanManager, c);
        if (b == null) {
            return null;
        }
        Class s = b.getScope();
        final ComponentScope cs = this.getComponentScope(b);
        if (s == Dependent.class) {
            if (!this.extension.getResourceConfig().getFeature("com.sun.jersey.config.feature.AllowRawManagedBeans") && !c.isAnnotationPresent(ManagedBean.class)) {
                return null;
            }
            LOGGER.fine("Binding the CDI managed bean " + c.getName() + " in scope " + s.getName() + " to CDIComponentProviderFactory");
            return new ComponentProviderDestroyable(){

                @Override
                public Object getInjectableInstance(Object o) {
                    return o;
                }

                @Override
                public Object getInstance() {
                    CreationalContext bcc = CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                    return c.cast(CDIComponentProviderFactory.this.beanManager.getReference(b, (Type)c, bcc));
                }

                @Override
                public void destroy(Object o) {
                    CreationalContext cc = CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                    b.destroy(o, cc);
                }
            };
        }
        LOGGER.fine("Binding the CDI managed bean " + c.getName() + " in scope " + s.getName() + " to CDIComponentProviderFactory in scope " + (Object)((Object)cs));
        return new IoCFullyManagedComponentProvider(){

            @Override
            public ComponentScope getScope() {
                return cs;
            }

            @Override
            public Object getInstance() {
                CreationalContext bcc = CDIComponentProviderFactory.this.beanManager.createCreationalContext((Contextual)b);
                return c.cast(CDIComponentProviderFactory.this.beanManager.getReference(b, (Type)c, bcc));
            }
        };
    }

    private ComponentScope getComponentScope(Bean<?> b) {
        ComponentScope cs = this.scopeMap.get(b.getScope());
        return cs != null ? cs : ComponentScope.Undefined;
    }

    private Map<Class<? extends Annotation>, ComponentScope> createScopeMap() {
        HashMap<Class<Dependent>, ComponentScope> m = new HashMap<Class<Dependent>, ComponentScope>();
        m.put(ApplicationScoped.class, ComponentScope.Singleton);
        m.put(RequestScoped.class, ComponentScope.PerRequest);
        m.put(Dependent.class, ComponentScope.PerRequest);
        return Collections.unmodifiableMap(m);
    }

    private static interface ComponentProviderDestroyable
    extends IoCInstantiatedComponentProvider,
    IoCDestroyable {
    }
}

