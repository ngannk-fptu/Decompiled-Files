/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 */
package org.springframework.data.repository.core.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.FragmentNotImplementedException;
import org.springframework.data.repository.core.support.MethodLookup;
import org.springframework.data.repository.core.support.MethodLookups;
import org.springframework.data.repository.core.support.RepositoryFragment;
import org.springframework.data.repository.core.support.RepositoryInvocationMulticaster;
import org.springframework.data.repository.core.support.RepositoryMethodInvoker;
import org.springframework.data.repository.util.ReactiveWrapperConverters;
import org.springframework.data.repository.util.ReactiveWrappers;
import org.springframework.data.util.Streamable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

public class RepositoryComposition {
    private static final BiFunction<Method, Object[], Object[]> PASSTHRU_ARG_CONVERTER = (methodParameter, o) -> o;
    private static final BiFunction<Method, Object[], Object[]> REACTIVE_ARGS_CONVERTER = (method, args) -> {
        if (ReactiveWrappers.isAvailable()) {
            Class<?>[] parameterTypes = method.getParameterTypes();
            Object[] converted = new Object[((Object[])args).length];
            for (int i = 0; i < ((Object[])args).length; ++i) {
                Class<?> parameterType;
                Object value;
                Object convertedArg = value = args[i];
                Class<?> clazz = parameterType = parameterTypes.length > i ? parameterTypes[i] : null;
                if (value != null && parameterType != null && !parameterType.isAssignableFrom(value.getClass()) && ReactiveWrapperConverters.canConvert(value.getClass(), parameterType)) {
                    convertedArg = ReactiveWrapperConverters.toWrapper(value, parameterType);
                }
                converted[i] = convertedArg;
            }
            return converted;
        }
        return args;
    };
    private static final RepositoryComposition EMPTY = new RepositoryComposition(null, RepositoryFragments.empty(), MethodLookups.direct(), PASSTHRU_ARG_CONVERTER);
    private final Map<Method, Method> methodCache = new ConcurrentReferenceHashMap();
    private final RepositoryFragments fragments;
    private final MethodLookup methodLookup;
    private final BiFunction<Method, Object[], Object[]> argumentConverter;
    @Nullable
    private final RepositoryMetadata metadata;

    private RepositoryComposition(@Nullable RepositoryMetadata metadata, RepositoryFragments fragments, MethodLookup methodLookup, BiFunction<Method, Object[], Object[]> argumentConverter) {
        this.metadata = metadata;
        this.fragments = fragments;
        this.methodLookup = methodLookup;
        this.argumentConverter = argumentConverter;
    }

    public static RepositoryComposition empty() {
        return EMPTY;
    }

    public static RepositoryComposition fromMetadata(RepositoryMetadata metadata) {
        if (metadata.isReactiveRepository()) {
            return new RepositoryComposition(metadata, RepositoryFragments.empty(), MethodLookups.forReactiveTypes(metadata), REACTIVE_ARGS_CONVERTER);
        }
        return new RepositoryComposition(metadata, RepositoryFragments.empty(), MethodLookups.forRepositoryTypes(metadata), PASSTHRU_ARG_CONVERTER);
    }

    public static RepositoryComposition just(Object implementation) {
        return new RepositoryComposition(null, RepositoryFragments.just(implementation), MethodLookups.direct(), PASSTHRU_ARG_CONVERTER);
    }

    public static RepositoryComposition of(RepositoryFragment<?> ... fragments) {
        return RepositoryComposition.of(Arrays.asList(fragments));
    }

    public static RepositoryComposition of(List<RepositoryFragment<?>> fragments) {
        return new RepositoryComposition(null, RepositoryFragments.from(fragments), MethodLookups.direct(), PASSTHRU_ARG_CONVERTER);
    }

    public static RepositoryComposition of(RepositoryFragments fragments) {
        return new RepositoryComposition(null, fragments, MethodLookups.direct(), PASSTHRU_ARG_CONVERTER);
    }

    public RepositoryComposition append(RepositoryFragment<?> fragment) {
        return new RepositoryComposition(this.metadata, this.fragments.append(fragment), this.methodLookup, this.argumentConverter);
    }

