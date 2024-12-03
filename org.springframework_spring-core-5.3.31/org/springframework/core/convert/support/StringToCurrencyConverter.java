/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.util.Currency;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

class StringToCurrencyConverter
implements Converter<String, Currency> {
    StringToCurrencyConverter() {
    }

    @Override
    public Currency convert(String source) {
        if (StringUtils.hasText(source)) {
            source = source.trim();
        }
        return Currency.getInstance(source);
    }
}

