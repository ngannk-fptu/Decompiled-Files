/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

final class NumberToCharacterConverter
implements Converter<Number, Character> {
    NumberToCharacterConverter() {
    }

    @Override
    public Character convert(Number source) {
        return Character.valueOf((char)source.shortValue());
    }
}

