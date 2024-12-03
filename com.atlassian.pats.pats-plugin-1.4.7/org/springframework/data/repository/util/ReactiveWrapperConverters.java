/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.reactivex.Flowable
 *  io.reactivex.Maybe
 *  io.reactivex.Observable
 *  io.reactivex.Single
 *  io.reactivex.rxjava3.core.Flowable
 *  io.reactivex.rxjava3.core.Maybe
 *  io.reactivex.rxjava3.core.Observable
 *  io.reactivex.rxjava3.core.Single
 *  javax.annotation.Nonnull
 *  kotlinx.coroutines.flow.Flow
 *  kotlinx.coroutines.flow.FlowKt
 *  kotlinx.coroutines.reactive.ReactiveFlowKt
 *  org.reactivestreams.Publisher
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalConverter
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterFactory
 *  org.springframework.core.convert.support.ConfigurableConversionService
 *  org.springframework.core.convert.support.GenericConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  reactor.core.publisher.Flux
 *  reactor.core.publisher.Mono
 *  rx.Observable
 *  rx.Single
 */
package org.springframework.data.repository.util;

import io.reactivex.Maybe;
import io.reactivex.rxjava3.core.Flowable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import kotlinx.coroutines.flow.Flow;
import kotlinx.coroutines.flow.FlowKt;
import kotlinx.coroutines.reactive.ReactiveFlowKt;
import org.reactivestreams.Publisher;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import rx.Observable;
import rx.Single;

public abstract class ReactiveWrapperConverters {
    private static final List<ReactiveTypeWrapper<?>> REACTIVE_WRAPPERS = new ArrayList();
    private static final GenericConversionService GENERIC_CONVERSION_SERVICE = new GenericConversionService();

    private ReactiveWrapperConverters() {
    }

