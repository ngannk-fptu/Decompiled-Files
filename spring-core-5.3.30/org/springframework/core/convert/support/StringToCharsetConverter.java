/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.nio.charset.Charset;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

class StringToCharsetConverter
implements Converter<String, Charset> {
    StringToCharsetConverter() {
    }

    @Override
    public Charset convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Charset.forName(source);
    }
}

