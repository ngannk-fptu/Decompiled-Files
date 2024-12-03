/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class FloatConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Float.TYPE || type == Float.class;
    }

    public Object fromString(String str) {
        return Float.valueOf(str);
    }
}