    private static ConversionService registerConvertersIn(ConfigurableConversionService conversionService) {
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.PROJECT_REACTOR)) {
            conversionService.addConverter((Converter)PublisherToMonoConverter.INSTANCE);
            conversionService.addConverter((Converter)PublisherToFluxConverter.INSTANCE);
            if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.KOTLIN_COROUTINES)) {
                conversionService.addConverter((Converter)PublisherToFlowConverter.INSTANCE);
            }
            if (RegistryHolder.REACTIVE_ADAPTER_REGISTRY != null) {
                conversionService.addConverterFactory((ConverterFactory)ReactiveAdapterConverterFactory.INSTANCE);
            }
        }
        return conversionService;
    }

    public static boolean supports(Class<?> type) {
        return RegistryHolder.REACTIVE_ADAPTER_REGISTRY != null && RegistryHolder.REACTIVE_ADAPTER_REGISTRY.getAdapter(type) != null;
    }

    public static TypeInformation<?> unwrapWrapperTypes(TypeInformation<?> type) {
        Assert.notNull(type, (String)"type must not be null");
        Class<?> rawType = type.getType();
        return ReactiveWrapperConverters.supports(rawType) ? ReactiveWrapperConverters.unwrapWrapperTypes(type.getRequiredComponentType()) : type;
    }

    @Nullable
    public static <T> T toWrapper(Object reactiveObject, Class<? extends T> targetWrapperType) {
        Assert.notNull((Object)reactiveObject, (String)"Reactive source object must not be null!");
        Assert.notNull(targetWrapperType, (String)"Reactive target type must not be null!");
        if (targetWrapperType.isAssignableFrom(reactiveObject.getClass())) {
            return (T)reactiveObject;
        }
        return (T)GENERIC_CONVERSION_SERVICE.convert(reactiveObject, targetWrapperType);
    }

    public static <T> T map(Object reactiveObject, Function<Object, Object> converter) {
        Assert.notNull((Object)reactiveObject, (String)"Reactive source object must not be null!");
        Assert.notNull(converter, (String)"Converter must not be null!");
        return (T)ReactiveWrapperConverters.getFirst(reactiveObject).map(it -> it.map(reactiveObject, converter)).orElseThrow(() -> new IllegalStateException(String.format("Cannot apply converter to %s", reactiveObject)));
    }

    private static Optional<ReactiveTypeWrapper<?>> getFirst(Object reactiveObject) {
        return REACTIVE_WRAPPERS.stream().filter(it -> ClassUtils.isAssignable(it.getWrapperClass(), reactiveObject.getClass())).findFirst();
    }

    public static boolean canConvert(Class<?> sourceType, Class<?> targetType) {
        Assert.notNull(sourceType, (String)"Source type must not be null!");
        Assert.notNull(targetType, (String)"Target type must not be null!");
        return GENERIC_CONVERSION_SERVICE.canConvert(sourceType, targetType);
    }

    static {
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.RXJAVA1)) {
            REACTIVE_WRAPPERS.add(RxJava1SingleWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava1ObservableWrapper.INSTANCE);
        }
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.RXJAVA2)) {
            REACTIVE_WRAPPERS.add(RxJava2SingleWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava2MaybeWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava2ObservableWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava2FlowableWrapper.INSTANCE);
        }
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.RXJAVA3)) {
            REACTIVE_WRAPPERS.add(RxJava3SingleWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava3MaybeWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava3ObservableWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(RxJava3FlowableWrapper.INSTANCE);
        }
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.PROJECT_REACTOR)) {
            REACTIVE_WRAPPERS.add(FluxWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(MonoWrapper.INSTANCE);
            REACTIVE_WRAPPERS.add(PublisherWrapper.INSTANCE);
        }
        if (ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.KOTLIN_COROUTINES)) {
            REACTIVE_WRAPPERS.add(FlowWrapper.INSTANCE);
        }
        ReactiveWrapperConverters.registerConvertersIn((ConfigurableConversionService)GENERIC_CONVERSION_SERVICE);
    }

    static class RegistryHolder {
        @Nullable
        static final ReactiveAdapterRegistry REACTIVE_ADAPTER_REGISTRY = ReactiveWrappers.isAvailable(ReactiveWrappers.ReactiveLibrary.PROJECT_REACTOR) ? new ReactiveAdapterRegistry() : null;

        RegistryHolder() {
        }
    }

    private static enum ReactiveAdapterConverterFactory implements ConverterFactory<Object, Object>,
    ConditionalConverter
    {
        INSTANCE;


        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            return this.isSupported(sourceType) || this.isSupported(targetType);
        }

        private boolean isSupported(TypeDescriptor typeDescriptor) {
            return RegistryHolder.REACTIVE_ADAPTER_REGISTRY != null && RegistryHolder.REACTIVE_ADAPTER_REGISTRY.getAdapter(typeDescriptor.getType()) != null;
        }

        public <T> Converter<Object, T> getConverter(Class<T> targetType) {
            return source -> {
                Publisher publisher = source instanceof Publisher ? (Publisher)source : RegistryHolder.REACTIVE_ADAPTER_REGISTRY.getAdapter(Publisher.class, source).toPublisher(source);
                ReactiveAdapter adapter = RegistryHolder.REACTIVE_ADAPTER_REGISTRY.getAdapter(targetType);
                return adapter.fromPublisher(publisher);
            };
        }
    }

    private static enum PublisherToFlowConverter implements Converter<Publisher<?>, Flow<?>>
    {
        INSTANCE;


        @Nonnull
        public Flow<?> convert(Publisher<?> source) {
            return ReactiveFlowKt.asFlow(source);
        }
    }

    private static enum PublisherToMonoConverter implements Converter<Publisher<?>, Mono<?>>
    {
        INSTANCE;


        @Nonnull
        public Mono<?> convert(Publisher<?> source) {
            return Mono.from(source);
        }
    }

    private static enum PublisherToFluxConverter implements Converter<Publisher<?>, Flux<?>>
    {
        INSTANCE;


        @Nonnull
        public Flux<?> convert(Publisher<?> source) {
            return Flux.from(source);
        }
    }

    private static enum RxJava3FlowableWrapper implements ReactiveTypeWrapper<Flowable<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Flowable<?>> getWrapperClass() {
            return Flowable.class;
        }

        public Flowable<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Flowable)wrapper).map(function::apply);
        }
    }

    private static enum RxJava3ObservableWrapper implements ReactiveTypeWrapper<io.reactivex.rxjava3.core.Observable<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.rxjava3.core.Observable<?>> getWrapperClass() {
            return io.reactivex.rxjava3.core.Observable.class;
        }

        public io.reactivex.rxjava3.core.Observable<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.rxjava3.core.Observable)wrapper).map(function::apply);
        }
    }

    private static enum RxJava3MaybeWrapper implements ReactiveTypeWrapper<io.reactivex.rxjava3.core.Maybe<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.rxjava3.core.Maybe<?>> getWrapperClass() {
            return io.reactivex.rxjava3.core.Maybe.class;
        }

        public io.reactivex.rxjava3.core.Maybe<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.rxjava3.core.Maybe)wrapper).map(function::apply);
        }
    }

    private static enum RxJava3SingleWrapper implements ReactiveTypeWrapper<io.reactivex.rxjava3.core.Single<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.rxjava3.core.Single<?>> getWrapperClass() {
            return io.reactivex.rxjava3.core.Single.class;
        }

        public io.reactivex.rxjava3.core.Single<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.rxjava3.core.Single)wrapper).map(function::apply);
        }
    }

    private static enum RxJava2FlowableWrapper implements ReactiveTypeWrapper<io.reactivex.Flowable<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.Flowable<?>> getWrapperClass() {
            return io.reactivex.Flowable.class;
        }

        public io.reactivex.Flowable<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.Flowable)wrapper).map(function::apply);
        }
    }

    private static enum RxJava2ObservableWrapper implements ReactiveTypeWrapper<io.reactivex.Observable<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.Observable<?>> getWrapperClass() {
            return io.reactivex.Observable.class;
        }

        public io.reactivex.Observable<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.Observable)wrapper).map(function::apply);
        }
    }

    private static enum RxJava2MaybeWrapper implements ReactiveTypeWrapper<Maybe<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Maybe<?>> getWrapperClass() {
            return Maybe.class;
        }

        public Maybe<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Maybe)wrapper).map(function::apply);
        }
    }

    private static enum RxJava2SingleWrapper implements ReactiveTypeWrapper<io.reactivex.Single<?>>
    {
        INSTANCE;


        @Override
        public Class<? super io.reactivex.Single<?>> getWrapperClass() {
            return io.reactivex.Single.class;
        }

        public io.reactivex.Single<?> map(Object wrapper, Function<Object, Object> function) {
            return ((io.reactivex.Single)wrapper).map(function::apply);
        }
    }

    private static enum RxJava1ObservableWrapper implements ReactiveTypeWrapper<Observable<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Observable<?>> getWrapperClass() {
            return Observable.class;
        }

        public Observable<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Observable)wrapper).map(function::apply);
        }
    }

    private static enum RxJava1SingleWrapper implements ReactiveTypeWrapper<Single<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Single<?>> getWrapperClass() {
            return Single.class;
        }

        public Single<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Single)wrapper).map(function::apply);
        }
    }

    private static enum PublisherWrapper implements ReactiveTypeWrapper<Publisher<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Publisher<?>> getWrapperClass() {
            return Publisher.class;
        }

        public Publisher<?> map(Object wrapper, Function<Object, Object> function) {
            if (wrapper instanceof Mono) {
                return MonoWrapper.INSTANCE.map(wrapper, function);
            }
            if (wrapper instanceof Flux) {
                return FluxWrapper.INSTANCE.map(wrapper, function);
            }
            return FluxWrapper.INSTANCE.map((Object)Flux.from((Publisher)((Publisher)wrapper)), function);
        }
    }

    private static enum FlowWrapper implements ReactiveTypeWrapper<Flow<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Flow<?>> getWrapperClass() {
            return Flow.class;
        }

        public Flow<?> map(Object wrapper, Function<Object, Object> function) {
            return FlowKt.map((Flow)((Flow)wrapper), (o, continuation) -> function.apply(o));
        }
    }

    private static enum FluxWrapper implements ReactiveTypeWrapper<Flux<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Flux<?>> getWrapperClass() {
            return Flux.class;
        }

        public Flux<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Flux)wrapper).map(function);
        }
    }

    private static enum MonoWrapper implements ReactiveTypeWrapper<Mono<?>>
    {
        INSTANCE;


        @Override
        public Class<? super Mono<?>> getWrapperClass() {
            return Mono.class;
        }

        public Mono<?> map(Object wrapper, Function<Object, Object> function) {
            return ((Mono)wrapper).map(function::apply);
        }
    }

    private static interface ReactiveTypeWrapper<T> {
        public Class<? super T> getWrapperClass();

        public Object map(Object var1, Function<Object, Object> var2);
    }
}

