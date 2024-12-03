/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.aopalliance.aop.Advice
 *  org.aopalliance.intercept.MethodInterceptor
 *  org.aopalliance.intercept.MethodInvocation
 *  org.springframework.aop.framework.ProxyFactory
 *  org.springframework.core.CollectionFactory
 *  org.springframework.core.ResolvableType
 *  org.springframework.lang.NonNull
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.ReflectionUtils
 *  org.springframework.util.StringUtils
 */
package org.springframework.data.util;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.core.CollectionFactory;
import org.springframework.core.ResolvableType;
import org.springframework.data.util.Optionals;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

public class MethodInvocationRecorder {
    public static PropertyNameDetectionStrategy DEFAULT = DefaultPropertyNameDetectionStrategy.INSTANCE;
    private Optional<RecordingMethodInterceptor> interceptor;

    private MethodInvocationRecorder() {
        this(Optional.empty());
    }

    private MethodInvocationRecorder(Optional<RecordingMethodInterceptor> interceptor) {
        this.interceptor = interceptor;
    }

    public static <T> Recorded<T> forProxyOf(Class<T> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        Assert.isTrue((!Modifier.isFinal(type.getModifiers()) ? 1 : 0) != 0, (String)"Type to record invocations on must not be final!");
        return new MethodInvocationRecorder().create(type);
    }

    private <T> Recorded<T> create(Class<T> type) {
        RecordingMethodInterceptor interceptor = new RecordingMethodInterceptor();
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.addAdvice((Advice)interceptor);
        if (!type.isInterface()) {
            proxyFactory.setTargetClass(type);
            proxyFactory.setProxyTargetClass(true);
        } else {
            proxyFactory.addInterface(type);
        }
        Object proxy = proxyFactory.getProxy(type.getClassLoader());
        return new Recorded<Object>(proxy, new MethodInvocationRecorder(Optional.ofNullable(interceptor)));
    }

    private Optional<String> getPropertyPath(List<PropertyNameDetectionStrategy> strategies) {
        return this.interceptor.flatMap(it -> ((RecordingMethodInterceptor)it).getPropertyPath(strategies));
    }

    static class Unrecorded
    extends Recorded<Object> {
        private Unrecorded() {
            super(null, null);
        }

        @Override
        public Optional<String> getPropertyPath(List<PropertyNameDetectionStrategy> strategies) {
            return Optional.empty();
        }
    }

    public static class Recorded<T> {
        @Nullable
        private final T currentInstance;
        @Nullable
        private final MethodInvocationRecorder recorder;

        Recorded(@Nullable T currentInstance, @Nullable MethodInvocationRecorder recorder) {
            this.currentInstance = currentInstance;
            this.recorder = recorder;
        }

        public Optional<String> getPropertyPath() {
            return this.getPropertyPath(DEFAULT);
        }

        public Optional<String> getPropertyPath(PropertyNameDetectionStrategy strategy) {
            MethodInvocationRecorder recorder = this.recorder;
            return recorder == null ? Optional.empty() : recorder.getPropertyPath(Arrays.asList(strategy));
        }

        public Optional<String> getPropertyPath(List<PropertyNameDetectionStrategy> strategies) {
            MethodInvocationRecorder recorder = this.recorder;
            return recorder == null ? Optional.empty() : recorder.getPropertyPath(strategies);
        }

        public <S> Recorded<S> record(Function<? super T, S> converter) {
            Assert.notNull(converter, (String)"Function must not be null!");
            return new Recorded<S>(converter.apply(this.currentInstance), this.recorder);
        }

        public <S> Recorded<S> record(ToCollectionConverter<T, S> converter) {
            Assert.notNull(converter, (String)"Converter must not be null!");
            return new Recorded(((Collection)converter.apply(this.currentInstance)).iterator().next(), this.recorder);
        }

        public <S> Recorded<S> record(ToMapConverter<T, S> converter) {
            Assert.notNull(converter, (String)"Converter must not be null!");
            return new Recorded(((Map)converter.apply(this.currentInstance)).values().iterator().next(), this.recorder);
        }

        public String toString() {
            return "MethodInvocationRecorder.Recorded(currentInstance=" + this.currentInstance + ", recorder=" + this.recorder + ")";
        }

        public static interface ToMapConverter<T, S>
        extends Function<T, Map<?, S>> {
        }

        public static interface ToCollectionConverter<T, S>
        extends Function<T, Collection<S>> {
        }
    }

    private static enum DefaultPropertyNameDetectionStrategy implements PropertyNameDetectionStrategy
    {
        INSTANCE;


        @Override
        @NonNull
        public String getPropertyName(Method method) {
            return DefaultPropertyNameDetectionStrategy.getPropertyName(method.getReturnType(), method.getName());
        }

