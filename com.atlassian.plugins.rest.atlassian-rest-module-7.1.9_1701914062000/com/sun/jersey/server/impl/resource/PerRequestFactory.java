/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.resource;

import com.sun.jersey.api.container.ContainerException;
import com.sun.jersey.api.container.MappableContainerException;
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
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

public final class PerRequestFactory
implements ResourceComponentProviderFactory {
    private static final Logger LOGGER = Logger.getLogger(PerRequestFactory.class.getName());
    private final ServerInjectableProviderContext sipc;
    private final HttpContext threadLocalHc;
    private static final String SCOPE_PER_REQUEST = "com.sun.jersey.scope.PerRequest";

    public static void destroy(HttpContext hc) {
        Map m = (Map)hc.getProperties().get(SCOPE_PER_REQUEST);
        if (m != null) {
            for (Map.Entry e : m.entrySet()) {
                try {
                    ((AbstractPerRequest)e.getKey()).destroy(e.getValue());
                }
                catch (ContainerException ex) {
                    LOGGER.log(Level.SEVERE, "Unable to destroy resource", ex);
                }
            }
        }
    }

    public PerRequestFactory(@Context ServerInjectableProviderContext sipc, @Context HttpContext threadLocalHc) {
        this.sipc = sipc;
        this.threadLocalHc = threadLocalHc;
    }

    @Override
    public ComponentScope getScope(Class c) {
        return ComponentScope.PerRequest;
    }

    @Override
    public ResourceComponentProvider getComponentProvider(Class c) {
        return new PerRequest();
    }

    public ResourceComponentProvider getComponentProvider(IoCComponentProvider icp, Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new PerRequestInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new PerRequestProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }

    private final class PerRequestProxied
    extends AbstractPerRequest {
        private final IoCProxiedComponentProvider ipcp;
        private ResourceComponentConstructor rcc;

        PerRequestProxied(IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
        }

        @Override
        public Object _getInstance(HttpContext hc) {
            try {
                return this.ipcp.proxy(this.rcc.construct(hc));
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
            catch (InvocationTargetException ex) {
                throw new MappableContainerException(ex.getTargetException());
            }
            catch (WebApplicationException ex) {
                throw ex;
            }
            catch (RuntimeException ex) {
                throw new ContainerException("Unable to create resource", ex);
            }
        }
    }

    private final class PerRequestInstantiated
    extends AbstractPerRequest {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;
        private ResourceComponentInjector rci;

        PerRequestInstantiated(IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = iicp instanceof IoCDestroyable ? (IoCDestroyable)((Object)iicp) : null;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            if (this.destroyable == null) {
                this.rci = new ResourceComponentInjector(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
            }
        }

        @Override
        public Object _getInstance(HttpContext hc) {
            Object o = this.iicp.getInstance();
            if (this.destroyable == null) {
                this.rci.inject(hc, this.iicp.getInjectableInstance(o));
            }
            return o;
        }

        @Override
        public void destroy(Object o) {
            if (this.destroyable != null) {
                this.destroyable.destroy(o);
            } else {
                super.destroy(o);
            }
        }
    }

    private final class PerRequest
    extends AbstractPerRequest {
        private ResourceComponentConstructor rcc;

        private PerRequest() {
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerRequestFactory.this.sipc, ComponentScope.PerRequest, abstractResource);
        }

        @Override
        protected Object _getInstance(HttpContext hc) {
            try {
                return this.rcc.construct(hc);
            }
            catch (InstantiationException ex) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex);
            }
            catch (InvocationTargetException ex) {
                throw new MappableContainerException(ex.getTargetException());
            }
            catch (WebApplicationException ex) {
                throw ex;
            }
            catch (RuntimeException ex) {
                throw new ContainerException("Unable to create resource " + this.rcc.getResourceClass(), ex);
            }
        }
    }

    private abstract class AbstractPerRequest
    implements ResourceComponentProvider {
        private ResourceComponentDestructor rcd;

        private AbstractPerRequest() {
        }

        @Override
        public final Object getInstance() {
            return this.getInstance(PerRequestFactory.this.threadLocalHc);
        }

        @Override
        public final ComponentScope getScope() {
            return ComponentScope.PerRequest;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
        }

        @Override
        public final Object getInstance(HttpContext hc) {
            Object o;
            HashMap m = (HashMap)hc.getProperties().get(PerRequestFactory.SCOPE_PER_REQUEST);
            if (m == null) {
                m = new HashMap();
                hc.getProperties().put(PerRequestFactory.SCOPE_PER_REQUEST, m);
            } else {
                o = m.get(this);
                if (o != null) {
                    return o;
                }
            }
            o = this._getInstance(hc);
            m.put(this, o);
            return o;
        }

        @Override
        public final void destroy() {
        }

        protected abstract Object _getInstance(HttpContext var1);

        public void destroy(Object o) {
            try {
                this.rcd.destroy(o);
            }
            catch (IllegalAccessException ex) {
                throw new ContainerException("Unable to destroy resource", ex);
            }
            catch (InvocationTargetException ex) {
                throw new ContainerException("Unable to destroy resource", ex);
            }
            catch (RuntimeException ex) {
                throw new ContainerException("Unable to destroy resource", ex);
            }
        }
    }
}

