/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.vavr.collection.LinkedHashMap
 *  io.vavr.collection.LinkedHashSet
 *  io.vavr.collection.List
 *  io.vavr.collection.Map
 *  io.vavr.collection.Seq
 *  io.vavr.collection.Set
 *  io.vavr.collection.Traversable
 *  javax.annotation.Nonnull
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.ConditionalGenericConverter
 *  org.springframework.core.convert.converter.Converter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.lang.Nullable
 */
package org.springframework.data.repository.util;

import io.vavr.collection.LinkedHashMap;
import io.vavr.collection.LinkedHashSet;
import io.vavr.collection.Map;
import io.vavr.collection.Seq;
import io.vavr.collection.Set;
import io.vavr.collection.Traversable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import javax.annotation.Nonnull;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.repository.util.QueryExecutionConverters;
import org.springframework.lang.Nullable;

class VavrCollections {
    VavrCollections() {
    }

    public static enum FromJavaConverter implements ConditionalGenericConverter
    {
        INSTANCE{

            @Nonnull
            public java.util.Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
                return CONVERTIBLE_PAIRS;
            }

            public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
                if (sourceType.isCollection() && Map.class.isAssignableFrom(targetType.getType())) {
                    return false;
                }
                return !sourceType.isMap() || Map.class.isAssignableFrom(targetType.getType()) || targetType.getType().equals(Traversable.class);
            }

            @Nullable
            public Object convert(@Nullable Object source, TypeDescriptor sourceDescriptor, TypeDescriptor targetDescriptor) {
                Class targetType = targetDescriptor.getType();
                if (Seq.class.isAssignableFrom(targetType)) {
                    return io.vavr.collection.List.ofAll((Iterable)((Iterable)source));
                }
                if (Set.class.isAssignableFrom(targetType)) {
                    return LinkedHashSet.ofAll((Iterable)((Iterable)source));
                }
                if (Map.class.isAssignableFrom(targetType)) {
                    return LinkedHashMap.ofAll((java.util.Map)((java.util.Map)source));
                }
                if (source instanceof List) {
                    return io.vavr.collection.List.ofAll((Iterable)((Iterable)source));
                }
                if (source instanceof java.util.Set) {
                    return LinkedHashSet.ofAll((Iterable)((Iterable)source));
                }
                if (source instanceof java.util.Map) {
                    return LinkedHashMap.ofAll((java.util.Map)((java.util.Map)source));
                }
                return source;
            }
        };

        private static final java.util.Set<GenericConverter.ConvertiblePair> CONVERTIBLE_PAIRS;

        static {
            HashSet<GenericConverter.ConvertiblePair> pairs = new HashSet<GenericConverter.ConvertiblePair>();
            pairs.add(new GenericConverter.ConvertiblePair(Collection.class, Traversable.class));
            pairs.add(new GenericConverter.ConvertiblePair(java.util.Map.class, Traversable.class));
            CONVERTIBLE_PAIRS = Collections.unmodifiableSet(pairs);
        }
    }

    public static enum ToJavaConverter implements Converter<Object, Object>
    {
        INSTANCE;


        public QueryExecutionConverters.WrapperType getWrapperType() {
            return QueryExecutionConverters.WrapperType.multiValue(Traversable.class);
        }

        @Nonnull
        public Object convert(Object source) {
            if (source instanceof Seq) {
                return ((Seq)source).asJava();
            }
            if (source instanceof Map) {
                return ((Map)source).toJavaMap();
            }
            if (source instanceof Set) {
                return ((Set)source).toJavaSet();
            }
            throw new IllegalArgumentException("Unsupported Vavr collection " + source.getClass());
        }
    }
}

