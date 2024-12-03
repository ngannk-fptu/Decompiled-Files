/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.component;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.core.ResourceConfig;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCComponentProviderFactory;
import com.sun.jersey.core.spi.component.ioc.IoCFullyManagedComponentProvider;
import com.sun.jersey.core.spi.component.ioc.IoCManagedComponentProvider;
import com.sun.jersey.core.util.PriorityUtil;
import com.sun.jersey.server.impl.component.ResourceFactory;
import com.sun.jersey.server.impl.inject.ServerInjectableProviderContext;
import com.sun.jersey.server.spi.component.ResourceComponentInjector;
import com.sun.jersey.server.spi.component.ResourceComponentProvider;
import com.sun.jersey.server.spi.component.ResourceComponentProviderFactory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class IoCResourceFactory
extends ResourceFactory {
    private final List<IoCComponentProviderFactory> factories;

    public IoCResourceFactory(ResourceConfig config, ServerInjectableProviderContext ipc, List<IoCComponentProviderFactory> factories) {
        super(config, ipc);
        ArrayList<IoCComponentProviderFactory> myFactories = new ArrayList<IoCComponentProviderFactory>(factories);
        Collections.sort(myFactories, PriorityUtil.INSTANCE_COMPARATOR);
        this.factories = Collections.unmodifiableList(myFactories);
    }

    @Override
    public ResourceComponentProvider getComponentProvider(ComponentContext cc, Class c) {
        IoCComponentProviderFactory f;
        IoCComponentProvider icp = null;
        Iterator<IoCComponentProviderFactory> iterator = this.factories.iterator();
        while (iterator.hasNext() && (icp = (f = iterator.next()).getComponentProvider(cc, c)) == null) {
        }
        return icp == null ? super.getComponentProvider(cc, c) : this.wrap(c, icp);
    }

    private ResourceComponentProvider wrap(Class c, IoCComponentProvider icp) {
        if (icp instanceof IoCManagedComponentProvider) {
            IoCManagedComponentProvider imcp = (IoCManagedComponentProvider)icp;
            if (imcp.getScope() == ComponentScope.PerRequest) {
                return new PerRequestWrapper(this.getInjectableProviderContext(), imcp);
            }
            if (imcp.getScope() == ComponentScope.Singleton) {
                return new SingletonWrapper(this.getInjectableProviderContext(), imcp);
            }
            return new UndefinedWrapper(this.getInjectableProviderContext(), imcp);
        }
        if (icp instanceof IoCFullyManagedComponentProvider) {
            IoCFullyManagedComponentProvider ifmcp = (IoCFullyManagedComponentProvider)icp;
            return new FullyManagedWrapper(ifmcp);
        }
        ResourceComponentProviderFactory rcpf = this.getComponentProviderFactory(c);
        return rcpf.getComponentProvider(icp, c);
    }

    private static class UndefinedWrapper
    implements ResourceComponentProvider {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private ResourceComponentInjector rci;

        UndefinedWrapper(ServerInjectableProviderContext ipc, IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            this.rci = new ResourceComponentInjector(this.ipc, ComponentScope.Undefined, abstractResource);
        }

        @Override
        public ComponentScope getScope() {
            return ComponentScope.Undefined;
        }

        @Override
        public Object getInstance(HttpContext hc) {
            Object o = this.imcp.getInstance();
            this.rci.inject(hc, this.imcp.getInjectableInstance(o));
            return o;
        }

        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }

        @Override
        public void destroy() {
        }
    }

    private static class SingletonWrapper
    implements ResourceComponentProvider {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private Object o;

        SingletonWrapper(ServerInjectableProviderContext ipc, IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            ResourceComponentInjector rci = new ResourceComponentInjector(this.ipc, ComponentScope.Singleton, abstractResource);
            this.o = this.imcp.getInstance();
            rci.inject(null, this.imcp.getInjectableInstance(this.o));
        }

        @Override
        public ComponentScope getScope() {
            return ComponentScope.Singleton;
        }

        @Override
        public Object getInstance(HttpContext hc) {
            return this.o;
        }

        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }

        @Override
        public void destroy() {
        }
    }

    private static class PerRequestWrapper
    implements ResourceComponentProvider {
        private final ServerInjectableProviderContext ipc;
        private final IoCManagedComponentProvider imcp;
        private ResourceComponentInjector rci;

        PerRequestWrapper(ServerInjectableProviderContext ipc, IoCManagedComponentProvider imcp) {
            this.ipc = ipc;
            this.imcp = imcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            this.rci = new ResourceComponentInjector(this.ipc, ComponentScope.PerRequest, abstractResource);
        }

        @Override
        public ComponentScope getScope() {
            return ComponentScope.PerRequest;
        }

        @Override
        public Object getInstance(HttpContext hc) {
            Object o = this.imcp.getInstance();
            this.rci.inject(hc, this.imcp.getInjectableInstance(o));
            return o;
        }

        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }

        @Override
        public void destroy() {
        }
    }

    private static class FullyManagedWrapper
    implements ResourceComponentProvider {
        private final IoCFullyManagedComponentProvider ifmcp;

        FullyManagedWrapper(IoCFullyManagedComponentProvider ifmcp) {
            this.ifmcp = ifmcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
        }

        @Override
        public ComponentScope getScope() {
            return this.ifmcp.getScope();
        }

        @Override
        public Object getInstance(HttpContext hc) {
            return this.ifmcp.getInstance();
        }

        @Override
        public Object getInstance() {
            throw new IllegalStateException();
        }

        @Override
        public void destroy() {
        }
    }
}

