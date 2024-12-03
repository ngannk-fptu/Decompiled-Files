/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.math.BigDecimal;

public class BigDecimalConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == BigDecimal.class;
    }

    public Object fromString(String str) {
        return new BigDecimal(str);
    }
}

