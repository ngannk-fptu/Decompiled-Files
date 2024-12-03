/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.util;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.Component;
import com.sun.xml.ws.api.ComponentEx;
import com.sun.xml.ws.api.server.ContainerResolver;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.ServiceLoader;

public final class ServiceFinder<T>
implements Iterable<T> {
    @NotNull
    private final Class<T> serviceClass;
    @NotNull
    private final ServiceLoader<T> serviceLoader;
    @Nullable
    private final ComponentEx component;

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service, @Nullable ClassLoader loader, Component component) {
        ClassLoader cl = loader == null ? Thread.currentThread().getContextClassLoader() : loader;
        return ServiceFinder.find(service, component, ServiceLoader.load(service, cl));
    }

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service, Component component, @NotNull ServiceLoader<T> serviceLoader) {
        Class<T> svc = Objects.requireNonNull(service);
        ServiceLoader<T> sl = Objects.requireNonNull(serviceLoader);
        return new ServiceFinder<T>(svc, component, sl);
    }

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service, Component component) {
        return ServiceFinder.find(service, component, ServiceLoader.load(service, Thread.currentThread().getContextClassLoader()));
    }

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service, @Nullable ClassLoader loader) {
        return ServiceFinder.find(service, loader, ContainerResolver.getInstance().getContainer());
    }

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service) {
        return ServiceFinder.find(service, ServiceLoader.load(service, Thread.currentThread().getContextClassLoader()));
    }

    public static <T> ServiceFinder<T> find(@NotNull Class<T> service, @NotNull ServiceLoader<T> serviceLoader) {
        return ServiceFinder.find(service, ContainerResolver.getInstance().getContainer(), serviceLoader);
    }

    private ServiceFinder(Class<T> service, Component component, ServiceLoader<T> serviceLoader) {
        this.serviceClass = service;
        this.component = ServiceFinder.getComponentEx(component);
        this.serviceLoader = serviceLoader;
    }

    @Override
    public Iterator<T> iterator() {
        CompositeIterator it = this.serviceLoader.iterator();
        return this.component != null ? new CompositeIterator(this.component.getIterableSPI(this.serviceClass).iterator(), it) : it;
    }

    public T[] toArray() {
        ArrayList<T> result = new ArrayList<T>();
        for (T t : this) {
            result.add(t);
        }
        return result.toArray((Object[])Array.newInstance(this.serviceClass, result.size()));
    }

    private static ComponentEx getComponentEx(Component component) {
        if (component instanceof ComponentEx) {
            return (ComponentEx)component;
        }
        return component != null ? new ComponentExWrapper(component) : null;
    }

    private static class CompositeIterator<T>
    implements Iterator<T> {
        private final Iterator<Iterator<T>> it;
        private Iterator<T> current = null;

        public CompositeIterator(Iterator<T> ... iterators) {
            this.it = Arrays.asList(iterators).iterator();
        }

        @Override
        public boolean hasNext() {
            if (this.current != null && this.current.hasNext()) {
                return true;
            }
            while (this.it.hasNext()) {
                this.current = this.it.next();
                if (!this.current.hasNext()) continue;
                return true;
            }
            return false;
        }

        @Override
        public T next() {
            if (!this.hasNext()) {
                throw new NoSuchElementException();
            }
            return this.current.next();
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private static class ComponentExWrapper
    implements ComponentEx {
        private final Component component;

        public ComponentExWrapper(Component component) {
            this.component = component;
        }

        @Override
        public <S> S getSPI(Class<S> spiType) {
            return this.component.getSPI(spiType);
        }

        @Override
        public <S> Iterable<S> getIterableSPI(Class<S> spiType) {
            S item = this.getSPI(spiType);
            if (item != null) {
                List<S> c = Collections.singletonList(item);
                return c;
            }
            return Collections.emptySet();
        }
    }
}

