/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class ShortConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Short.TYPE || type == Short.class;
    }

    public Object fromString(String str) {
        int value = Integer.decode(str);
        if (value < Short.MIN_VALUE || value > 65535) {
            throw new NumberFormatException("For input string: \"" + str + '\"');
        }
        return new Short((short)value);
    }
}

