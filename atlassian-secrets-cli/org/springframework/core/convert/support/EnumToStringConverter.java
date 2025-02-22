/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.AbstractConditionalEnumConverter;

final class EnumToStringConverter
extends AbstractConditionalEnumConverter
implements Converter<Enum<?>, String> {
    public EnumToStringConverter(ConversionService conversionService) {
        super(conversionService);
    }

    @Override
    public String convert(Enum<?> source) {
        return source.name();
    }
}

