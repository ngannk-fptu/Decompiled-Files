/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.util.Iterator;
import java.util.ServiceConfigurationError;
import java.util.ServiceLoader;
import java.util.Spliterator;
import java.util.function.Consumer;

class ServiceLoaderSpliterator<T>
implements Spliterator<ServiceLoader.Provider<T>> {
    private final Iterator<T> iterator;

    public ServiceLoaderSpliterator(ServiceLoader<T> serviceLoader) {
        this.iterator = serviceLoader.iterator();
    }

    @Override
    public boolean tryAdvance(Consumer<? super ServiceLoader.Provider<T>> action) {
        ServiceProvider next;
        try {
            if (!this.iterator.hasNext()) {
                return false;
            }
            next = new ServiceProvider<T>(this.iterator.next());
        }
        catch (Throwable t) {
            next = new ServiceProvider(t);
        }
        action.accept(next);
        return true;
    }

    @Override
    public Spliterator<ServiceLoader.Provider<T>> trySplit() {
        return null;
    }

    @Override
    public long estimateSize() {
        return Long.MAX_VALUE;
    }

    @Override
    public int characteristics() {
        return 16;
    }

    private static class ServiceProvider<T>
    implements ServiceLoader.Provider<T> {
        private final T service;
        private final Throwable error;

        public ServiceProvider(T service) {
            this.service = service;
            this.error = null;
        }

        public ServiceProvider(Throwable error) {
            this.service = null;
            this.error = error;
        }

        @Override
        public Class<? extends T> type() {
            return this.get().getClass();
        }

        @Override
        public T get() {
            if (this.service == null) {
                throw new ServiceConfigurationError("", this.error);
            }
            return this.service;
        }
    }
}

