/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.spi.component.ComponentConstructor;
import com.sun.jersey.core.spi.component.ComponentDestructor;
import com.sun.jersey.core.spi.component.ComponentInjector;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ComponentProviderFactory;
import com.sun.jersey.core.spi.component.ProviderServices;
import com.sun.jersey.spi.inject.InjectableProviderContext;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProviderFactory
implements ComponentProviderFactory<ComponentProvider> {
    protected static final Logger LOGGER = Logger.getLogger(ProviderFactory.class.getName());
    private final Map<Class, ComponentProvider> cache = new HashMap<Class, ComponentProvider>();
    private final InjectableProviderContext ipc;

    public ProviderFactory(InjectableProviderContext ipc) {
        this.ipc = ipc;
    }

    public InjectableProviderContext getInjectableProviderContext() {
        return this.ipc;
    }

    public final ComponentProvider getComponentProvider(ProviderServices.ProviderClass pc) {
        if (!pc.isServiceClass) {
            return this.getComponentProvider(pc.c);
        }
        ComponentProvider cp = this.cache.get(pc.c);
        if (cp != null) {
            return cp;
        }
        cp = this.__getComponentProvider(pc.c);
        if (cp != null) {
            this.cache.put(pc.c, cp);
        }
        return cp;
    }

    @Override
    public final ComponentProvider getComponentProvider(Class c) {
        ComponentProvider cp = this.cache.get(c);
        if (cp != null) {
            return cp;
        }
        cp = this._getComponentProvider(c);
        if (cp != null) {
            this.cache.put(c, cp);
        }
        return cp;
    }

    protected ComponentProvider _getComponentProvider(Class c) {
        return this.__getComponentProvider(c);
    }

    private ComponentProvider __getComponentProvider(Class c) {
        try {
            ComponentInjector ci = new ComponentInjector(this.ipc, c);
            ComponentConstructor cc = new ComponentConstructor(this.ipc, c, ci);
            Object o = cc.getInstance();
            return new SingletonComponentProvider(ci, o);
        }
        catch (NoClassDefFoundError ex) {
            LOGGER.log(Level.CONFIG, "A dependent class, " + ex.getLocalizedMessage() + ", of the component " + c + " is not found. The component is ignored.");
            return null;
        }
        catch (InvocationTargetException ex) {
            if (ex.getCause() instanceof NoClassDefFoundError) {
                NoClassDefFoundError ncdf = (NoClassDefFoundError)ex.getCause();
                LOGGER.log(Level.CONFIG, "A dependent class, " + ncdf.getLocalizedMessage() + ", of the component " + c + " is not found. The component is ignored.");
                return null;
            }
            LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", ex.getTargetException());
            return null;
        }
        catch (Exception ex) {
            LOGGER.log(Level.SEVERE, "The provider class, " + c + ", could not be instantiated. Processing will continue but the class will not be utilized", ex);
            return null;
        }
    }

    public void injectOnAllComponents() {
        for (ComponentProvider cp : this.cache.values()) {
            if (!(cp instanceof SingletonComponentProvider)) continue;
            SingletonComponentProvider scp = (SingletonComponentProvider)cp;
            scp.inject();
        }
    }

    public void destroy() {
        for (ComponentProvider cp : this.cache.values()) {
            if (!(cp instanceof Destroyable)) continue;
            Destroyable d = (Destroyable)((Object)cp);
            d.destroy();
        }
    }

    public void injectOnProviderInstances(Collection<?> providers) {
        for (Object o : providers) {
            this.injectOnProviderInstance(o);
        }
    }

    public void injectOnProviderInstance(Object provider) {
        Class<?> c = provider.getClass();
        ComponentInjector ci = new ComponentInjector(this.ipc, c);
        ci.inject(provider);
    }

    private static final class SingletonComponentProvider
    implements ComponentProvider,
    Destroyable {
        private final Object o;
        private final ComponentDestructor cd;
        private final ComponentInjector ci;

        SingletonComponentProvider(ComponentInjector ci, Object o) {
            this.cd = new ComponentDestructor(o.getClass());
            this.ci = ci;
            this.o = o;
        }

        @Override
        public Object getInstance() {
            return this.o;
        }

        public void inject() {
            this.ci.inject(this.o);
        }

        @Override
        public void destroy() {
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

    protected static interface Destroyable {
        public void destroy();
    }
}

