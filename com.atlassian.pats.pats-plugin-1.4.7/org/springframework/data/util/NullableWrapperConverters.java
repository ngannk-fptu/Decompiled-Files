/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  io.vavr.control.Option
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.ConverterRegistry
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ClassUtils
 *  org.springframework.util.ConcurrentReferenceHashMap
 *  org.springframework.util.ObjectUtils
 *  scala.Function0
 *  scala.Option
 *  scala.runtime.AbstractFunction0
 */
package org.springframework.data.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterRegistry;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.util.NullableWrapper;
import org.springframework.data.util.StreamUtils;
import org.springframework.data.util.Streamable;
import org.springframework.data.util.TypeInformation;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.springframework.util.ObjectUtils;
import scala.Function0;
import scala.Option;
import scala.runtime.AbstractFunction0;

public abstract class NullableWrapperConverters {
    private static final boolean GUAVA_PRESENT = ClassUtils.isPresent((String)"com.google.common.base.Optional", (ClassLoader)NullableWrapperConverters.class.getClassLoader());
    private static final boolean SCALA_PRESENT = ClassUtils.isPresent((String)"scala.Option", (ClassLoader)NullableWrapperConverters.class.getClassLoader());
    private static final boolean VAVR_PRESENT = ClassUtils.isPresent((String)"io.vavr.control.Option", (ClassLoader)NullableWrapperConverters.class.getClassLoader());
    private static final Set<WrapperType> WRAPPER_TYPES = new HashSet<WrapperType>();
    private static final Set<WrapperType> UNWRAPPER_TYPES = new HashSet<WrapperType>();
    private static final Set<Converter<Object, Object>> UNWRAPPERS = new HashSet<Converter<Object, Object>>();
    private static final Map<Class<?>, Boolean> supportsCache = new ConcurrentReferenceHashMap();

    private NullableWrapperConverters() {
    }

    public static boolean supports(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        return supportsCache.computeIfAbsent(type, key -> {
            for (WrapperType candidate : WRAPPER_TYPES) {
                if (!candidate.getType().isAssignableFrom((Class<?>)key)) continue;
                return true;
            }
            return false;
        });
    }

    public static boolean supportsUnwrapping(Class<?> type) {
        Assert.notNull(type, (String)"Type must not be null!");
        for (WrapperType candidate : UNWRAPPER_TYPES) {
            if (!candidate.getType().isAssignableFrom(type)) continue;
            return true;
        }
        return false;
    }

    public static boolean isSingleValue(Class<?> type) {
        for (WrapperType candidate : WRAPPER_TYPES) {
            if (!candidate.getType().isAssignableFrom(type)) continue;
            return candidate.isSingleValue();
        }
        return false;
    }

    public static void registerConvertersIn(ConverterRegistry registry) {
        Assert.notNull((Object)registry, (String)"ConversionService must not be null!");
        registry.addConverter((GenericConverter)NullableWrapperToJdk8OptionalConverter.INSTANCE);
        if (GUAVA_PRESENT) {
            registry.addConverter((GenericConverter)NullableWrapperToGuavaOptionalConverter.INSTANCE);
        }
        if (SCALA_PRESENT) {
            registry.addConverter((GenericConverter)NullableWrapperToScalaOptionConverter.INSTANCE);
        }
        if (VAVR_PRESENT) {
            registry.addConverter((GenericConverter)NullableWrapperToVavrOptionConverter.INSTANCE);
        }
    }

    @Nullable
    public static Object unwrap(@Nullable Object source) {
        if (source == null || !NullableWrapperConverters.supports(source.getClass())) {
            return source;
        }
        for (Converter<Object, Object> converter : UNWRAPPERS) {
            Object result = converter.convert(source);
            if (result == source) continue;
            return result;
        }
        return source;
    }

    public static TypeInformation<?> unwrapActualType(TypeInformation<?> type) {
        Assert.notNull(type, (String)"type must not be null");
        Class<?> rawType = type.getType();
        boolean needToUnwrap = NullableWrapperConverters.supports(rawType) || Stream.class.isAssignableFrom(rawType);
        return needToUnwrap ? NullableWrapperConverters.unwrapActualType(type.getRequiredComponentType()) : type;
    }

    static {
        WRAPPER_TYPES.add(NullableWrapperToJdk8OptionalConverter.getWrapperType());
        UNWRAPPER_TYPES.add(NullableWrapperToJdk8OptionalConverter.getWrapperType());
        UNWRAPPERS.add(Jdk8OptionalUnwrapper.INSTANCE);
        if (GUAVA_PRESENT) {
            WRAPPER_TYPES.add(NullableWrapperToGuavaOptionalConverter.getWrapperType());
            UNWRAPPER_TYPES.add(NullableWrapperToGuavaOptionalConverter.getWrapperType());
            UNWRAPPERS.add(GuavaOptionalUnwrapper.INSTANCE);
        }
        if (SCALA_PRESENT) {
            WRAPPER_TYPES.add(NullableWrapperToScalaOptionConverter.getWrapperType());
            UNWRAPPER_TYPES.add(NullableWrapperToScalaOptionConverter.getWrapperType());
            UNWRAPPERS.add(ScalOptionUnwrapper.INSTANCE);
        }
        if (VAVR_PRESENT) {
            WRAPPER_TYPES.add(NullableWrapperToVavrOptionConverter.getWrapperType());
            UNWRAPPERS.add(VavrOptionUnwrapper.INSTANCE);
        }
    }

