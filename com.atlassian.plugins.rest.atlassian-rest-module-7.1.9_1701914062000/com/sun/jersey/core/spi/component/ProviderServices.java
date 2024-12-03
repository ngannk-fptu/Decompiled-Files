/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.core.spi.component;

import com.sun.jersey.core.reflection.ReflectionHelper;
import com.sun.jersey.core.spi.component.ComponentProvider;
import com.sun.jersey.core.spi.component.ProviderFactory;
import com.sun.jersey.core.spi.factory.InjectableProviderFactory;
import com.sun.jersey.impl.SpiMessages;
import com.sun.jersey.spi.inject.ConstrainedTo;
import com.sun.jersey.spi.inject.ConstrainedToType;
import com.sun.jersey.spi.service.ServiceFinder;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProviderServices {
    private static final Logger LOGGER = Logger.getLogger(ProviderServices.class.getName());
    private final Class<? extends ConstrainedToType> constraintToType;
    private final ProviderFactory componentProviderFactory;
    private final Set<Class<?>> providers;
    private final Set providerInstances;

    public ProviderServices(ProviderFactory componentProviderFactory, Set<Class<?>> providers, Set<?> providerInstances) {
        this(ConstrainedToType.class, componentProviderFactory, providers, providerInstances);
    }

    public ProviderServices(Class<? extends ConstrainedToType> constraintToType, ProviderFactory componentProviderFactory, Set<Class<?>> providers, Set<?> providerInstances) {
        this.constraintToType = constraintToType;
        this.componentProviderFactory = componentProviderFactory;
        this.providers = providers;
        this.providerInstances = providerInstances;
    }

    public void update(Set<Class<?>> providers, Set<?> providerInstances, InjectableProviderFactory ipf) {
        Set<Class<?>> addedProviders = this.diff(this.providers, providers);
        Set<?> addedProviderInstances = this.diff(this.providerInstances, providerInstances);
        this.providers.clear();
        this.providers.addAll(providers);
        this.providerInstances.clear();
        this.providerInstances.addAll(providerInstances);
        ProviderServices _ps = new ProviderServices(this.componentProviderFactory, addedProviders, addedProviderInstances);
        InjectableProviderFactory _ipf = new InjectableProviderFactory();
        _ipf.configureProviders(_ps);
        ipf.update(_ipf);
    }

    private <T> Set<T> diff(Set<T> s1, Set<T> s2) {
        LinkedHashSet<T> diff = new LinkedHashSet<T>();
        for (T t : s1) {
            if (s2.contains(t)) continue;
            diff.add(t);
        }
        for (T t : s2) {
            if (s1.contains(t)) continue;
            diff.add(t);
        }
        return diff;
    }

    public ProviderFactory getComponentProviderFactory() {
        return this.componentProviderFactory;
    }

    public <T> Set<T> getProviders(Class<T> provider) {
        LinkedHashSet<T> ps = new LinkedHashSet<T>();
        ps.addAll(this.getProviderInstances(provider));
        for (Class pc : this.getProviderClasses(provider)) {
            Object o = this.getComponent(pc);
            if (o == null) continue;
            ps.add(provider.cast(o));
        }
        return ps;
    }

    public <T> Set<T> getServices(Class<T> provider) {
        LinkedHashSet<T> ps = new LinkedHashSet<T>();
        for (ProviderClass pc : this.getServiceClasses(provider)) {
            Object o = this.getComponent(pc);
            if (o == null) continue;
            ps.add(provider.cast(o));
        }
        return ps;
    }

    public <T> Set<T> getProvidersAndServices(Class<T> provider) {
        LinkedHashSet<T> ps = new LinkedHashSet<T>();
        ps.addAll(this.getProviderInstances(provider));
        for (ProviderClass pc : this.getProviderAndServiceClasses(provider)) {
            Object o = this.getComponent(pc);
            if (o == null) continue;
            ps.add(provider.cast(o));
        }
        return ps;
    }

    public <T> void getProviders(Class<T> provider, ProviderListener listener) {
        for (T t : this.getProviderInstances(provider)) {
            listener.onAdd(t);
        }
        for (ProviderClass pc : this.getProviderOnlyClasses(provider)) {
            Object o = this.getComponent(pc);
            if (o == null) continue;
            listener.onAdd(provider.cast(o));
        }
    }

    public <T> void getProvidersAndServices(Class<T> provider, ProviderListener listener) {
        for (T t : this.getProviderInstances(provider)) {
            listener.onAdd(t);
        }
        for (ProviderClass pc : this.getProviderAndServiceClasses(provider)) {
            Object o = this.getComponent(pc);
            if (o == null) continue;
            listener.onAdd(provider.cast(o));
        }
    }

    public <T> List<T> getInstances(Class<T> provider, String[] classNames) {
        LinkedList<T> ps = new LinkedList<T>();
        for (String className : classNames) {
            try {
                Class c = AccessController.doPrivileged(ReflectionHelper.classForNameWithExceptionPEA(className));
                if (provider.isAssignableFrom(c)) {
                    Object o = this.getComponent(c);
                    if (o == null) continue;
                    ps.add(provider.cast(o));
                    continue;
                }
                LOGGER.severe("The class " + className + " is not assignable to the class " + provider.getName() + ". This class is ignored.");
            }
            catch (ClassNotFoundException ex) {
                LOGGER.severe("The class " + className + " could not be found. This class is ignored.");
            }
            catch (PrivilegedActionException pae) {
                Throwable thrown = pae.getCause();
                if (thrown instanceof ClassNotFoundException) {
                    LOGGER.severe("The class " + className + " could not be found. This class is ignored.");
                    continue;
                }
                if (thrown instanceof NoClassDefFoundError) {
                    LOGGER.severe(SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_NOT_FOUND(thrown.getLocalizedMessage(), className, provider));
                    continue;
                }
                if (thrown instanceof ClassFormatError) {
                    LOGGER.severe(SpiMessages.DEPENDENT_CLASS_OF_PROVIDER_FORMAT_ERROR(thrown.getLocalizedMessage(), className, provider));
                    continue;
                }
                LOGGER.severe(SpiMessages.PROVIDER_CLASS_COULD_NOT_BE_LOADED(className, provider.getName(), thrown.getLocalizedMessage()));
            }
        }
        return ps;
    }

    public <T> List<T> getInstances(Class<T> provider, Class<? extends T>[] classes) {
        LinkedList<T> ps = new LinkedList<T>();
        for (Class<? extends T> c : classes) {
            Object o = this.getComponent(c);
            if (o == null) continue;
            ps.add(provider.cast(o));
        }
        return ps;
    }

    private Object getComponent(Class provider) {
        ComponentProvider cp = this.componentProviderFactory.getComponentProvider(provider);
        return cp != null ? cp.getInstance() : null;
    }

    private Object getComponent(ProviderClass provider) {
        ComponentProvider cp = this.componentProviderFactory.getComponentProvider(provider);
        return cp != null ? cp.getInstance() : null;
    }

    private <T> Set<T> getProviderInstances(Class<T> service) {
        LinkedHashSet<T> sp = new LinkedHashSet<T>();
        for (Object p : this.providerInstances) {
            if (!service.isInstance(p) || !this.constrainedTo(p.getClass())) continue;
            sp.add(service.cast(p));
        }
        return sp;
    }

    private Set<Class> getProviderClasses(Class<?> service) {
        LinkedHashSet<Class> sp = new LinkedHashSet<Class>();
        for (Class<?> p : this.providers) {
            if (!service.isAssignableFrom(p) || !this.constrainedTo(p)) continue;
            sp.add(p);
        }
        return sp;
    }

    private Set<ProviderClass> getProviderAndServiceClasses(Class<?> service) {
        Set<ProviderClass> sp = this.getProviderOnlyClasses(service);
        this.getServiceClasses(service, sp);
        return sp;
    }

    private Set<ProviderClass> getProviderOnlyClasses(Class<?> service) {
        LinkedHashSet<ProviderClass> sp = new LinkedHashSet<ProviderClass>();
        for (Class c : this.getProviderClasses(service)) {
            sp.add(new ProviderClass(c));
        }
        return sp;
    }

    private Set<ProviderClass> getServiceClasses(Class<?> service) {
        LinkedHashSet<ProviderClass> sp = new LinkedHashSet<ProviderClass>();
        this.getServiceClasses(service, sp);
        return sp;
    }

    private void getServiceClasses(Class<?> service, Set<ProviderClass> sp) {
        Class<?>[] pca;
        LOGGER.log(Level.CONFIG, "Searching for providers that implement: " + service);
        for (Class<?> pc : pca = ServiceFinder.find(service, true).toClassArray()) {
            if (!this.constrainedTo(pc)) continue;
            LOGGER.log(Level.CONFIG, "    Provider found: " + pc);
        }
        for (Class<?> pc : pca) {
            if (!this.constrainedTo(pc)) continue;
            if (service.isAssignableFrom(pc)) {
                sp.add(new ProviderClass(pc, true));
                continue;
            }
            LOGGER.log(Level.CONFIG, "Provider " + pc.getName() + " won't be used because its not assignable to " + service.getName() + ". This might be caused by clashing container-provided and application-bundled Jersey classes.");
        }
    }

    private boolean constrainedTo(Class<?> p) {
        ConstrainedTo ct = p.getAnnotation(ConstrainedTo.class);
        return ct != null ? ct.value().isAssignableFrom(this.constraintToType) : true;
    }

    public class ProviderClass {
        final boolean isServiceClass;
        final Class c;

        ProviderClass(Class c) {
            this.c = c;
            this.isServiceClass = false;
        }

        ProviderClass(Class c, boolean isServiceClass) {
            this.c = c;
            this.isServiceClass = isServiceClass;
        }
    }

    public static interface ProviderListener<T> {
        public void onAdd(T var1);
    }
}