    public RepositoryComposition append(RepositoryFragments fragments) {
        return new RepositoryComposition(this.metadata, this.fragments.append(fragments), this.methodLookup, this.argumentConverter);
    }

    public RepositoryComposition withArgumentConverter(BiFunction<Method, Object[], Object[]> argumentConverter) {
        return new RepositoryComposition(this.metadata, this.fragments, this.methodLookup, argumentConverter);
    }

    public RepositoryComposition withMethodLookup(MethodLookup methodLookup) {
        return new RepositoryComposition(this.metadata, this.fragments, methodLookup, this.argumentConverter);
    }

    public RepositoryComposition withMetadata(RepositoryMetadata metadata) {
        return new RepositoryComposition(metadata, this.fragments, this.methodLookup, this.argumentConverter);
    }

    public boolean isEmpty() {
        return this.fragments.isEmpty();
    }

    public Object invoke(Method method, Object ... args) throws Throwable {
        return this.invoke(RepositoryInvocationMulticaster.NoOpRepositoryInvocationMulticaster.INSTANCE, method, args);
    }

    Object invoke(RepositoryInvocationMulticaster listener, Method method, Object[] args) throws Throwable {
        Method methodToCall = this.getMethod(method);
        if (methodToCall == null) {
            throw new IllegalArgumentException(String.format("No fragment found for method %s", method));
        }
        ReflectionUtils.makeAccessible((Method)methodToCall);
        return this.fragments.invoke(this.metadata != null ? this.metadata.getRepositoryInterface() : method.getDeclaringClass(), listener, method, methodToCall, this.argumentConverter.apply(methodToCall, args));
    }

    public Optional<Method> findMethod(Method method) {
        return Optional.ofNullable(this.getMethod(method));
    }

    @Nullable
    Method getMethod(Method method) {
        return this.methodCache.computeIfAbsent(method, key -> RepositoryFragments.findMethod(MethodLookup.InvokedMethod.of(key), this.methodLookup, this.fragments::methods));
    }

