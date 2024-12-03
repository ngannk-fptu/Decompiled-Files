/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.enums;

import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumSingleValueConverter
extends AbstractSingleValueConverter {
    private final Class<? extends Enum> enumType;

    public EnumSingleValueConverter(Class<? extends Enum> type) {
        if (!Enum.class.isAssignableFrom(type) && type != Enum.class) {
            throw new IllegalArgumentException("Converter can only handle defined enums");
        }
        this.enumType = type;
    }

    @Override
    public boolean canConvert(Class type) {
        return type != null && this.enumType.isAssignableFrom(type);
    }

    @Override
    public String toString(Object obj) {
        return ((Enum)Enum.class.cast(obj)).name();
    }

    @Override
    public Object fromString(String str) {
        Enum result = Enum.valueOf(this.enumType, str);
        return result;
    }
}

