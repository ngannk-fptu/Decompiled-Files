/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.convert.converter.GenericConverter
 *  org.springframework.core.convert.converter.GenericConverter$ConvertiblePair
 *  org.springframework.util.Assert
 */
package org.springframework.data.convert;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.DefaultConverterBuilder;
import org.springframework.util.Assert;

public interface ConverterBuilder {
    public static <S, T> ReadingConverterBuilder<S, T> reading(Class<S> source, Class<T> target, Function<? super S, ? extends T> function) {
        Assert.notNull(source, (String)"Source type must not be null!");
        Assert.notNull(target, (String)"Target type must not be null!");
        Assert.notNull(function, (String)"Conversion function must not be null!");
        return new DefaultConverterBuilder(new GenericConverter.ConvertiblePair(source, target), Optional.empty(), Optional.of(function));
    }

    public static <S, T> WritingConverterBuilder<S, T> writing(Class<S> source, Class<T> target, Function<? super S, ? extends T> function) {
        Assert.notNull(source, (String)"Source type must not be null!");
        Assert.notNull(target, (String)"Target type must not be null!");
        Assert.notNull(function, (String)"Conversion function must not be null!");
        return new DefaultConverterBuilder(new GenericConverter.ConvertiblePair(target, source), Optional.of(function), Optional.empty());
    }

    public Set<GenericConverter> getConverters();

    public static interface ConverterAware
    extends ConverterBuilder,
    ReadingConverterAware,
    WritingConverterAware {
    }

    public static interface WritingConverterBuilder<S, T>
    extends ConverterBuilder,
    WritingConverterAware {
        public ConverterAware andReading(Function<? super T, ? extends S> var1);
    }

    public static interface ReadingConverterBuilder<T, S>
    extends ConverterBuilder,
    ReadingConverterAware {
        public ConverterAware andWriting(Function<? super S, ? extends T> var1);
    }

    public static interface ReadingConverterAware {
        public GenericConverter getReadingConverter();
    }

    public static interface WritingConverterAware {
        public GenericConverter getWritingConverter();
    }
}