    private static final class WrapperType {
        private final Class<?> type;
        private final Cardinality cardinality;

        private WrapperType(Class<?> type, Cardinality cardinality) {
            this.type = type;
            this.cardinality = cardinality;
        }

        Class<?> getType() {
            return this.type;
        }

        Cardinality getCardinality() {
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
            return "WrapperType(type=" + this.getType() + ", cardinality=" + (Object)((Object)this.getCardinality()) + ")";
        }

        static WrapperType singleValue(Class<?> type) {
            return new WrapperType(type, Cardinality.SINGLE);
        }

        static WrapperType multiValue(Class<?> type) {
            return new WrapperType(type, Cardinality.MULTI);
        }

        static WrapperType noValue(Class<?> type) {
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

    private static enum VavrOptionUnwrapper implements Converter<Object, Object>
    {
        INSTANCE;


        @Nullable
        public Object convert(Object source) {
            if (source instanceof io.vavr.control.Option) {
                return ((io.vavr.control.Option)source).getOrElse(() -> null);
            }
            return source;
        }
    }

    private static enum ScalOptionUnwrapper implements Converter<Object, Object>
    {
        INSTANCE;

        private final Function0<Object> alternative = new AbstractFunction0<Object>(){

            @Nullable
            public Option<Object> apply() {
                return null;
            }
        };

        @Nullable
        public Object convert(Object source) {
            return source instanceof Option ? ((Option)source).getOrElse(this.alternative) : source;
        }
    }

    private static enum Jdk8OptionalUnwrapper implements Converter<Object, Object>
    {
        INSTANCE;


        @Nullable
        public Object convert(Object source) {
            return source instanceof Optional ? ((Optional)source).orElse(null) : source;
        }
    }

    private static enum GuavaOptionalUnwrapper implements Converter<Object, Object>
    {
        INSTANCE;


        @Nullable
        public Object convert(Object source) {
            return source instanceof com.google.common.base.Optional ? ((com.google.common.base.Optional)source).orNull() : source;
        }
    }

    private static class NullableWrapperToVavrOptionConverter
    extends AbstractWrapperTypeConverter {
        public static final NullableWrapperToVavrOptionConverter INSTANCE = new NullableWrapperToVavrOptionConverter();

        private NullableWrapperToVavrOptionConverter() {
            super(io.vavr.control.Option.none(), Collections.singleton(io.vavr.control.Option.class));
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(io.vavr.control.Option.class);
        }

        @Override
        protected Object wrap(Object source) {
            return io.vavr.control.Option.of((Object)source);
        }
    }

    private static class NullableWrapperToScalaOptionConverter
    extends AbstractWrapperTypeConverter {
        public static final NullableWrapperToScalaOptionConverter INSTANCE = new NullableWrapperToScalaOptionConverter();

        private NullableWrapperToScalaOptionConverter() {
            super(Option.empty(), Collections.singleton(Option.class));
        }

        @Override
        protected Object wrap(Object source) {
            return Option.apply((Object)source);
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(Option.class);
        }
    }

    private static class NullableWrapperToGuavaOptionalConverter
    extends AbstractWrapperTypeConverter {
        public static final NullableWrapperToGuavaOptionalConverter INSTANCE = new NullableWrapperToGuavaOptionalConverter();

        private NullableWrapperToGuavaOptionalConverter() {
            super(com.google.common.base.Optional.absent(), Collections.singleton(com.google.common.base.Optional.class));
        }

        @Override
        protected Object wrap(Object source) {
            return com.google.common.base.Optional.of((Object)source);
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(com.google.common.base.Optional.class);
        }
    }

    private static class NullableWrapperToJdk8OptionalConverter
    extends AbstractWrapperTypeConverter {
        public static final NullableWrapperToJdk8OptionalConverter INSTANCE = new NullableWrapperToJdk8OptionalConverter();

        private NullableWrapperToJdk8OptionalConverter() {
            super(Optional.empty());
        }

        @Override
        protected Object wrap(Object source) {
            return Optional.of(source);
        }

        public static WrapperType getWrapperType() {
            return WrapperType.singleValue(Optional.class);
        }
    }

    private static abstract class AbstractWrapperTypeConverter
    implements GenericConverter {
        private final Object nullValue;
        private final Iterable<Class<?>> wrapperTypes;

        protected AbstractWrapperTypeConverter(Object nullValue) {
            Assert.notNull((Object)nullValue, (String)"Null value must not be null!");
            this.nullValue = nullValue;
            this.wrapperTypes = Collections.singleton(nullValue.getClass());
        }

        public AbstractWrapperTypeConverter(Object nullValue, Iterable<Class<?>> wrapperTypes) {
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
}

