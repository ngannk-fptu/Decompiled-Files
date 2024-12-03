/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.reactivex.BackpressureStrategy
 *  io.reactivex.Completable
 *  io.reactivex.Flowable
 *  io.reactivex.Maybe
 *  io.reactivex.Observable
 *  io.reactivex.Single
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  rx.Completable
 *  rx.Observable
 *  rx.RxReactiveStreams
 *  rx.Single
 */
package org.springframework.core;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveTypeDescriptor;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Completable;
import rx.Observable;
import rx.RxReactiveStreams;

public class ReactiveAdapterRegistry {
    @Nullable
    private static volatile ReactiveAdapterRegistry sharedInstance;
    private final boolean reactorPresent;
    private final List<ReactiveAdapter> adapters = new ArrayList<ReactiveAdapter>(32);

    public ReactiveAdapterRegistry() {
        boolean reactorRegistered = false;
        try {
            new ReactorRegistrar().registerAdapters(this);
            reactorRegistered = true;
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        this.reactorPresent = reactorRegistered;
        try {
            new RxJava1Registrar().registerAdapters(this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            new RxJava2Registrar().registerAdapters(this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            new ReactorJdkFlowAdapterRegistrar().registerAdapter(this);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    public boolean hasAdapters() {
        return !this.adapters.isEmpty();
    }

    public void registerReactiveType(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toAdapter, Function<Publisher<?>, Object> fromAdapter) {
        if (this.reactorPresent) {
            this.adapters.add(new ReactorAdapter(descriptor, toAdapter, fromAdapter));
        } else {
            this.adapters.add(new ReactiveAdapter(descriptor, toAdapter, fromAdapter));
        }
    }

    @Nullable
    public ReactiveAdapter getAdapter(Class<?> reactiveType) {
        return this.getAdapter(reactiveType, null);
    }

    @Nullable
    public ReactiveAdapter getAdapter(@Nullable Class<?> reactiveType, @Nullable Object source) {
        Class<?> clazz;
        Object sourceToUse = source instanceof Optional ? ((Optional)source).orElse(null) : source;
        Class<?> clazz2 = clazz = sourceToUse != null ? sourceToUse.getClass() : reactiveType;
        if (clazz == null) {
            return null;
        }
        return this.adapters.stream().filter(adapter -> adapter.getReactiveType() == clazz).findFirst().orElseGet(() -> this.adapters.stream().filter(adapter -> adapter.getReactiveType().isAssignableFrom(clazz)).findFirst().orElse(null));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public static ReactiveAdapterRegistry getSharedInstance() {
        ReactiveAdapterRegistry registry = sharedInstance;
        if (registry != null) return registry;
        Class<ReactiveAdapterRegistry> clazz = ReactiveAdapterRegistry.class;
        synchronized (ReactiveAdapterRegistry.class) {
            registry = sharedInstance;
            if (registry != null) return registry;
            sharedInstance = registry = new ReactiveAdapterRegistry();
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return registry;
        }
    }

    private static class ReactorAdapter
    extends ReactiveAdapter {
        ReactorAdapter(ReactiveTypeDescriptor descriptor, Function<Object, Publisher<?>> toPublisherFunction, Function<Publisher<?>, Object> fromPublisherFunction) {
            super(descriptor, toPublisherFunction, fromPublisherFunction);
        }

        @Override
        public <T> Publisher<T> toPublisher(@Nullable Object source) {
            Publisher publisher = super.toPublisher(source);
            return this.isMultiValue() ? Flux.from(publisher) : Mono.from(publisher);
        }
    }

    private static class ReactorJdkFlowAdapterRegistrar {
        private ReactorJdkFlowAdapterRegistrar() {
        }

        void registerAdapter(ReactiveAdapterRegistry registry) throws Exception {
            String publisherName = "java.util.concurrent.Flow.Publisher";
            Class<?> publisherClass = ClassUtils.forName(publisherName, this.getClass().getClassLoader());
            String adapterName = "reactor.adapter.JdkFlowAdapter";
            Class<?> flowAdapterClass = ClassUtils.forName(adapterName, this.getClass().getClassLoader());
            Method toFluxMethod = flowAdapterClass.getMethod("flowPublisherToFlux", publisherClass);
            Method toFlowMethod = flowAdapterClass.getMethod("publisherToFlowPublisher", Publisher.class);
            Object emptyFlow = ReflectionUtils.invokeMethod(toFlowMethod, null, Flux.empty());
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(publisherClass, () -> emptyFlow), source -> (Publisher)ReflectionUtils.invokeMethod(toFluxMethod, null, source), publisher -> ReflectionUtils.invokeMethod(toFlowMethod, null, publisher));
        }
    }

    private static class RxJava2Registrar {
        private RxJava2Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flowable.class, Flowable::empty), source -> (Flowable)source, Flowable::fromPublisher);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(io.reactivex.Observable.class, io.reactivex.Observable::empty), source -> ((io.reactivex.Observable)source).toFlowable(BackpressureStrategy.BUFFER), source -> Flowable.fromPublisher((Publisher)source).toObservable());
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(Single.class), source -> ((Single)source).toFlowable(), source -> Flowable.fromPublisher((Publisher)source).toObservable().singleElement().toSingle());
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Maybe.class, Maybe::empty), source -> ((Maybe)source).toFlowable(), source -> Flowable.fromPublisher((Publisher)source).toObservable().singleElement());
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(io.reactivex.Completable.class, io.reactivex.Completable::complete), source -> ((io.reactivex.Completable)source).toFlowable(), source -> Flowable.fromPublisher((Publisher)source).toObservable().ignoreElements());
        }
    }

    private static class RxJava1Registrar {
        private RxJava1Registrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Observable.class, Observable::empty), source -> RxReactiveStreams.toPublisher((Observable)((Observable)source)), RxReactiveStreams::toObservable);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleRequiredValue(rx.Single.class), source -> RxReactiveStreams.toPublisher((rx.Single)((rx.Single)source)), RxReactiveStreams::toSingle);
            registry.registerReactiveType(ReactiveTypeDescriptor.noValue(Completable.class, Completable::complete), source -> RxReactiveStreams.toPublisher((Completable)((Completable)source)), RxReactiveStreams::toCompletable);
        }
    }

    private static class ReactorRegistrar {
        private ReactorRegistrar() {
        }

        void registerAdapters(ReactiveAdapterRegistry registry) {
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(Mono.class, Mono::empty), source -> (Mono)source, Mono::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Flux.class, Flux::empty), source -> (Flux)source, Flux::from);
            registry.registerReactiveType(ReactiveTypeDescriptor.multiValue(Publisher.class, Flux::empty), source -> (Publisher)source, source -> source);
            registry.registerReactiveType(ReactiveTypeDescriptor.singleOptionalValue(CompletableFuture.class, () -> {
                CompletableFuture<Object> empty = new CompletableFuture<Object>();
                empty.complete(null);
                return empty;
            }), source -> Mono.fromFuture((CompletableFuture)((CompletableFuture)source)), source -> Mono.from((Publisher)source).toFuture());
        }
    }
}

