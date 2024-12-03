/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ManagedBean
 */
package com.sun.jersey.server.impl.managedbeans;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import java.lang.reflect.Method;
import java.util.logging.Logger;
import javax.annotation.ManagedBean;

final class ManagedBeanComponentProviderFactory
implements IoCComponentProviderFactory {
    private static final Logger LOGGER = Logger.getLogger(ManagedBeanComponentProviderFactory.class.getName());
    private final Object injectionMgr;
    private final Method createManagedObjectMethod;
    private final Method destroyManagedObjectMethod;

    public ManagedBeanComponentProviderFactory(Object injectionMgr, Method createManagedObjectMethod, Method destroyManagedObjectMethod) {
        this.injectionMgr = injectionMgr;
        this.createManagedObjectMethod = createManagedObjectMethod;
        this.destroyManagedObjectMethod = destroyManagedObjectMethod;
    }

    @Override
    public IoCComponentProvider getComponentProvider(Class<?> c) {
        return this.getComponentProvider(null, c);
    }

    @Override
    public IoCComponentProvider getComponentProvider(ComponentContext cc, Class<?> c) {
        if (!this.isManagedBean(c)) {
            return null;
        }
        LOGGER.info("Binding the Managed bean class " + c.getName() + " to ManagedBeanComponentProvider");
        return new ManagedBeanComponentProvider(c);
    }

    private boolean isManagedBean(Class<?> c) {
        return c.isAnnotationPresent(ManagedBean.class);
    }

    private class ManagedBeanComponentProvider
    implements IoCInstantiatedComponentProvider,
    IoCDestroyable {
        private final Class<?> c;

        ManagedBeanComponentProvider(Class<?> c) {
            this.c = c;
        }

        @Override
        public Object getInstance() {
            try {
                return ManagedBeanComponentProviderFactory.this.createManagedObjectMethod.invoke(ManagedBeanComponentProviderFactory.this.injectionMgr, this.c);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }

        @Override
        public Object getInjectableInstance(Object o) {
            return o;
        }

        @Override
        public void destroy(Object o) {
            try {
                ManagedBeanComponentProviderFactory.this.destroyManagedObjectMethod.invoke(ManagedBeanComponentProviderFactory.this.injectionMgr, o);
            }
            catch (Exception ex) {
                throw new ContainerException(ex);
            }
        }
    }
}

