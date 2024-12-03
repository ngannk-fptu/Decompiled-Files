/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component.ioc;

import com.sun.jersey.core.spi.component.ComponentDestructor;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCDestroyable;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCInstantiatedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCProxiedComponentProvider;
import com.sun.jersey.core.util.PriorityUtil;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

public class IoCProviderFactory
extends ProviderFactory {
    private final List<IoCComponentProviderFactory> factories;

    public IoCProviderFactory(InjectableProviderContext ipc, IoCComponentProviderFactory icpf) {
        this(ipc, Collections.singletonList(icpf));
    }

    public IoCProviderFactory(InjectableProviderContext ipc, List<IoCComponentProviderFactory> factories) {
        super(ipc);
        ArrayList<IoCComponentProviderFactory> myFactories = new ArrayList<IoCComponentProviderFactory>(factories);
        Collections.sort(myFactories, PriorityUtil.INSTANCE_COMPARATOR);
        this.factories = Collections.unmodifiableList(myFactories);
    }

    @Override
    public ComponentProvider _getComponentProvider(Class c) {
        IoCComponentProviderFactory f;
        ComponentProvider icp = null;
        Iterator<IoCComponentProviderFactory> iterator = this.factories.iterator();
        while (iterator.hasNext() && (icp = (f = iterator.next()).getComponentProvider(c)) == null) {
        }
        return icp == null ? super._getComponentProvider(c) : this.wrap(c, (IoCComponentProvider)icp);
    }

    private ComponentProvider wrap(Class c, IoCComponentProvider icp) {
        if (icp instanceof IoCManagedComponentProvider) {
            IoCManagedComponentProvider imcp = (IoCManagedComponentProvider)icp;
            if (imcp.getScope() == ComponentScope.Singleton) {
                return new ManagedSingleton(this.getInjectableProviderContext(), imcp, c);
            }
            throw new RuntimeException("The scope of the component " + c + " must be a singleton");
        }
        if (icp instanceof IoCFullyManagedComponentProvider) {
            IoCFullyManagedComponentProvider ifmcp = (IoCFullyManagedComponentProvider)icp;
            return new FullyManagedSingleton(ifmcp.getInstance());
        }
        if (icp instanceof IoCInstantiatedComponentProvider) {
            IoCInstantiatedComponentProvider iicp = (IoCInstantiatedComponentProvider)icp;
            return new InstantiatedSingleton(this.getInjectableProviderContext(), iicp, c);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            IoCProxiedComponentProvider ipcp = (IoCProxiedComponentProvider)icp;
            ComponentProvider cp = super._getComponentProvider(c);
            if (cp == null) {
                return null;
            }
            return new ProxiedSingletonWrapper(ipcp, cp, c);
        }
        throw new UnsupportedOperationException();
    }

    private static class ProxiedSingletonWrapper
    implements ComponentProvider,
    ProviderFactory.Destroyable {
        private final ProviderFactory.Destroyable destroyable;
        private final Object proxy;

        ProxiedSingletonWrapper(IoCProxiedComponentProvider ipcp, ComponentProvider cp, Class c) {
            this.destroyable = cp instanceof ProviderFactory.Destroyable ? (ProviderFactory.Destroyable)((Object)cp) : null;
            Object o = cp.getInstance();
            this.proxy = ipcp.proxy(o);
            if (!this.proxy.getClass().isAssignableFrom(o.getClass())) {
                throw new IllegalStateException("Proxied object class " + this.proxy.getClass() + " is not assignable from object class " + o.getClass());
            }
        }

        @Override
        public Object getInstance() {
            return this.proxy;
        }

        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy();
            }
        }
    }

    private static class FullyManagedSingleton
    implements ComponentProvider {
        private final Object o;

        FullyManagedSingleton(Object o) {
            this.o = o;
        }

        @Override
        public Object getInstance() {
            return this.o;
        }
    }

    private static class ManagedSingleton
    implements ComponentProvider {
        private final Object o;

        ManagedSingleton(InjectableProviderContext ipc, IoCInstantiatedComponentProvider iicp, Class c) {
            ComponentInjector<Object> rci = new ComponentInjector<Object>(ipc, c);
            this.o = iicp.getInstance();
            rci.inject(iicp.getInjectableInstance(this.o));
        }

        @Override
        public Object getInstance() {
            return this.o;
        }
    }

    private static class InstantiatedSingleton
    implements ComponentProvider,
    ProviderFactory.Destroyable {
        private final Object o;
        private final IoCDestroyable destroyable;
        private final ComponentDestructor cd;

        InstantiatedSingleton(InjectableProviderContext ipc, IoCInstantiatedComponentProvider iicp, Class c) {
            this.destroyable = iicp instanceof IoCDestroyable ? (IoCDestroyable)((Object)iicp) : null;
            this.o = iicp.getInstance();
            ComponentDestructor componentDestructor = this.cd = this.destroyable == null ? new ComponentDestructor(c) : null;
            if (this.destroyable == null) {
                ComponentInjector<Object> ci = new ComponentInjector<Object>(ipc, c);
                ci.inject(iicp.getInjectableInstance(this.o));
            }
        }

        @Override
        public Object getInstance() {
            return this.o;
        }

        @Override
        public void destroy() {
            if (this.destroyable != null) {
                this.destroyable.destroy(this.o);
            } else {
                try {
                    this.cd.destroy(this.o);
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
}

