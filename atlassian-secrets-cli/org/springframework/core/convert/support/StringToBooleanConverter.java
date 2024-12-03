/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.core.convert.support;

import java.util.HashSet;
import java.util.Set;
import org.springframework.core.convert.converter.Converter;

final class StringToBooleanConverter
implements Converter<String, Boolean> {
    private static final Set<String> trueValues = new HashSet<String>(4);
    private static final Set<String> falseValues = new HashSet<String>(4);

    StringToBooleanConverter() {
    }

    @Override
    public Boolean convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (trueValues.contains(value = value.toLowerCase())) {
            return Boolean.TRUE;
        }
        if (falseValues.contains(value)) {
            return Boolean.FALSE;
        }
        throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
    }

    static {
        trueValues.add("true");
        trueValues.add("on");
        trueValues.add("yes");
        trueValues.add("1");
        falseValues.add("false");
        falseValues.add("off");
        falseValues.add("no");
        falseValues.add("0");
    }
}

