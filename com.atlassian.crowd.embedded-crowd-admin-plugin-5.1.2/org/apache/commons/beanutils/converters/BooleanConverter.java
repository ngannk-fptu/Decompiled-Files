/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.beanutils.converters;

import org.apache.commons.beanutils.converters.AbstractConverter;

public final class BooleanConverter
extends AbstractConverter {
    @Deprecated
    public static final Object NO_DEFAULT = new Object();
    private String[] trueStrings = new String[]{"true", "yes", "y", "on", "1"};
    private String[] falseStrings = new String[]{"false", "no", "n", "off", "0"};

    public BooleanConverter() {
    }

    public BooleanConverter(Object defaultValue) {
        if (defaultValue != NO_DEFAULT) {
            this.setDefaultValue(defaultValue);
        }
    }

    public BooleanConverter(String[] trueStrings, String[] falseStrings) {
        this.trueStrings = BooleanConverter.copyStrings(trueStrings);
        this.falseStrings = BooleanConverter.copyStrings(falseStrings);
    }

    public BooleanConverter(String[] trueStrings, String[] falseStrings, Object defaultValue) {
        this.trueStrings = BooleanConverter.copyStrings(trueStrings);
        this.falseStrings = BooleanConverter.copyStrings(falseStrings);
        if (defaultValue != NO_DEFAULT) {
            this.setDefaultValue(defaultValue);
        }
    }

    protected Class<Boolean> getDefaultType() {
        return Boolean.class;
    }

    @Override
    protected <T> T convertToType(Class<T> type, Object value) throws Throwable {
        if (Boolean.class.equals(type) || Boolean.TYPE.equals(type)) {
            String stringValue = value.toString().toLowerCase();
            for (String trueString : this.trueStrings) {
                if (!trueString.equals(stringValue)) continue;
                return type.cast(Boolean.TRUE);
            }
            for (String falseString : this.falseStrings) {
                if (!falseString.equals(stringValue)) continue;
                return type.cast(Boolean.FALSE);
            }
        }
        throw this.conversionException(type, value);
    }

    private static String[] copyStrings(String[] src) {
        String[] dst = new String[src.length];
        for (int i = 0; i < src.length; ++i) {
            dst[i] = src[i].toLowerCase();
        }
        return dst;
    }
}

