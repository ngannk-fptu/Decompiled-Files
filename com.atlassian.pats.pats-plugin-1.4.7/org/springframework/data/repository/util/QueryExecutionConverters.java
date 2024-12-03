/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.vavr.collection.Seq
 *  io.vavr.collection.Traversable
 *  io.vavr.control.Try
 *  org.springframework.core.convert.ConversionService
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.core.convert.support.ConfigurableConversionService
 *  org.springframework.core.convert.support.DefaultConversionService
 *  org.springframework.lang.Nullable
 *  org.springframework.scheduling.annotation.AsyncResult
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  org.springframework.util.concurrent.ListenableFuture
 */
package org.springframework.data.repository.util;

import io.vavr.collection.Seq;
import io.vavr.collection.Traversable;
import io.vavr.control.Try;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.stream.Stream;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.repository.util.VavrCollections;
import org.springframework.data.util.NullableWrapper;
import org.springframework.data.util.NullableWrapperConverters;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import org.springframework.util.concurrent.ListenableFuture;

public abstract class QueryExecutionConverters {
    private static final boolean VAVR_PRESENT = ClassUtils.isPresent((String)"io.vavr.control.Try", (ClassLoader)QueryExecutionConverters.class.getClassLoader());
    private static final Set<WrapperType> WRAPPER_TYPES = new HashSet<WrapperType>();
    private static final Set<WrapperType> UNWRAPPER_TYPES = new HashSet<WrapperType>();
    private static final Set<Converter<Object, Object>> UNWRAPPERS = new HashSet<Converter<Object, Object>>();
    private static final Set<Class<?>> ALLOWED_PAGEABLE_TYPES = new HashSet();
    private static final Map<Class<?>, ExecutionAdapter> EXECUTION_ADAPTER = new HashMap();
    private static final Map<Class<?>, Boolean> supportsCache = new ConcurrentReferenceHashMap();

    private QueryExecutionConverters() {
    }

    public static boolean supports(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return supportsCache.computeIfAbsent(type, key -> {
            for (WrapperType candidate : WRAPPER_TYPES) {
                if (!candidate.getType().isAssignableFrom((Class<?>)key)) continue;
                return true;
            }
            return NullableWrapperConverters.supports(type);
        });
    }

    public static boolean supportsUnwrapping(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        if (NullableWrapperConverters.supportsUnwrapping(type)) {
            return NullableWrapperConverters.supportsUnwrapping(type);
        }
        for (WrapperType candidate : UNWRAPPER_TYPES) {
            if (!candidate.getType().isAssignableFrom(type)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSingleValue(Class<?> type) {
        if (NullableWrapperConverters.supports(type)) {
            return NullableWrapperConverters.isSingleValue(type);
        }
        for (WrapperType candidate : WRAPPER_TYPES) {
            if (!candidate.getType().isAssignableFrom(type)) continue;
            return candidate.isSingleValue();
        }
        return false;
    }

    public static Set<Class<?>> getAllowedPageableTypes() {
        return Collections.unmodifiableSet(ALLOWED_PAGEABLE_TYPES);
    }

    public static void registerConvertersIn(ConfigurableConversionService conversionService) {
        Assert.notNull((Object)conversionService, (String)"ConversionService must not be null!");
        conversionService.removeConvertible(Collection.class, Object.class);
        NullableWrapperConverters.registerConvertersIn((ConverterRegistry)conversionService);
        if (VAVR_PRESENT) {
            conversionService.addConverter((GenericConverter)VavrCollections.FromJavaConverter.INSTANCE);
        }
        conversionService.addConverter((GenericConverter)new NullableWrapperToCompletableFutureConverter());
        conversionService.addConverter((GenericConverter)new NullableWrapperToFutureConverter());
        conversionService.addConverter((GenericConverter)new IterableToStreamableConverter());
    }

    @Nullable
    public static Object unwrap(@Nullable Object source) {
        if ((source = NullableWrapperConverters.unwrap(source)) == null || !QueryExecutionConverters.supports(source.getClass())) {
            return source;
        }
        for (Converter<Object, Object> converter : UNWRAPPERS) {
            Object result = converter.convert(source);
            if (result == source) continue;
            return result;
        }
        return source;
    }

    public static TypeInformation<?> unwrapWrapperTypes(TypeInformation<?> type) {
        Assert.notNull(type, (String)"type must not be null");
        Class<?> rawType = type.getType();
        boolean needToUnwrap = type.isCollectionLike() || Slice.class.isAssignableFrom(rawType) || GeoResults.class.isAssignableFrom(rawType) || rawType.isArray() || QueryExecutionConverters.supports(rawType) || Stream.class.isAssignableFrom(rawType);
        return needToUnwrap ? QueryExecutionConverters.unwrapWrapperTypes(type.getRequiredComponentType()) : type;
    }

    @Nullable
    public static ExecutionAdapter getExecutionAdapter(Class<?> returnType) {
        Assert.notNull(returnType, (String)"Return type must not be null!");
        return EXECUTION_ADAPTER.get(returnType);
    }

    static {
        WRAPPER_TYPES.add(WrapperType.singleValue(Future.class));
        UNWRAPPER_TYPES.add(WrapperType.singleValue(Future.class));
        WRAPPER_TYPES.add(WrapperType.singleValue(ListenableFuture.class));
        UNWRAPPER_TYPES.add(WrapperType.singleValue(ListenableFuture.class));
        ALLOWED_PAGEABLE_TYPES.add(Slice.class);
        ALLOWED_PAGEABLE_TYPES.add(Page.class);
        ALLOWED_PAGEABLE_TYPES.add(List.class);
        WRAPPER_TYPES.add(NullableWrapperToCompletableFutureConverter.getWrapperType());
        if (VAVR_PRESENT) {
            WRAPPER_TYPES.add(VavrCollections.ToJavaConverter.INSTANCE.getWrapperType());
            UNWRAPPERS.add(VavrTraversableUnwrapper.INSTANCE);
            WRAPPER_TYPES.add(WrapperType.singleValue(Try.class));
            EXECUTION_ADAPTER.put(Try.class, it -> Try.of(it::get));
            ALLOWED_PAGEABLE_TYPES.add(Seq.class);
        }
    }

    public static final class WrapperType {
        private final Class<?> type;
        private final Cardinality cardinality;

        private WrapperType(Class<?> type, Cardinality cardinality) {
            this.type = type;
            this.cardinality = cardinality;
        }

        public Class<?> getType() {
            return this.type;
        }

        public Cardinality getCardinality() {
            return this.cardinality;
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof WrapperType)) {
                return false;
            }
            WrapperType that = (WrapperType)o;
            if (!ObjectUtils.nullSafeEquals(this.type, that.type)) {
                return false;
            }
            return this.cardinality == that.cardinality;
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode(this.type);
            result = 31 * result + ObjectUtils.nullSafeHashCode((Object)((Object)this.cardinality));
            return result;
        }

        public String toString() {
            return "QueryExecutionConverters.WrapperType(type=" + this.getType() + ", cardinality=" + (Object)((Object)this.getCardinality()) + ")";
        }

        public static WrapperType singleValue(Class<?> type) {
            return new WrapperType(type, Cardinality.SINGLE);
        }

        public static WrapperType multiValue(Class<?> type) {
            return new WrapperType(type, Cardinality.MULTI);
        }

        public static WrapperType noValue(Class<?> type) {
            return new WrapperType(type, Cardinality.NONE);
        }

        boolean isSingleValue() {
            return this.cardinality.equals((Object)Cardinality.SINGLE);
        }

        static enum Cardinality {
            NONE,
            SINGLE,
            MULTI;

        }
    }

    private static class IterableToStreamableConverter
    implements ConditionalGenericConverter {
        private static final TypeDescriptor STREAMABLE = TypeDescriptor.valueOf(Streamable.class);
        private final Map<TypeDescriptor, Boolean> targetTypeCache = new ConcurrentHashMap<TypeDescriptor, Boolean>();
        private final ConversionService conversionService = DefaultConversionService.getSharedInstance();

        IterableToStreamableConverter() {
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(new GenericConverter.ConvertiblePair(Iterable.class, Object.class));
        }

        public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (sourceType.isAssignableTo(targetType)) {
                return false;
            }
            if (!Iterable.class.isAssignableFrom(sourceType.getType())) {
                return false;
            }
            if (Streamable.class.equals((Object)targetType.getType())) {
                return true;
            }
            return this.targetTypeCache.computeIfAbsent(targetType, it -> this.conversionService.canConvert(STREAMABLE, targetType));
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            Streamable streamable = source == null ? Streamable.empty() : Streamable.of((Iterable)Iterable.class.cast(source));
            return Streamable.class.equals((Object)targetType.getType()) ? streamable : this.conversionService.convert(streamable, STREAMABLE, targetType);
        }
    }

