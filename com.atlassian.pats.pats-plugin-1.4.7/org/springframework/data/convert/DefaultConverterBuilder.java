/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.TypeDescriptor
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.convert;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ConverterBuilder;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.util.Optionals;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

class DefaultConverterBuilder<S, T>
implements ConverterBuilder.ConverterAware,
ConverterBuilder.ReadingConverterBuilder<T, S>,
ConverterBuilder.WritingConverterBuilder<S, T> {
    private final GenericConverter.ConvertiblePair convertiblePair;
    private final Optional<Function<? super S, ? extends T>> writing;
    private final Optional<Function<? super T, ? extends S>> reading;

    DefaultConverterBuilder(GenericConverter.ConvertiblePair convertiblePair, Optional<Function<? super S, ? extends T>> writing, Optional<Function<? super T, ? extends S>> reading) {
        this.convertiblePair = convertiblePair;
        this.writing = writing;
        this.reading = reading;
    }

    @Override
    public ConverterBuilder.ConverterAware andReading(Function<? super T, ? extends S> function) {
        return this.withReading(Optional.of(function));
    }

    @Override
    public ConverterBuilder.ConverterAware andWriting(Function<? super S, ? extends T> function) {
        return this.withWriting(Optional.of(function));
    }

    @Override
    public GenericConverter getReadingConverter() {
        return this.getOptionalReadingConverter().orElseThrow(() -> new IllegalStateException("No reading converter specified!"));
    }

    @Override
    public GenericConverter getWritingConverter() {
        return this.getOptionalWritingConverter().orElseThrow(() -> new IllegalStateException("No writing converter specified!"));
    }

    @Override
    public Set<GenericConverter> getConverters() {
        return Optionals.toStream(this.getOptionalReadingConverter(), this.getOptionalWritingConverter()).collect(Collectors.toSet());
    }

    private Optional<GenericConverter> getOptionalReadingConverter() {
        return this.reading.map(it -> new ConfigurableGenericConverter.Reading(this.convertiblePair, it));
    }

    private Optional<GenericConverter> getOptionalWritingConverter() {
        return this.writing.map(it -> new ConfigurableGenericConverter.Writing(this.invertedPair(), it));
    }

    private GenericConverter.ConvertiblePair invertedPair() {
        return new GenericConverter.ConvertiblePair(this.convertiblePair.getTargetType(), this.convertiblePair.getSourceType());
    }

    DefaultConverterBuilder<S, T> withWriting(Optional<Function<? super S, ? extends T>> writing) {
        return this.writing == writing ? this : new DefaultConverterBuilder<S, T>(this.convertiblePair, writing, this.reading);
    }

    DefaultConverterBuilder<S, T> withReading(Optional<Function<? super T, ? extends S>> reading) {
        return this.reading == reading ? this : new DefaultConverterBuilder<S, T>(this.convertiblePair, this.writing, reading);
    }

    private static class ConfigurableGenericConverter<S, T>
    implements GenericConverter {
        private final GenericConverter.ConvertiblePair convertiblePair;
        private final Function<? super S, ? extends T> function;

        public ConfigurableGenericConverter(GenericConverter.ConvertiblePair convertiblePair, Function<? super S, ? extends T> function) {
            this.convertiblePair = convertiblePair;
            this.function = function;
        }

        @Nullable
        public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
            return this.function.apply(source);
        }

        public Set<GenericConverter.ConvertiblePair> getConvertibleTypes() {
            return Collections.singleton(this.convertiblePair);
        }

        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ConfigurableGenericConverter)) {
                return false;
            }
            ConfigurableGenericConverter that = (ConfigurableGenericConverter)o;
            if (!ObjectUtils.nullSafeEquals((Object)this.convertiblePair, (Object)that.convertiblePair)) {
                return false;
            }
            return ObjectUtils.nullSafeEquals(this.function, that.function);
        }

        public int hashCode() {
            int result = ObjectUtils.nullSafeHashCode((Object)this.convertiblePair);
            result = 31 * result + ObjectUtils.nullSafeHashCode(this.function);
            return result;
        }

        @ReadingConverter
        private static class Reading<S, T>
        extends ConfigurableGenericConverter<S, T> {
            Reading(GenericConverter.ConvertiblePair convertiblePair, Function<? super S, ? extends T> function) {
                super(convertiblePair, function);
            }
        }

        @WritingConverter
        private static class Writing<S, T>
        extends ConfigurableGenericConverter<S, T> {
            Writing(GenericConverter.ConvertiblePair convertiblePair, Function<? super S, ? extends T> function) {
                super(convertiblePair, function);
            }
        }
    }
}

