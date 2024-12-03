/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class IntConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Integer.TYPE || type == Integer.class;
    }

    public Object fromString(String str) {
        long value = Long.decode(str);
        if (value < Integer.MIN_VALUE || value > 0xFFFFFFFFL) {
            throw new NumberFormatException("For input string: \"" + str + '\"');
        }
        return new Integer((int)value);
    }
}

