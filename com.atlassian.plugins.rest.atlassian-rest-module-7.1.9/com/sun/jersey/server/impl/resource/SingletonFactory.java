/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.resource;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.spi.component.ResourceComponentConstructor;
import com.sun.jersey.server.spi.component.ResourceComponentDestructor;
import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import java.lang.reflect.InvocationTargetException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.core.Context;

public final class SingletonFactory
implements ResourceComponentProviderFactory {
    private static final Logger LOGGER = Logger.getLogger(SingletonFactory.class.getName());
    private final ServerInjectableProviderContext sipc;

    public SingletonFactory(@Context ServerInjectableProviderContext sipc) {
        this.sipc = sipc;
    }

    @Override
    public ComponentScope getScope(Class c) {
        return ComponentScope.Singleton;
    }

    @Override
    public ResourceComponentProvider getComponentProvider(Class c) {
        return new Singleton();
    }

    public ResourceComponentProvider getComponentProvider(IoCComponentProvider icp, Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new SingletonInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new SingletonProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }

    private class SingletonProxied
    extends AbstractSingleton {
        private final IoCProxiedComponentProvider ipcp;

        SingletonProxied(IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            ResourceComponentConstructor rcc = new ResourceComponentConstructor(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
            try {
                Object o = rcc.construct(null);
                this.resource = this.ipcp.proxy(o);
            }
            catch (InvocationTargetException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
        }
    }

    private class SingletonInstantiated
    extends AbstractSingleton {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;

        SingletonInstantiated(IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = iicp instanceof IoCDestroyable ? (IoCDestroyable)((Object)iicp) : null;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            this.resource = this.iicp.getInstance();
            if (this.destroyable == null) {
                ResourceComponentInjector rci = new ResourceComponentInjector(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
                rci.inject(null, this.iicp.getInjectableInstance(this.resource));
            }
        }

        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy(this.resource);
            } else {
                super.destroy();
            }
        }
    }

    private class Singleton
    extends AbstractSingleton {
        private Singleton() {
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            ResourceComponentConstructor rcc = new ResourceComponentConstructor(SingletonFactory.this.sipc, ComponentScope.Singleton, abstractResource);
            try {
                this.resource = rcc.construct(null);
            }
            catch (InvocationTargetException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
        }
    }

    private abstract class AbstractSingleton
    implements ResourceComponentProvider {
        private ResourceComponentDestructor rcd;
        protected Object resource;

        private AbstractSingleton() {
        }

        @Override
        public void init(AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
        }

        @Override
        public final Object getInstance(HttpContext hc) {
            return this.resource;
        }

        @Override
        public final Object getInstance() {
            return this.resource;
        }

        @Override
        public final ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        @Override
        public void destroy() {
            try {
                this.rcd.destroy(this.resource);
            }
            catch (IllegalAccessException ex) {
                LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
            }
            catch (IllegalArgumentException ex) {
                LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
            }
            catch (InvocationTargetException ex) {
                LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
            }
        }
    }
}

