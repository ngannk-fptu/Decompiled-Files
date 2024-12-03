/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class BooleanConverter
extends AbstractSingleValueConverter {
    public static final BooleanConverter TRUE_FALSE = new BooleanConverter("true", "false", false);
    public static final BooleanConverter YES_NO = new BooleanConverter("yes", "no", false);
    public static final BooleanConverter BINARY = new BooleanConverter("1", "0", true);
    private final String positive;
    private final String negative;
    private final boolean caseSensitive;

    public BooleanConverter(String positive, String negative, boolean caseSensitive) {
        this.positive = positive;
        this.negative = negative;
        this.caseSensitive = caseSensitive;
    }

    public BooleanConverter() {
        this("true", "false", false);
    }

    public boolean shouldConvert(Class type, Object value) {
        return true;
    }

    public boolean canConvert(Class type) {
        return type == Boolean.TYPE || type == Boolean.class;
    }

    public Object fromString(String str) {
        if (this.caseSensitive) {
            return this.positive.equals(str) ? Boolean.TRUE : Boolean.FALSE;
        }
        return this.positive.equalsIgnoreCase(str) ? Boolean.TRUE : Boolean.FALSE;
    }

    public String toString(Object obj) {
        Boolean value = (Boolean)obj;
        return obj == null ? null : (value != false ? this.positive : this.negative);
    }
}

