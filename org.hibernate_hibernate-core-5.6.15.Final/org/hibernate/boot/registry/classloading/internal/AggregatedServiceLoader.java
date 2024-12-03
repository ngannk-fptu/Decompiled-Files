/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.boot.registry.classloading.internal;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.hibernate.AssertionFailure;
import org.hibernate.boot.registry.classloading.internal.AggregatedClassLoader;
import org.hibernate.internal.CoreLogging;
import org.hibernate.internal.CoreMessageLogger;

abstract class AggregatedServiceLoader<S> {
    private static final CoreMessageLogger log = CoreLogging.messageLogger(AggregatedServiceLoader.class);
    private static final Method SERVICE_LOADER_STREAM_METHOD;
    private static final Method PROVIDER_TYPE_METHOD;

    AggregatedServiceLoader() {
    }

    static <S> AggregatedServiceLoader<S> create(AggregatedClassLoader aggregatedClassLoader, Class<S> serviceContract) {
        if (SERVICE_LOADER_STREAM_METHOD != null) {
            return new ClassPathAndModulePathAggregatedServiceLoader(aggregatedClassLoader, serviceContract);
        }
        return new ClassPathOnlyAggregatedServiceLoader(aggregatedClassLoader, serviceContract);
    }

    public abstract Collection<S> getAll();

    public abstract void close();

    static {
        Class<ServiceLoader> serviceLoaderClass = ServiceLoader.class;
        Method serviceLoaderStreamMethod = null;
        Method providerTypeMethod = null;
        try {
            serviceLoaderStreamMethod = serviceLoaderClass.getMethod("stream", new Class[0]);
            Class<?> providerClass = Class.forName(serviceLoaderClass.getName() + "$Provider");
            providerTypeMethod = providerClass.getMethod("type", new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException reflectiveOperationException) {
            // empty catch block
        }
        SERVICE_LOADER_STREAM_METHOD = serviceLoaderStreamMethod;
        PROVIDER_TYPE_METHOD = providerTypeMethod;
    }

    private static class ClassPathAndModulePathAggregatedServiceLoader<S>
    extends AggregatedServiceLoader<S> {
        private final Class<S> serviceContract;
        private final ServiceLoader<S> aggregatedClassLoaderServiceLoader;
        private final List<ServiceLoader<S>> delegates;
        private Collection<S> cache = null;

        private ClassPathAndModulePathAggregatedServiceLoader(AggregatedClassLoader aggregatedClassLoader, Class<S> serviceContract) {
            this.serviceContract = serviceContract;
            this.delegates = new ArrayList<ServiceLoader<S>>();
            this.aggregatedClassLoaderServiceLoader = ServiceLoader.load(serviceContract, aggregatedClassLoader);
            Iterator<ClassLoader> clIterator = aggregatedClassLoader.newClassLoaderIterator();
            while (clIterator.hasNext()) {
                this.delegates.add(ServiceLoader.load(serviceContract, clIterator.next()));
            }
        }

        @Override
        public Collection<S> getAll() {
            if (this.cache == null) {
                this.cache = Collections.unmodifiableCollection(this.loadAll());
            }
            return this.cache;
        }

        private Collection<S> loadAll() {
            HashSet<String> alreadyEncountered = new HashSet<String>();
            LinkedHashSet result = new LinkedHashSet();
            Iterator providerIterator = this.providerStream(this.aggregatedClassLoaderServiceLoader).iterator();
            while (providerIterator.hasNext()) {
                Supplier provider = (Supplier)providerIterator.next();
                this.collectServiceIfNotDuplicate(result, alreadyEncountered, provider);
            }
            for (ServiceLoader<S> delegate : this.delegates) {
                providerIterator = this.providerStream(delegate).iterator();
                while (this.hasNextIgnoringServiceConfigurationError(providerIterator)) {
                    Supplier provider = (Supplier)providerIterator.next();
                    this.collectServiceIfNotDuplicate(result, alreadyEncountered, provider);
                }
            }
            return result;
        }

        private Stream<? extends Supplier<S>> providerStream(ServiceLoader<S> serviceLoader) {
            try {
                return (Stream)SERVICE_LOADER_STREAM_METHOD.invoke(serviceLoader, new Object[0]);
            }
            catch (IllegalAccessException | RuntimeException | InvocationTargetException e) {
                throw new AssertionFailure("Error calling ServiceLoader.stream()", e);
            }
        }

        private boolean hasNextIgnoringServiceConfigurationError(Iterator<?> iterator) {
            while (true) {
                try {
                    return iterator.hasNext();
                }
                catch (ServiceConfigurationError e) {
                    log.ignoringServiceConfigurationError(this.serviceContract, e);
                    continue;
                }
                break;
            }
        }

        private void collectServiceIfNotDuplicate(Set<S> result, Set<String> alreadyEncountered, Supplier<S> provider) {
            Class type;
            try {
                type = (Class)PROVIDER_TYPE_METHOD.invoke(provider, new Object[0]);
            }
            catch (IllegalAccessException | RuntimeException | InvocationTargetException e) {
                throw new AssertionFailure("Error calling ServiceLoader.Provider.type()", e);
            }
            String typeName = type.getName();
            if (alreadyEncountered.add(typeName)) {
                result.add(provider.get());
            }
        }

        @Override
        public void close() {
            this.cache = null;
        }
    }

    private static class ClassPathOnlyAggregatedServiceLoader<S>
    extends AggregatedServiceLoader<S> {
        private final ServiceLoader<S> delegate;

        private ClassPathOnlyAggregatedServiceLoader(AggregatedClassLoader aggregatedClassLoader, Class<S> serviceContract) {
            this.delegate = ServiceLoader.load(serviceContract, aggregatedClassLoader);
        }

        @Override
        public Collection<S> getAll() {
            LinkedHashSet<S> services = new LinkedHashSet<S>();
            for (S service : this.delegate) {
                services.add(service);
            }
            return services;
        }

        @Override
        public void close() {
            this.delegate.reload();
        }
    }
}

