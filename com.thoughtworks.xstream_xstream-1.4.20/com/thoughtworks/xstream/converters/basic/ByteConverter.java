/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class ByteConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Byte.TYPE || type == Byte.class;
    }

    public Object fromString(String str) {
        int value = Integer.decode(str);
        if (value < -128 || value > 255) {
            throw new NumberFormatException("For input string: \"" + str + '\"');
        }
        return new Byte((byte)value);
    }
}

