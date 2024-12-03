/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.ReactiveAdapter
 *  org.springframework.core.ReactiveAdapterRegistry
 *  org.springframework.core.ReactiveTypeDescriptor
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 */
package org.springframework.data.repository.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.ReactiveTypeDescriptor;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.util.ProxyUtils;
import org.springframework.data.util.ReflectionUtils;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

public abstract class ReactiveWrappers {
    private static final boolean PROJECT_REACTOR_PRESENT = ClassUtils.isPresent((String)"reactor.core.publisher.Flux", (ClassLoader)ReactiveWrappers.class.getClassLoader());
    @Deprecated
    private static final boolean RXJAVA1_PRESENT = ClassUtils.isPresent((String)"rx.Observable", (ClassLoader)ReactiveWrappers.class.getClassLoader()) && ClassUtils.isPresent((String)"rx.RxReactiveStreams", (ClassLoader)ReactiveWrappers.class.getClassLoader());
    private static final boolean RXJAVA2_PRESENT = ClassUtils.isPresent((String)"io.reactivex.Flowable", (ClassLoader)ReactiveWrappers.class.getClassLoader());
    private static final boolean RXJAVA3_PRESENT = ClassUtils.isPresent((String)"io.reactivex.rxjava3.core.Flowable", (ClassLoader)ReactiveWrappers.class.getClassLoader());
    private static final boolean KOTLIN_COROUTINES_PRESENT = ClassUtils.isPresent((String)"kotlinx.coroutines.reactor.MonoKt", (ClassLoader)ReactiveWrappers.class.getClassLoader());

    private ReactiveWrappers() {
    }

    public static boolean isAvailable() {
        return Arrays.stream(ReactiveLibrary.values()).anyMatch(ReactiveWrappers::isAvailable);
    }

    public static boolean isAvailable(ReactiveLibrary reactiveLibrary) {
        Assert.notNull((Object)((Object)reactiveLibrary), (String)"Reactive library must not be null!");
        switch (reactiveLibrary) {
            case PROJECT_REACTOR: {
                return PROJECT_REACTOR_PRESENT;
            }
            case RXJAVA1: {
                return RXJAVA1_PRESENT;
            }
            case RXJAVA2: {
                return RXJAVA2_PRESENT;
            }
            case RXJAVA3: {
                return RXJAVA3_PRESENT;
            }
            case KOTLIN_COROUTINES: {
                return PROJECT_REACTOR_PRESENT && KOTLIN_COROUTINES_PRESENT;
            }
        }
        throw new IllegalArgumentException(String.format("Reactive library %s not supported", new Object[]{reactiveLibrary}));
    }

    public static boolean supports(Class<?> type) {
        return ReactiveWrappers.isAvailable() && ReactiveWrappers.isWrapper(ProxyUtils.getUserClass(type));
    }

    public static boolean usesReactiveType(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return Arrays.stream(type.getMethods()).flatMap(ReflectionUtils::returnTypeAndParameters).anyMatch(ReactiveWrappers::supports);
    }

    public static boolean isNoValueType(Class<?> type) {
        Assert.notNull(type, (String)"Candidate type must not be null!");
        return ReactiveWrappers.findDescriptor(type).map(ReactiveTypeDescriptor::isNoValue).orElse(false);
    }

    public static boolean isSingleValueType(Class<?> type) {
        Assert.notNull(type, (String)"Candidate type must not be null!");
        return ReactiveWrappers.findDescriptor(type).map(it -> !it.isMultiValue() && !it.isNoValue()).orElse(false);
    }

    public static boolean isMultiValueType(Class<?> type) {
        Assert.notNull(type, (String)"Candidate type must not be null!");
        return ReactiveWrappers.isSingleValueType(type) ? false : ReactiveWrappers.findDescriptor(type).map(ReactiveTypeDescriptor::isMultiValue).orElse(false);
    }

    @Deprecated
    public static Collection<Class<?>> getNoValueTypes() {
        return Collections.emptyList();
    }

    @Deprecated
    public static Collection<Class<?>> getSingleValueTypes() {
        return Collections.emptyList();
    }

    @Deprecated
    public static Collection<Class<?>> getMultiValueTypes() {
        return Collections.emptyList();
    }

    private static boolean isWrapper(Class<?> type) {
        Assert.notNull(type, (String)"Candidate type must not be null!");
        return ReactiveWrappers.isNoValueType(type) || ReactiveWrappers.isSingleValueType(type) || ReactiveWrappers.isMultiValueType(type);
    }

    private static Optional<ReactiveTypeDescriptor> findDescriptor(Class<?> type) {
        Assert.notNull(type, (String)"Wrapper type must not be null!");
        ReactiveAdapterRegistry adapterRegistry = ReactiveWrapperConverters.RegistryHolder.REACTIVE_ADAPTER_REGISTRY;
        if (adapterRegistry == null) {
            return Optional.empty();
        }
        ReactiveAdapter adapter = adapterRegistry.getAdapter(type);
        if (adapter != null && adapter.getDescriptor().isDeferred()) {
            return Optional.of(adapter.getDescriptor());
        }
        return Optional.empty();
    }

    public static enum ReactiveLibrary {
        PROJECT_REACTOR,
        RXJAVA1,
        RXJAVA2,
        RXJAVA3,
        KOTLIN_COROUTINES;

    }
}

