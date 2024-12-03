/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpSession
 *  javax.servlet.http.HttpSessionBindingEvent
 *  javax.servlet.http.HttpSessionBindingListener
 */
package com.sun.jersey.server.impl.container.servlet;

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
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

public final class PerSessionFactory
implements ResourceComponentProviderFactory {
    private final ServerInjectableProviderContext sipc;
    private final ServletContext sc;
    private final HttpServletRequest hsr;
    private final HttpContext threadLocalHc;
    private final String abstractPerSessionMapPropertyName;
    private final ConcurrentHashMap<Class, AbstractPerSession> abstractPerSessionMap = new ConcurrentHashMap();

    public PerSessionFactory(@Context ServerInjectableProviderContext sipc, @Context ServletContext sc, @Context HttpServletRequest hsr, @Context HttpContext threadLocalHc) {
        this.hsr = hsr;
        this.sc = sc;
        this.sipc = sipc;
        this.threadLocalHc = threadLocalHc;
        this.abstractPerSessionMapPropertyName = this.toString();
        sc.setAttribute(this.abstractPerSessionMapPropertyName, this.abstractPerSessionMap);
    }

    @Override
    public ComponentScope getScope(Class c) {
        return ComponentScope.Undefined;
    }

    @Override
    public ResourceComponentProvider getComponentProvider(Class c) {
        return new PerSesson();
    }

    public ResourceComponentProvider getComponentProvider(IoCComponentProvider icp, Class c) {
        if (icp instanceof IoCInstantiatedComponentProvider) {
            return new PerSessonInstantiated((IoCInstantiatedComponentProvider)icp);
        }
        if (icp instanceof IoCProxiedComponentProvider) {
            return new PerSessonProxied((IoCProxiedComponentProvider)icp);
        }
        throw new IllegalStateException();
    }

    private final class PerSessonProxied
    extends AbstractPerSession {
        private final IoCProxiedComponentProvider ipcp;
        private ResourceComponentConstructor rcc;

        PerSessonProxied(IoCProxiedComponentProvider ipcp) {
            this.ipcp = ipcp;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
        }

        @Override
        protected Object _getInstance(HttpContext hc) {
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

    private final class PerSessonInstantiated
    extends AbstractPerSession {
        private final IoCInstantiatedComponentProvider iicp;
        private final IoCDestroyable destroyable;
        private ResourceComponentInjector rci;

        PerSessonInstantiated(IoCInstantiatedComponentProvider iicp) {
            this.iicp = iicp;
            this.destroyable = iicp instanceof IoCDestroyable ? (IoCDestroyable)((Object)iicp) : null;
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            if (this.destroyable == null) {
                this.rci = new ResourceComponentInjector(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
            }
        }

        @Override
        protected Object _getInstance(HttpContext hc) {
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

    private final class PerSesson
    extends AbstractPerSession {
        private ResourceComponentConstructor rcc;

        private PerSesson() {
        }

        @Override
        public void init(AbstractResource abstractResource) {
            super.init(abstractResource);
            this.rcc = new ResourceComponentConstructor(PerSessionFactory.this.sipc, ComponentScope.Undefined, abstractResource);
        }

        @Override
        protected Object _getInstance(HttpContext hc) {
            try {
                return this.rcc.construct(hc);
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

    private abstract class AbstractPerSession
    implements ResourceComponentProvider {
        private static final String SCOPE_PER_SESSION = "com.sun.jersey.scope.PerSession";
        private ResourceComponentDestructor rcd;
        private Class c;

        private AbstractPerSession() {
        }

        @Override
        public void init(AbstractResource abstractResource) {
            this.rcd = new ResourceComponentDestructor(abstractResource);
            this.c = abstractResource.getResourceClass();
        }

        @Override
        public final Object getInstance() {
            return this.getInstance(PerSessionFactory.this.threadLocalHc);
        }

        @Override
        public final ComponentScope getScope() {
            return ComponentScope.Undefined;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public final Object getInstance(HttpContext hc) {
            HttpSession hs;
            HttpSession httpSession = hs = PerSessionFactory.this.hsr.getSession();
            synchronized (httpSession) {
                SessionMap sm = (SessionMap)hs.getAttribute(SCOPE_PER_SESSION);
                if (sm == null) {
                    sm = new SessionMap(PerSessionFactory.this.abstractPerSessionMapPropertyName);
                    hs.setAttribute(SCOPE_PER_SESSION, (Object)sm);
                }
                PerSessionFactory.this.abstractPerSessionMap.putIfAbsent(this.c, this);
                Object o = sm.get(this.c.getName());
                if (o != null) {
                    return o;
                }
                o = this._getInstance(hc);
                sm.put(this.c.getName(), o);
                return o;
            }
        }

        protected abstract Object _getInstance(HttpContext var1);

        @Override
        public final void destroy() {
        }

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

    private static class SessionMap
    extends HashMap<String, Object>
    implements HttpSessionBindingListener {
        private final String abstractPerSessionMapPropertyName;

        SessionMap(String abstractPerSessionMapPropertyName) {
            this.abstractPerSessionMapPropertyName = abstractPerSessionMapPropertyName;
        }

        public void valueBound(HttpSessionBindingEvent hsbe) {
        }

        public void valueUnbound(HttpSessionBindingEvent hsbe) {
            ServletContext sc = hsbe.getSession().getServletContext();
            Map abstractPerSessionMap = (Map)sc.getAttribute(this.abstractPerSessionMapPropertyName);
            for (Object o : this.values()) {
                AbstractPerSession aps = (AbstractPerSession)abstractPerSessionMap.get(o.getClass());
                if (aps == null) continue;
                aps.destroy(o);
            }
        }
    }
}

