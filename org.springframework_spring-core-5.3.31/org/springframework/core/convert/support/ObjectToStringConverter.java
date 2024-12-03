/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

final class ObjectToStringConverter
implements Converter<Object, String> {
    ObjectToStringConverter() {
    }

    @Override
    public String convert(Object source) {
        return source.toString();
    }
}

