/*
 * Decompiled with CFR 0.152.
 */
package com.twelvemonkeys.util.convert;

import com.twelvemonkeys.util.convert.ConversionException;

public class TypeMismathException
extends ConversionException {
    public TypeMismathException(Class clazz) {
        super("Wrong type for conversion: " + clazz.getName());
    }
}

