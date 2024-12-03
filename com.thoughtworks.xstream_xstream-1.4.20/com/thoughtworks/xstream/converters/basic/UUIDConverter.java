/*
 * Decompiled with CFR 0.152.
 */
package com.thoughtworks.xstream.converters.basic;

import com.thoughtworks.xstream.converters.ConversionException;
import com.thoughtworks.xstream.converters.basic.AbstractSingleValueConverter;
import java.util.UUID;

public class UUIDConverter
extends AbstractSingleValueConverter {
    public boolean canConvert(Class type) {
        return type == UUID.class;
    }

    public Object fromString(String str) {
        try {
            return UUID.fromString(str);
        }
        catch (IllegalArgumentException e) {
            throw new ConversionException("Cannot create UUID instance", e);
        }
    }
}