    public void validateImplementation() {
        this.fragments.stream().forEach(it -> it.getImplementation().orElseThrow(() -> {
            Class<Object> repositoryInterface = this.metadata != null ? this.metadata.getRepositoryInterface() : Object.class;
            return new FragmentNotImplementedException(String.format("Fragment %s used in %s has no implementation.", ClassUtils.getQualifiedName(it.getSignatureContributor()), ClassUtils.getQualifiedName(repositoryInterface)), repositoryInterface, (RepositoryFragment<?>)it);
        }));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof RepositoryComposition)) {
            return false;
        }
        RepositoryComposition that = (RepositoryComposition)o;
        return ObjectUtils.nullSafeEquals((Object)this.fragments, (Object)that.fragments);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.fragments);
    }

    public RepositoryFragments getFragments() {
        return this.fragments;
    }

    public MethodLookup getMethodLookup() {
        return this.methodLookup;
    }

    public BiFunction<Method, Object[], Object[]> getArgumentConverter() {
        return this.argumentConverter;
    }

    public static class RepositoryFragments
    implements Streamable<RepositoryFragment<?>> {
        static final RepositoryFragments EMPTY = new RepositoryFragments(Collections.emptyList());
        private final Map<Method, RepositoryFragment<?>> fragmentCache = new ConcurrentReferenceHashMap();
        private final Map<Method, RepositoryMethodInvoker> invocationMetadataCache = new ConcurrentHashMap<Method, RepositoryMethodInvoker>();
        private final List<RepositoryFragment<?>> fragments;

        private RepositoryFragments(List<RepositoryFragment<?>> fragments) {
            this.fragments = fragments;
        }

        public static RepositoryFragments empty() {
            return EMPTY;
        }

        public static RepositoryFragments just(Object ... implementations) {
            Assert.notNull((Object)implementations, (String)"Implementations must not be null!");
            Assert.noNullElements((Object[])implementations, (String)"Implementations must not contain null elements!");
            return new RepositoryFragments(Arrays.stream(implementations).map(RepositoryFragment::implemented).collect(Collectors.toList()));
        }

        public static RepositoryFragments of(RepositoryFragment<?> ... fragments) {
            Assert.notNull(fragments, (String)"RepositoryFragments must not be null!");
            Assert.noNullElements((Object[])fragments, (String)"RepositoryFragments must not contain null elements!");
            return new RepositoryFragments(Arrays.asList(fragments));
        }

        public static RepositoryFragments from(List<RepositoryFragment<?>> fragments) {
            Assert.notNull(fragments, (String)"RepositoryFragments must not be null!");
            return new RepositoryFragments(new ArrayList(fragments));
        }

        public RepositoryFragments append(RepositoryFragment<?> fragment) {
            Assert.notNull(fragment, (String)"RepositoryFragment must not be null!");
            return RepositoryFragments.concat(this.stream(), Stream.of(fragment));
        }

        public RepositoryFragments append(RepositoryFragments fragments) {
            Assert.notNull((Object)fragments, (String)"RepositoryFragments must not be null!");
            return RepositoryFragments.concat(this.stream(), fragments.stream());
        }

        private static RepositoryFragments concat(Stream<RepositoryFragment<?>> left, Stream<RepositoryFragment<?>> right) {
            return RepositoryFragments.from(Stream.concat(left, right).collect(Collectors.toList()));
        }

        @Override
        public Iterator<RepositoryFragment<?>> iterator() {
            return this.fragments.iterator();
        }

        public Stream<Method> methods() {
            return this.stream().flatMap(RepositoryFragment::methods);
        }

        @Nullable
        public Object invoke(Method invokedMethod, Method methodToCall, Object[] args) throws Throwable {
            return this.invoke(null, RepositoryInvocationMulticaster.NoOpRepositoryInvocationMulticaster.INSTANCE, invokedMethod, methodToCall, args);
        }

        @Nullable
        Object invoke(Class<?> repositoryInterface, RepositoryInvocationMulticaster listener, Method invokedMethod, Method methodToCall, Object[] args) throws Throwable {
            RepositoryFragment fragment = this.fragmentCache.computeIfAbsent(methodToCall, this::findImplementationFragment);
            Optional optional = fragment.getImplementation();
            if (!optional.isPresent()) {
                throw new IllegalArgumentException(String.format("No implementation found for method %s", methodToCall));
            }
            RepositoryMethodInvoker repositoryMethodInvoker = this.invocationMetadataCache.get(invokedMethod);
            if (repositoryMethodInvoker == null) {
                repositoryMethodInvoker = RepositoryMethodInvoker.forFragmentMethod(invokedMethod, optional.get(), methodToCall);
                this.invocationMetadataCache.put(invokedMethod, repositoryMethodInvoker);
            }
            return repositoryMethodInvoker.invoke(repositoryInterface, listener, args);
        }

        private RepositoryFragment<?> findImplementationFragment(Method key) {
            return this.stream().filter(it -> it.hasMethod(key)).filter(it -> it.getImplementation().isPresent()).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No fragment found for method %s", key)));
        }

        @Nullable
        private static Method findMethod(MethodLookup.InvokedMethod invokedMethod, MethodLookup lookup, Supplier<Stream<Method>> methodStreamSupplier) {
            for (MethodLookup.MethodPredicate methodPredicate : lookup.getLookups()) {
                Optional<Method> resolvedMethod = methodStreamSupplier.get().filter(it -> methodPredicate.test(invokedMethod, (Method)it)).findFirst();
                if (!resolvedMethod.isPresent()) continue;
                return resolvedMethod.get();
            }
            return null;
        }

        public int size() {
            return this.fragments.size();
        }

        public String toString() {
            return this.fragments.toString();
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof RepositoryFragments)) {
                return false;
            }
            RepositoryFragments that = (RepositoryFragments)o;
            if (!ObjectUtils.nullSafeEquals(this.fragmentCache, that.fragmentCache)) {
                return false;
            }
            if (!ObjectUtils.nullSafeEquals(this.invocationMetadataCache, that.invocationMetadataCache)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.fragments, that.fragments);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.fragmentCache);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.invocationMetadataCache);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.fragments);
            return result;
        }
    }
}