        private static String getPropertyName(Class<?> type, String methodName) {
            String pattern = DefaultPropertyNameDetectionStrategy.getPatternFor(type);
            String replaced = methodName.replaceFirst(pattern, "");
            return StringUtils.uncapitalize((String)replaced);
        }

        private static String getPatternFor(Class<?> type) {
            return type.equals(Boolean.TYPE) ? "^(is)" : "^(get|set)";
        }
    }

    public static interface PropertyNameDetectionStrategy {
        @Nullable
        public String getPropertyName(Method var1);
    }

    private static final class InvocationInformation {
        private static final InvocationInformation NOT_INVOKED = new InvocationInformation(new Unrecorded(), null);
        private final Recorded<?> recorded;
        @Nullable
        private final Method invokedMethod;

        public InvocationInformation(Recorded<?> recorded, @Nullable Method invokedMethod) {
            Assert.notNull(recorded, (String)"Recorded must not be null!");
            this.recorded = recorded;
            this.invokedMethod = invokedMethod;
        }

        @Nullable
        Object getCurrentInstance() {
            return ((Recorded)this.recorded).currentInstance;
        }

        Optional<String> getPropertyPath(List<PropertyNameDetectionStrategy> strategies) {
            Method invokedMethod = this.invokedMethod;
            if (invokedMethod == null) {
                return Optional.empty();
            }
            String propertyName = InvocationInformation.getPropertyName(invokedMethod, strategies);
            Optional<String> next = this.recorded.getPropertyPath(strategies);
            return Optionals.firstNonEmpty(() -> next.map(it -> propertyName.concat(".").concat((String)it)), () -> Optional.of(propertyName));
        }

        private static String getPropertyName(Method invokedMethod, List<PropertyNameDetectionStrategy> strategies) {
            return strategies.stream().map(it -> it.getPropertyName(invokedMethod)).findFirst().orElseThrow(() -> new IllegalArgumentException(String.format("No property name found for method %s!", invokedMethod)));
        }

        public Recorded<?> getRecorded() {
            return this.recorded;
        }

        @Nullable
        public Method getInvokedMethod() {
            return this.invokedMethod;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof InvocationInformation)) {
                return false;
            }
            InvocationInformation that = (InvocationInformation)o;
            if (!ObjectUtils.nullSafeEquals(this.recorded, that.recorded)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals((Object)this.invokedMethod, (Object)that.invokedMethod);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.recorded);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)this.invokedMethod);
            return result;
        }

        public String toString() {
            return "MethodInvocationRecorder.InvocationInformation(recorded=" + this.getRecorded() + ", invokedMethod=" + this.getInvokedMethod() + ")";
        }

        static /* synthetic */ InvocationInformation access$100() {
            return NOT_INVOKED;
        }
    }

    private class RecordingMethodInterceptor
    implements MethodInterceptor {
        private InvocationInformation information = InvocationInformation.access$100();

        private RecordingMethodInterceptor() {
        }

        public Object invoke(MethodInvocation invocation) throws Throwable {
            Method method = invocation.getMethod();
            Object[] arguments = invocation.getArguments();
            if (ReflectionUtils.isObjectMethod((Method)method)) {
                return method.invoke((Object)this, arguments);
            }
            ResolvableType type = ResolvableType.forMethodReturnType((Method)method);
            Class rawType = type.resolve(Object.class);
            if (Collection.class.isAssignableFrom(rawType)) {
                Class clazz = type.getGeneric(new int[]{0}).resolve(Object.class);
                InvocationInformation information = this.registerInvocation(method, clazz);
                Collection collection = CollectionFactory.createCollection((Class)rawType, (int)1);
                collection.add(information.getCurrentInstance());
                return collection;
            }
            if (Map.class.isAssignableFrom(rawType)) {
                Class clazz = type.getGeneric(new int[]{1}).resolve(Object.class);
                InvocationInformation information = this.registerInvocation(method, clazz);
                Map map = CollectionFactory.createMap((Class)rawType, (int)1);
                map.put("_key_", information.getCurrentInstance());
                return map;
            }
            return this.registerInvocation(method, rawType).getCurrentInstance();
        }

        private Optional<String> getPropertyPath(List<PropertyNameDetectionStrategy> strategies) {
            return this.information.getPropertyPath(strategies);
        }

        private InvocationInformation registerInvocation(Method method, Class<?> proxyType) {
            InvocationInformation information;
            Recorded create = Modifier.isFinal(proxyType.getModifiers()) ? new Unrecorded() : MethodInvocationRecorder.this.create(proxyType);
            this.information = information = new InvocationInformation(create, method);
            return this.information;
        }
    }
}

