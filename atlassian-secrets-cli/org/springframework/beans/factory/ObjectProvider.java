/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans.factory;

import java.util.Iterator;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.lang.Nullable;

public interface ObjectProvider<T>
extends ObjectFactory<T>,
Iterable<T> {
    public T getObject(Object ... var1) throws BeansException;

    @Nullable
    public T getIfAvailable() throws BeansException;

    default public T getIfAvailable(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = this.getIfAvailable();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default public void ifAvailable(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = this.getIfAvailable();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    @Nullable
    public T getIfUnique() throws BeansException;

    default public T getIfUnique(Supplier<T> defaultSupplier) throws BeansException {
        T dependency = this.getIfUnique();
        return dependency != null ? dependency : defaultSupplier.get();
    }

    default public void ifUnique(Consumer<T> dependencyConsumer) throws BeansException {
        T dependency = this.getIfUnique();
        if (dependency != null) {
            dependencyConsumer.accept(dependency);
        }
    }

    @Override
    default public Iterator<T> iterator() {
        return this.stream().iterator();
    }

    default public Stream<T> stream() {
        throw new UnsupportedOperationException("Multi element access not supported");
    }

    default public Stream<T> orderedStream() {
        throw new UnsupportedOperationException("Ordered element access not supported");
    }
}

