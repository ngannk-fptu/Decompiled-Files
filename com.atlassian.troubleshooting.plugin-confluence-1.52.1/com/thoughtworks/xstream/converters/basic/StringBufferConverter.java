/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

public class StringBufferConverter
extends AbstractSingleValueConverter {
    public Object fromString(String str) {
        return new StringBuffer(str);
    }

    public boolean canConvert(Class type) {
        return type == StringBuffer.class;
    }
}