    private static enum VavrTraversableUnwrapper implements Converter<Object, Object>
    {
        INSTANCE;


        @Nullable
        public Object convert(Object source) {
            if (source instanceof Traversable) {
                return VavrCollections.ToJavaConverter.INSTANCE.convert(source);
            }
            return source;
        }
    }

    private static class NullableWrapperToCompletableFutureConverter
    extends AbstractWrapperTypeConverter {
        NullableWrapperToCompletableFutureConverter() {
            super(CompletableFuture.completedFuture(null));
        }

        @Override
        protected Object wrap(Object source) {
            return source instanceof CompletableFuture ? source : CompletableFuture.completedFuture(source);
        }

        static WrapperType getWrapperType() {
            return WrapperType.singleValue(CompletableFuture.class);
        }
    }

    private static class NullableWrapperToFutureConverter
    extends AbstractWrapperTypeConverter {
        NullableWrapperToFutureConverter() {
            super(new AsyncResult(null), Arrays.asList(Future.class, ListenableFuture.class));
        }

        @Override
        protected Object wrap(Object source) {
            return new AsyncResult(source);
        }
    }

    private static abstract class AbstractWrapperTypeConverter
    implements GenericConverter {
        private final Object nullValue;
        private final Iterable<Class<?>> wrapperTypes;

        AbstractWrapperTypeConverter(Object nullValue) {
            Assert.notNull((Object)nullValue, (String)"Null value must not be null!");
            this.nullValue = nullValue;
            this.wrapperTypes = Collections.singleton(nullValue.getClass());
        }

        AbstractWrapperTypeConverter(Object nullValue, Iterable<Class<?>> wrapperTypes) {
            this.nullValue = nullValue;
            this.wrapperTypes = wrapperTypes;
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Streamable.of(this.wrapperTypes).map(it -> new GenericConverter.ConvertiblePair(NullableWrapper.class, it)).stream().collect(StreamUtils.toUnmodifiableSet());
        }

        @Nullable
        public final Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            if (source == null) {
                return null;
            }
            NullableWrapper wrapper = (NullableWrapper)source;
            Object value = wrapper.getValue();
            return value == null ? this.nullValue : this.wrap(value);
        }

        protected abstract Object wrap(Object var1);
    }

    public static interface ExecutionAdapter {
        public Object apply(ThrowingSupplier var1) throws Throwable;
    }

    public static interface ThrowingSupplier {
        public Object get() throws Throwable;
    }
}

