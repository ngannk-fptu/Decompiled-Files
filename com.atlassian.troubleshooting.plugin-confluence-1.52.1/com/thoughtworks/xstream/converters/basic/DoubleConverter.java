/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class DoubleConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == Double.TYPE || type == Double.class;
    }

    public Object fromString(String str) {
        return Double.valueOf(str);
    }
}

